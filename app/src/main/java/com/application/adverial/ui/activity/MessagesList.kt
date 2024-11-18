package com.application.adverial.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.databinding.ActivityMassegesListBinding
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.MessagesListAdapter

class MessagesList : AppCompatActivity() {

    private lateinit var binding: ActivityMassegesListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMassegesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.messagesBackBtn)
        Tools().changeViewFromTheme(this, binding.messageListRoot)
        pageInit()
    }

    private fun pageInit() {
        binding.messagesListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.messagesListRecyclerView.adapter = MessagesListAdapter()
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") {
                View.LAYOUT_DIRECTION_LTR
            } else {
                View.LAYOUT_DIRECTION_RTL
            }
    }
}
