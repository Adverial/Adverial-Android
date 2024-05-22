package com.application.adverial.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.NotificationData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NotificationAdapter
import kotlinx.android.synthetic.main.activity_my_ads.*
import kotlinx.android.synthetic.main.activity_notifications.*

class Notifications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        Tools().rotateLayout(this,home_menu4)
        Tools().changeViewFromTheme(this,notificationRot)
        Tools().setBasedLogo(this,R.id.imageView47)

        pageInit()
    }

    private fun pageInit(){
        Tools().viewEnable(this.window.decorView.rootView, false)
        val recyclerView= findViewById<RecyclerView>(R.id.notification_recycelrView)
        recyclerView.layoutManager= LinearLayoutManager(this)

        val repo= Repository(this)
        repo.notification()
        repo.getNotificationData().observe(this, {
            if(it.status){
                recyclerView.adapter= NotificationAdapter(it.data as ArrayList<NotificationData>)
                if(it.data.isEmpty()) notification_empty.visibility= View.VISIBLE
            }else notification_empty.visibility= View.VISIBLE
            lottie18.visibility= View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
        })
    }

    fun back(view: View){ finish() }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}