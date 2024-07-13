package com.application.adverial.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.MessageAdapter
import kotlinx.android.synthetic.main.activity_message.*

class Message : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
//        Tools().rotateLayout(this,imageView44)
//        Tools().changeViewFromTheme(this,messageRoot)

//        pageInit()
    }

//    private fun pageInit(){
//        message_recyclerView.layoutManager= LinearLayoutManager(this)
//        message_recyclerView.adapter= MessageAdapter()
//    }
//
//    fun back(view: View){ finish() }
//
//    override fun onResume() {
//        super.onResume()
//        Tools().getLocale(this)
//        val language =  getSharedPreferences("user", 0).getString("languageId", "")
//        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
//        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
//    }
}