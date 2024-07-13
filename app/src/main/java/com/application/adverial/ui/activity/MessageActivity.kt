package com.application.adverial.ui.activity

import com.application.adverial.ui.MessageViewModel
import com.application.adverial.ui.adapter.MessageAdapter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.model.MessageResponse
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageViewModel: MessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val conversationId = intent.getIntExtra("conversation_id", -1)
        if (conversationId == -1) {
            finish()
            return
        }

        // Set up RecyclerView
        messageAdapter = MessageAdapter()
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
            }
        }

        // Observe send message response
        messageViewModel.getSendMessageResponse().observe(this, Observer { response ->
            handleSendMessageResponse(response)
        })
    }

    private fun handleSendMessageResponse(response: MessageResponse?) {
        if (response == null) {
            Toast.makeText(this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show()
        } else {
            // Handle success response

        }
    }
}
