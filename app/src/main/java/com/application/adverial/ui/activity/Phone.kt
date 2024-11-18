package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.application.adverial.R
import com.application.adverial.databinding.ActivityPhoneBinding
import com.application.adverial.service.Tools

class Phone : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneBinding
    private var type = ""
    private var idArray = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.phoneRoot)
        pageInit()
    }

    private fun pageInit() {
        type = intent.getStringExtra("type") ?: ""
        idArray = intent.getStringExtra("idArray") ?: ""

        // Add TextWatcher to monitor phone number input
        binding.phoneAuthPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.phoneAuthPhoneNumber.text.length == 10) {
                    binding.phoneAuthOk.backgroundTintList =
                        ContextCompat.getColorStateList(this@Phone, R.color.red)
                } else {
                    binding.phoneAuthOk.backgroundTintList =
                        ContextCompat.getColorStateList(this@Phone, R.color.red_cover)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun ok(view: View) {
        if (binding.phoneAuthPhoneNumber.text.length == 10) {
            val phone = binding.countryCodePicker.fullNumber + binding.phoneAuthPhoneNumber.text.toString()
            val intent = Intent(this, NewAdInfo::class.java).apply {
                putExtra("type", type)
                putExtra("idArray", idArray)
                putExtra("phone", phone)
            }
            startActivity(intent)
            finish()
        }
    }

    fun clear(view: View) {
        binding.phoneAuthPhoneNumber.setText("")
        binding.phoneAuthOk.backgroundTintList =
            ContextCompat.getColorStateList(this@Phone, R.color.red_cover)
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
