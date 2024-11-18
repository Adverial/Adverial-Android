package com.application.adverial.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityNotificationsBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.NotificationData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NotificationAdapter

class Notifications : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.homeMenu4)
        Tools().changeViewFromTheme(this, binding.notificationRot)
        Tools().setBasedLogo(this, R.id.imageView47)

        pageInit()
    }

    private fun pageInit() {
        Tools().viewEnable(window.decorView.rootView, false)

        // Setting up the RecyclerView with a LinearLayoutManager
        binding.notificationRecycelrView.layoutManager = LinearLayoutManager(this)

        val repo = Repository(this)
        repo.notification()
        repo.getNotificationData().observe(this) { response ->
            if (response.status) {
                binding.notificationRecycelrView.adapter = NotificationAdapter(response.data as ArrayList<NotificationData>)
                if (response.data.isEmpty()) binding.notificationEmpty.visibility = View.VISIBLE
            } else {
                binding.notificationEmpty.visibility = View.VISIBLE
            }
            binding.lottie18.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") View.LAYOUT_DIRECTION_LTR
            else View.LAYOUT_DIRECTION_RTL
    }
}
