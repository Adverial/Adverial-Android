package com.application.adverial.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.databinding.ActivityContactUsBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools

class ContactUs : AppCompatActivity() {

    private lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.newAdCategoryBack7)
        Tools().changeViewFromTheme(this, binding.contactUsRoot)
        Tools().setBasedLogo(this, R.id.imageView55)

        pageInit()
    }

    private fun pageInit() {
        // Additional initialization if needed
    }

    fun send(view: View) {
        val message = binding.contactUsText.text.toString()
        if (message.isNotBlank()) {
            val repo = Repository(this)
            repo.contactUs(message)
            repo.getContactUsData().observe(this) { response ->
                if (response.status) {
                    Toast.makeText(this, getString(R.string.messageSent), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, R.string.messageEmpty, Toast.LENGTH_SHORT).show()
        }
    }

    fun website(view: View) {
        Tools().openBrowser(this, BuildConfig.API_BASE_URL)
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
