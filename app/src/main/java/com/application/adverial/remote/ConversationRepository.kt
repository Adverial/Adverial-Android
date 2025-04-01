package com.application.adverial.remote

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.adverial.BuildConfig
import com.application.adverial.remote.model.Conversation
import com.application.adverial.remote.model.ConversationResponse
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.service.Tools
import java.io.IOException
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        call.enqueue(
                object : Callback<ConversationResponse> {
                    override fun onResponse(
                            call: Call<ConversationResponse>,
                            response: Response<ConversationResponse>
                    ) {
                        if (response.isSuccessful) {
                            initialConversationLiveData.value = response.body()
                        }
                    }

                    override fun onFailure(call: Call<ConversationResponse>, t: Throwable) {
                        // Handle error
                    }
                }
        )
    }

    fun getUserConversations() {
        val call = apiService.getUserConversations(token, "application/json", lang)
        call.enqueue(
                object : Callback<List<Conversation>> {
                    override fun onResponse(
                            call: Call<List<Conversation>>,
                            response: Response<List<Conversation>>
                    ) {
                        if (response.isSuccessful) {
                            userConversationsLiveData.value = response.body()
                        }
                    }

                    override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                        // Handle error
                    }
                }
        )
    }

    fun sendMessage(
            conversionId: Int,
            message: String,
            media: RequestBody?,
            voice: RequestBody? = null
    ) {
        try {
            // Create a new multipart builder
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

            // Add message part
            if (message.isNotEmpty()) {
                builder.addFormDataPart("message", message)
            }

            // For debugging
            val parts = mutableListOf<String>()
            parts.add("message=${message.isNotEmpty()}")

            // Add media file if present
            if (media != null) {
                try {
                    builder.addFormDataPart("media", "image.jpg", media)
                    parts.add("media=true")
                } catch (e: Exception) {
                    Log.e("ConversationRepository", "Error adding media: ${e.message}")
                }
            }

            // Add voice file if present
            if (voice != null) {
                try {
                    // Log more details about the voice file
                    if (voice is okhttp3.RequestBody) {
                        Log.d("VoiceDebug", "Voice RequestBody is valid")
                    }

                    // Use a filename with proper extension for server validation
                    // The server expects mp3, wav, or m4a file types
                    builder.addFormDataPart("voice", "voice_message.m4a", voice)
                    parts.add("voice=true")
                    Log.d("VoiceDebug", "Voice part added as voice_message.m4a")
                } catch (e: Exception) {
                    Log.e("VoiceDebug", "Error adding voice: ${e.message}")
                    e.printStackTrace()
                }
            }

            Log.d("ConversationRepository", "Request parts: ${parts.joinToString(", ")}")

            // Build the request body
            val requestBody = builder.build()

            // Create OkHttp client with timeouts
            val client =
                    OkHttpClient.Builder()
                            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                            .build()

            // Create request
            val request =
                    Request.Builder()
                            .url(BuildConfig.API_BASE_URL + "api/v2/send-message/$conversionId")
                            .post(requestBody)
                            .addHeader("Authorization", token)
                            .addHeader("lang", lang)
                            .build()

            // Execute request
            client.newCall(request)
                    .enqueue(
                            object : okhttp3.Callback {
                                override fun onFailure(call: okhttp3.Call, e: IOException) {
                                    Log.e(
                                            "ConversationRepository",
                                            "Failed to send message: ${e.message}"
                                    )
                                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                                        sendMessageLiveData.value = null
                                        Toast.makeText(
                                                        context,
                                                        "Failed to send message",
                                                        Toast.LENGTH_SHORT
                                                )
                                                .show()
                                    }
                                }

                                override fun onResponse(
                                        call: okhttp3.Call,
                                        response: okhttp3.Response
                                ) {
                                    try {
                                        val responseBody = response.body()?.string() ?: ""
                                        Log.d("ConversationRepository", "Response: $responseBody")

                                        if (response.isSuccessful) {
                                            try {
                                                val messageResponse =
                                                        com.google.gson.Gson()
                                                                .fromJson(
                                                                        responseBody,
                                                                        MessageResponse::class.java
                                                                )

                                                android.os.Handler(
                                                                android.os.Looper.getMainLooper()
                                                        )
                                                        .post {
                                                            sendMessageLiveData.value =
                                                                    messageResponse
                                                            getMessagesByConversationId(
                                                                    conversionId
                                                            )
                                                        }
                                            } catch (e: Exception) {
                                                Log.e(
                                                        "ConversationRepository",
                                                        "Error parsing successful response: ${e.message}"
                                                )
                                                android.os.Handler(
                                                                android.os.Looper.getMainLooper()
                                                        )
                                                        .post { sendMessageLiveData.value = null }
                                            }
                                        } else {
                                            try {
                                                // Parse error message
                                                val errorJson =
                                                        com.google.gson.JsonParser.parseString(
                                                                        responseBody
                                                                )
                                                                .asJsonObject
                                                val errorMessage =
                                                        errorJson.get("message")?.asString
                                                                ?: "Unknown error"

                                                // Check for validation errors
                                                var detailedError = errorMessage
                                                if (errorJson.has("data")) {
                                                    val data = errorJson.getAsJsonObject("data")
                                                    if (data.has("voice")) {
                                                        val voiceErrors =
                                                                data.getAsJsonArray("voice")
                                                        if (voiceErrors.size() > 0) {
                                                            detailedError =
                                                                    "Voice file error: " +
                                                                            voiceErrors.get(0)
                                                                                    .asString
                                                        }
                                                    }
                                                }

                                                Log.e(
                                                        "ConversationRepository",
                                                        "Error: $detailedError (${response.code()})"
                                                )

                                                android.os.Handler(
                                                                android.os.Looper.getMainLooper()
                                                        )
                                                        .post {
                                                            sendMessageLiveData.value = null
                                                            Toast.makeText(
                                                                            context,
                                                                            detailedError,
                                                                            Toast.LENGTH_LONG
                                                                    )
                                                                    .show()
                                                        }
                                            } catch (e: Exception) {
                                                Log.e(
                                                        "ConversationRepository",
                                                        "Error parsing error response: ${e.message}"
                                                )
                                                android.os.Handler(
                                                                android.os.Looper.getMainLooper()
                                                        )
                                                        .post { sendMessageLiveData.value = null }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                                "ConversationRepository",
                                                "Error processing response: ${e.message}"
                                        )
                                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                                            sendMessageLiveData.value = null
                                        }
                                    }
                                }
                            }
                    )
        } catch (e: Exception) {
            Log.e("ConversationRepository", "Error creating request: ${e.message}")
            sendMessageLiveData.value = null
        }
    }

    fun getMessagesByConversationId(conversionId: Int) {
        val call =
                apiService.getMessagesByConversationId(
                        conversionId,
                        token,
                        "multipart/form-data",
                        lang
                )
        call.enqueue(
                object : Callback<List<Message>> {
                    override fun onResponse(
                            call: Call<List<Message>>,
                            response: Response<List<Message>>
                    ) {
                        if (response.isSuccessful) {
                            messagesLiveData.value = response.body()
                        }
                    }

                    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                        // Handle error
                    }
                }
        )
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
