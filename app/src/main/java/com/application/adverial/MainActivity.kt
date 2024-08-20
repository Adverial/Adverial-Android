package com.application.adverial

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.remote.model.Message
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.MessageActivity
import com.application.adverial.ui.activity.NewAdMap
import com.application.adverial.ui.navigation.Home
import com.application.adverial.utils.DialogUtils
import com.application.adverial.utils.NetworkUtils
import com.application.adverial.utils.NotificationUtils
import com.huawei.hms.maps.MapsInitializer
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.android.synthetic.main.activity_message.recyclerViewMessages
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.setApiKey("DQEDACRuLE5ygNAVgf/C/jiDIULciSgrEQuOKKATQxwiZFYsqUFTLr4CJke4SudwvutlZqfvK5OWVYZ6B16ZeM/hojk/RC6ScXsgaw==");

        setContentView(R.layout.activity_main)
        val inputMethodManager: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .permitNetwork().build()
            )
        }


    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

//        val sharedPrefManager: SharedPrefManager by lazy {
//            SharedPrefManager(this)
//        }

        if (NetworkUtils.isNetworkAvailable(this)) {
            // START INTENT NewAdMap
            val intent= Intent(this, NewAdMap::class.java)
            //add extra data to intent
            intent.putExtra("adId", 23)
            intent.putExtra("country", "country")
            intent.putExtra("city", "city")
            intent.putExtra("district", "district")

            startActivity(intent)

            //Tools().goto(this, Home(), false)
        } else {
            DialogUtils.showNoInternetDialog(this)
            finish()
        }
    }

    companion object {
        const val CHANNEL_ID:String = "message_notifications_channel"
        const val CHANNEL_NAME :String= "Message Notifications"
        const val NOTIFICATION_ID :Int= 1
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
//            Log.d("userId", userId.toString())
            val channel = pusher.subscribe("user.$userId")
            channel.bind("new.message") { event ->
                val jsonObject = JSONObject(event.data)
                val partnerName = jsonObject.getString("partnerName")
                val conversationId = jsonObject.getInt("conversionId")
                 // don't show notification if the user is in the MessageActivity active
                if (MessageActivity.isActivityVisible && MessageActivity.conversationId == conversationId) {
                    return@bind
                }
                Tools().showNotificationMessage(this,
                    "New message from $partnerName",
                      CHANNEL_ID , NOTIFICATION_ID ,conversationId, partnerName)


            }
        }

    }

