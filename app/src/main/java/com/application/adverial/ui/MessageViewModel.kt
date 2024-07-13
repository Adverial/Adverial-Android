package com.application.adverial.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.application.adverial.remote.ConversationRepository
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import okhttp3.RequestBody

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ConversationRepository = ConversationRepository(application)

    fun getMessagesResponse(): LiveData<List<Message>> = repository.getMessagesResponse()

    fun getSendMessageResponse(): LiveData<MessageResponse> = repository.getSendMessageResponse()

    fun loadMessagesByConversationId(conversationId: Int) {
        repository.getMessagesByConversationId(conversationId)
    }

    fun sendMessage(conversationId: Int, message: String, media: RequestBody?) {
        repository.sendMessage(conversationId, message, media)
    }
}
