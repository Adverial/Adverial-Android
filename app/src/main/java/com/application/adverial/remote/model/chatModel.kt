package com.application.adverial.remote.model

import com.google.gson.annotations.SerializedName
import android.os.Parcel
import android.os.Parcelable

data class ConversationResponse(
    @SerializedName("conversion_id") val conversionId: Int,
    val name: String
)

data class Conversation(
    @SerializedName("chat_partner_id") val chatPartnerId: Int,
    @SerializedName("conversion_id") val conversionId: Int,
    @SerializedName("chat_partner_name") val chatPartnerName: String,
    @SerializedName("chat_partner_email") val chatPartnerEmail: String,
    @SerializedName("last_message") val lastMessage: String,
    @SerializedName("last_message_time") val lastMessageAt: String,
    val avatar: String,
    @SerializedName("ad_id") val adId: Int?,
    @SerializedName("ad_title") val adTitle: String?,
    @SerializedName("ad_price") val adPrice: Double?,
    @SerializedName("ad_price_currency") val adPriceCurrency: String?,
    @SerializedName("ad_image") val adImage: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(chatPartnerId)
        parcel.writeInt(conversionId)
        parcel.writeString(chatPartnerName)
        parcel.writeString(chatPartnerEmail)
        parcel.writeString(lastMessage)
        parcel.writeString(lastMessageAt)
        parcel.writeString(avatar)
        parcel.writeValue(adId)
        parcel.writeString(adTitle)
        parcel.writeValue(adPrice)
        parcel.writeString(adPriceCurrency)
        parcel.writeString(adImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Conversation> {
        override fun createFromParcel(parcel: Parcel): Conversation {
            return Conversation(parcel)
        }

        override fun newArray(size: Int): Array<Conversation?> {
            return arrayOfNulls(size)
        }
    }
}

data class MessageResponse(
    val id: Int,
    @SerializedName("conversion_id") val conversionId: Int,
    val message: String,
    @SerializedName("media") val mediaUrl: String?,
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
