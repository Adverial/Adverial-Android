package com.application.adverial.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.application.adverial.remote.model.Conversation
import com.application.adverial.remote.model.ConversationResponse
import com.application.adverial.remote.model.GenericResponse
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.service.Tools
import okhttp3.RequestBody




class ConversationRepository(val context: Context) {

    private val apiService: APIService = RetroClassV2().apiService()
    private val token = "Bearer " + context.getSharedPreferences("user", 0).getString("token", "")
    private val lang = Tools().getCurrentLanguage(context)

    val initialConversationLiveData = MutableLiveData<ConversationResponse>()
    val userConversationsLiveData = MutableLiveData<List<Conversation>>()
    val sendMessageLiveData = MutableLiveData<MessageResponse>()
    val messagesLiveData = MutableLiveData<List<Message>>()

    fun initialConversation(partnerUserId: Int) {
        val call = apiService.initialConversation(partnerUserId, token, lang)
        call.enqueue(object : Callback<ConversationResponse> {
            override fun onResponse(call: Call<ConversationResponse>, response: Response<ConversationResponse>) {
                if (response.isSuccessful) {
                    initialConversationLiveData.value = response.body()
                }
            }

            override fun onFailure(call: Call<ConversationResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    fun getUserConversations() {
        val call = apiService.getUserConversations(token, "application/json", lang)
        call.enqueue(object : Callback<List<Conversation>> {
            override fun onResponse(call: Call<List<Conversation>>, response: Response<List<Conversation>>) {
                if (response.isSuccessful) {
                    userConversationsLiveData.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                // Handle error
            }
        })
    }

    fun sendMessage(conversionId: Int, message: String, media: RequestBody?) {
        val call = apiService.sendMessage(conversionId, token, "multipart/form-data", lang, message, media)
        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    sendMessageLiveData.value = response.body()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    fun getMessagesByConversationId(conversionId: Int) {
        val call = apiService.getMessagesByConversationId(conversionId, token, "multipart/form-data", lang)
        call.enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if (response.isSuccessful) {
                    messagesLiveData.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                // Handle error
            }
        })
    }
    fun getUserConversationsResponse(): LiveData<List<Conversation>> {
        return userConversationsLiveData
    }
    fun getSendMessageResponse(): LiveData<MessageResponse> {
        return sendMessageLiveData
    }
    fun getMessagesResponse(): LiveData<List<Message>> {
        return messagesLiveData
    }
    fun getInitialConversationResponse(): LiveData<ConversationResponse> {
        return initialConversationLiveData
    }
}

