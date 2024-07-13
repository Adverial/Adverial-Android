package com.application.adverial.ui.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.service.Tools
import com.application.adverial.ui.MessageAdapter
import com.application.adverial.ui.MessageViewModel
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.SubscriptionEventListener
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_my_account.countryCodePicker
import kotlinx.android.synthetic.main.activity_my_account.myaccount_email
import kotlinx.android.synthetic.main.activity_my_account.myaccount_firstname
import kotlinx.android.synthetic.main.activity_my_account.myaccount_lastname
import kotlinx.android.synthetic.main.activity_my_account.myaccount_phone
import kotlinx.android.synthetic.main.activity_signup.lottie14
import okhttp3.RequestBody
import org.json.JSONObject

class MessageActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageViewModel: MessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val conversationId = intent.getIntExtra("conversation_id", -1)
        val chatPartnerName = intent.getStringExtra("chat_partner_name") ?: "Chat Partner"
        if (conversationId == -1) {
            finish()
            return
        }

        textViewChatPartnerName.text = chatPartnerName

        buttonBack.setOnClickListener {
            finish()
        }

        // Set up RecyclerView
        messageAdapter = MessageAdapter()

        val repo= Repository(this)
        repo.user()
        repo.getUserData().observe(this) {
            if (it.status) {
                it.data.id?.let { it1 -> messageAdapter.setCurrentUserId(it1) }
            }
        }
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messageAdapter
        // Set up ViewModel
        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)

        // Observe LiveData from ViewModel
        messageViewModel.getMessagesResponse().observe(this, Observer { messages ->
            messageAdapter.setMessages(messages)
        })

        // Load messages
        messageViewModel.loadMessagesByConversationId(conversationId)

        // Handle send button click
        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {

                messageViewModel.sendMessage(conversationId, message, null)
//                messageViewModel.getMessagesResponse().observe(this, Observer { messages ->
//                    messageAdapter.setMessages(messages)
//                })
                editTextMessage.text.clear()
            }
        }

        // Observe send message response
        messageViewModel.getSendMessageResponse().observe(this, Observer { response ->
            handleSendMessageResponse(response)
        })

        // Set up Pusher
        setupPusher(conversationId)
    }

    private fun handleSendMessageResponse(response: MessageResponse?) {
        if (response == null) {
            Toast.makeText(this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show()
        } else {
            // Successfully sent message, update UI or do additional actions if needed
        }
    }

    private fun setupPusher(conversationId: Int) {
        val options = PusherOptions().setCluster("us2")
        val pusher = Pusher("0f97d1f616126b909ce3", options)

        val channel = pusher.subscribe("chat.$conversationId")

        channel.bind("message.sent") { event ->
            Log.e("PUSHER", event.data)
            val jsonObject = JSONObject(event.data)
            val message = jsonObject.getJSONObject("message").getString("message")
            val senderId = jsonObject.getJSONObject("user").getInt("id")
            val mediaUrl = jsonObject.getJSONObject("message").optString("media_url", null)
            val newMessage = Message(
                messageId = 0, // Assume 0 for simplicity
                conversionId = conversationId,
                message = message,
                mediaUrl = mediaUrl,
                createdAt = "", // Provide correct timestamp
                senderId = senderId
            )
            runOnUiThread {
                messageAdapter.addMessage(newMessage)
                recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
            }
        }

        pusher.connect()
    }

}
