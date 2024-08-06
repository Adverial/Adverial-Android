package com.application.adverial.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.adverial.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.application.adverial.remote.model.Conversation
import com.application.adverial.remote.model.ConversationResponse
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.service.Tools
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


class ConversationRepository(val context: Context) {

    private val apiService: APIService = RetroClassV2().apiService()
    private val token = "Bearer " + context.getSharedPreferences("user", 0).getString("token", "")
    private val lang = Tools().getCurrentLanguage(context)

    val initialConversationLiveData = MutableLiveData<ConversationResponse>()
    val userConversationsLiveData = MutableLiveData<List<Conversation>>()
    val sendMessageLiveData = MutableLiveData<MessageResponse>()
    val messagesLiveData = MutableLiveData<List<Message>>()

   fun initialConversation(partnerUserId: Int, adId: Int?) {
    val call = apiService.initialConversation(partnerUserId, adId, token, lang)
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
        val client = OkHttpClient()

        // Build the multipart body
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("message", message)

        // Add media if present
        media?.let {
            requestBodyBuilder.addFormDataPart("media", "media", it)
        }

        val requestBody = requestBodyBuilder.build()

        // Build the request
        val request = Request.Builder()
            .url( BuildConfig.API_BASE_URL+ "api/v2/send-message/$conversionId")
            .post(requestBody)
            .addHeader("Authorization", token)
            .addHeader("lang", lang)
            .build()

        // Send the request
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("MessageRepository", "Failed to send message", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body().toString()
               // Log.i("MessageRepository", "Response: $body")
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

