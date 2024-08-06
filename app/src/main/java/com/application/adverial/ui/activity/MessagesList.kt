package com.application.adverial.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_masseges_list.*

class MessagesList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masseges_list)
        Tools().rotateLayout(this,messages_back_btn)
        Tools().changeViewFromTheme(this,messageListRoot)
        pageInit()
    }

    private fun pageInit(){
        messagesList_recyclerView.layoutManager = LinearLayoutManager(this)
        messagesList_recyclerView.adapter = MessagesListAdapter()
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