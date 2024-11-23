package com.application.adverial

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.databinding.ActivityMainBinding
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.MessageActivity
import com.application.adverial.ui.activity.NewAdImages
import com.application.adverial.ui.navigation.Home
import com.application.adverial.utils.DialogUtils
import com.application.adverial.utils.NetworkUtils
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the keyboard on launch
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .permitNetwork()
                    .build()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection = if (language.isNullOrEmpty() || language == "0" || language == "1") {
            View.LAYOUT_DIRECTION_LTR
        } else {
            View.LAYOUT_DIRECTION_RTL
        }

        if (NetworkUtils.isNetworkAvailable(this)) {
            Tools().goto(this, NewAdImages(), false)
        } else {
            DialogUtils.showNoInternetDialog(this)
            finish()
        }
    }

    companion object {
        const val CHANNEL_ID = "message_notifications_channel"
        const val CHANNEL_NAME = "Message Notifications"
        const val NOTIFICATION_ID = 1
    }

    private fun setupPusher() {
        val options = PusherOptions().setCluster(BuildConfig.PUSHER_APP_CLUSTER)
        val pusher = Pusher(BuildConfig.PUSHER_APP_KEY, options)

        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                // Handle connection state changes if needed
            }

            override fun onError(message: String, code: String, e: Exception) {
                // Handle connection errors if needed
            }
        }, ConnectionState.ALL)

        val userId = getSharedPreferences("user", 0).getString("user_id", "")
        val channel = pusher.subscribe("user.$userId")
        channel.bind("new.message") { event ->
            val jsonObject = JSONObject(event.data)
            val partnerName = jsonObject.getString("partnerName")
            val conversationId = jsonObject.getInt("conversionId")

            // ToDo:: Don’t show notification if the user is in MessageActivity && MessageActivity.conversationId == conversationId
            if (MessageActivity.isActivityVisible ) {
                return@bind
            }
            Tools().showNotificationMessage(
                this@MainActivity,
                "New message from $partnerName",
                CHANNEL_ID,
                NOTIFICATION_ID,
                conversationId,
                partnerName
            )
        }
    }
}
