package com.application.adverial.remote.model

import com.google.gson.annotations.SerializedName

data class ConversationResponse(
    @SerializedName("conversion_id") val conversionId: Int,
    val name: String
)

data class Conversation(
    @SerializedName("chat_partner_id") val chatPartnerId: Int,
    @SerializedName("conversion_id") val conversionId: Int,
    @SerializedName("chat_partner_name") val chatPartnerName: String,
    @SerializedName("chat_partner_email") val chatPartnerEmail: String,
    val avatar: String
)

data class MessageResponse(
    val id: Int,
    @SerializedName("conversion_id") val conversionId: Int,
    val message: String,
    @SerializedName("media_url") val mediaUrl: String?,
    @SerializedName("created_at") val createdAt: String
)

data class Message(
    @SerializedName("message_id") val messageId: Int,
    @SerializedName("conversion_id") val conversionId: Int,
    val message: String,
    @SerializedName("media_url") val mediaUrl: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("sender_id") val senderId: Int
)
