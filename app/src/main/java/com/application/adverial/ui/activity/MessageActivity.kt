package com.application.adverial.ui.activity


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
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
    private var selectedMediaUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        buttonAddMedia.setOnClickListener {
            // Check for storage permissions and open media picker
            if (checkStoragePermission()) {
                openMediaPicker()
            } else {
                requestStoragePermission()
            }
        }
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
                editTextMessage.text.clear()
            } else {
                Toast.makeText(this, "Message is required", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Message sent successfully.", Toast.LENGTH_SHORT).show()
            Log.i("Pusher", "Response: $response")
//            response.message?.let { message as Message ->
//                messageAdapter.addMessage(message)
//                recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
//            }
        }
    }


    private fun setupPusher(conversationId: Int) {


        val options = PusherOptions().setCluster("us2")
        val pusher = Pusher("0f97d1f616126b909ce3", options)

        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {

             //   Log.i("Pusher", "State changed from ${change.previousState} to ${change.currentState}")
            }
            override fun onError(
                message: String,
                code: String,
                e: Exception
            ) {
              //  Log.i("Pusher", "There was a problem connecting! code ($code), message ($message), exception($e)")
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribe("chat.$conversationId")
        channel.bind("message.sent") { event ->
//            Log.i("Pusher","Received event with data: $event")
            val jsonObject = JSONObject(event.data)
            //log jsonObject
//            Log.i("Pusher", "Received event with data: $jsonObject")
          //  2024-07-14 10:30:23.967 28738-28904 Pusher                  com.application.adverial             I  Received event with data: {"message":{"message":"Hello facebook","chat_room_id":"2","user_id":8,"updated_at":"2024-07-14T07:30:23.000000Z","created_at":"2024-07-14T07:30:23.000000Z","id":67,"user":{"id":8,"name":"ali","last_name":"","email":"9aeXYRBTFu@gmail.com","whatsapp_number":"+9647508961058","email_verified_at":null,"birth_date":null,"photo":"uploads\/user\/default.png","phone":null,"is_verified":1,"is_store":0,"status":0,"created_at":"2024-07-03T08:20:35.000000Z","updated_at":"2024-07-14T06:44:20.000000Z","type":null}},"user":{"id":8,"name":"ali","last_name":"","email":"9aeXYRBTFu@gmail.com","whatsapp_number":"+9647508961058","email_verified_at":null,"birth_date":null,"photo":"uploads\/user\/default.png","phone":null,"is_verified":1,"is_store":0,"status":0,"created_at":"2024-07-03T08:20:35.000000Z","updated_at":"2024-07-14T06:44:20.000000Z","type":null}}

            val message = jsonObject.getJSONObject("message").getString("message")
            val senderId = jsonObject.getJSONObject("user").getInt("id")
            val mediaUrl = jsonObject.getJSONObject("message").optString("media_url", null)
            val createdAt = jsonObject.getJSONObject("message").getString("created_at")
            val messageId = jsonObject.getJSONObject("message").getInt("id")
            val newMessage = Message(
                messageId = messageId,
                conversionId = conversationId,
                message = message,
                mediaUrl = mediaUrl,
                createdAt = createdAt,
                senderId = senderId
            )
            // log newMessage
//            Log.i("Pusher", "newMessage: $newMessage")
            try{
                runOnUiThread {
                messageAdapter.addMessage(newMessage)
                recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
            }
            }catch (e: Exception){
                Log.i("Pusher", "Error: $e")
            }
        }


    }
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
    }

    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_MEDIA_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PICK && resultCode == Activity.RESULT_OK) {
//            selectedMediaUri = data?.data.toString()
//            imageViewMediaPreview.setImageURI(selectedMediaUri)
//            imageViewMediaPreview.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val REQUEST_MEDIA_PICK = 1
        private const val REQUEST_STORAGE_PERMISSION = 2
    }

}
