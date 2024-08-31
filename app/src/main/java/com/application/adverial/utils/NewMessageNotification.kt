package com.application.adverial.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.remote.model.Conversation
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.MessageActivity
import com.google.gson.annotations.SerializedName
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import org.json.JSONObject

object NewMessageNotification {
    private const val CHANNEL_ID: String = "message_notifications_channel"
    const val CHANNEL_NAME: String = "Message Notifications"
    private const val NOTIFICATION_ID: Int = 1

    fun setupNewMessageNotification(context: Context) {
    val options = PusherOptions().setCluster(BuildConfig.PUSHER_APP_CLUSTER)
    val pusher = Pusher(BuildConfig.PUSHER_APP_KEY, options)

    pusher.connect(object : ConnectionEventListener {
        override fun onConnectionStateChange(change: ConnectionStateChange) {
            // Handle connection state changes if needed
          //  Log.d("Pusher", "Connection state changed: ${change.currentState}")
        }

        override fun onError(message: String, code: String, e: Exception) {
            // Handle connection errors if needed
           // Log.e("Pusher", "Connection error: $message, code: $code", e)
        }
    }, ConnectionState.ALL)

    val userId = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("user_id", "")
 //   Log.d("Pusher", "User ID: $userId")

    if (userId.isNullOrEmpty()) {
      //  Log.e("Pusher", "User ID is null or empty")
        return
    }

    val channel = pusher.subscribe("user.$userId")
    channel.bind("new.message") { event ->
        val jsonObject = JSONObject(event.data)
        val partnerName = jsonObject.getString("partnerName")
        val conversationId = jsonObject.getInt("conversionId")


        // don't show notification if the user is in the MessageActivity active
        if (MessageActivity.isActivityVisible && MessageActivity.conversationId == conversationId) {
            return@bind
        }

       showNotificationMessage(
            context,
            "New message from $partnerName",
            CHANNEL_ID,
            NOTIFICATION_ID,
            conversationId,
            partnerName
        )
    }
}
    fun showNotificationMessage(
        context: Context,
        message: String,
        CHANNEL_ID: String,
        NOTIFICATION_ID: Int,
        conversation_id: Int,
        user_name: String
    ) {
        createNotificationChannel(context, CHANNEL_ID, "Message Channel")
        val intent = Intent(context, MessageActivity::class.java).apply {
         val   conversationRequest= Conversation(
                chatPartnerId = 0,
                conversionId = conversation_id,
                chatPartnerName = user_name,
                chatPartnerEmail = "",
                lastMessage = "",
                lastMessageAt = "",
                avatar = "",
                adId = 0,
                adTitle = "",
                adPrice = "",
                adPriceCurrency = "",
                adImage = ""
            )
            putExtra("conversation", conversationRequest)
            putExtra("show_item", false)

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.new_message))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun createNotificationChannel(context: Context, CHANNEL_ID: String, CHANNEL_NAME: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "Your channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}