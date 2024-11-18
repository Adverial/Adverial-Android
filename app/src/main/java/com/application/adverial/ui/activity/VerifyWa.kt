package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.application.adverial.R
import com.application.adverial.databinding.ActivityVerifyWaBinding
import com.application.adverial.remote.AuthRepository
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home

class VerifyWa : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyWaBinding
    private lateinit var whatsappNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityVerifyWaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply theme adjustments
        Tools().changeViewFromTheme(this, binding.verifyWaRoot)

        whatsappNumber = intent.getStringExtra("whatsapp_number") ?: ""
        binding.verificationSubtext.text =
            "We've sent a verification code to your WhatsApp number $whatsappNumber"

        setupListeners()
        checkCode()
    }

    private fun setupListeners() {
        // Set click listeners using View Binding
        binding.verifyOtpButton.setOnClickListener { verify() }
        binding.resendOtpButton.setOnClickListener { resendCode() }
        binding.newAdCategoryBack6.setOnClickListener { finish() }
    }

    private fun verify() {
        val otp = binding.verifyOtpCode.text.toString().toIntOrNull()

        if (otp != null) {
            binding.lottie15.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)

            val repo = AuthRepository(this)
            repo.verifyOtpWa(whatsappNumber, otp)
            repo.getVerifyResponse().observe(this) { response ->
                binding.lottie15.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)

                response?.let {
                    when {
                        it.data?.token != null -> {
                            getSharedPreferences("user", 0).edit().putString("token", it.data.token).apply()
                            getSharedPreferences("user", 0).edit().putString("user_id", it.data.userId).apply()
                            Tools().goto(this, Home(), false)
                        }
                        it.otp != null -> {
                            Toast.makeText(this, it.otp.joinToString(), Toast.LENGTH_SHORT).show()
                        }
                        it.whatsappNumber != null -> {
                            Toast.makeText(this, it.whatsappNumber.joinToString(), Toast.LENGTH_SHORT).show()
                        }
                        it.message != null -> {
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "An unknown error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.verificationCodeEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun resendCode() {
        binding.lottie15.visibility = View.VISIBLE
        val repo = AuthRepository(this)
        repo.resendOtpWa(whatsappNumber)
        repo.getLoginResponse().observe(this) {
            binding.lottie15.visibility = View.GONE
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCode() {
        val code = binding.verifyOtpCode
        val textViews = arrayOf(
            binding.verifyOtpT1,
            binding.verifyOtpT2,
            binding.verifyOtpT3,
            binding.verifyOtpT4,
            binding.verifyOtpT5,
            binding.verifyOtpT6
        )

        code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCodeBackground(textViews, code.text.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateCodeBackground(textViews: Array<TextView>, length: Int) {
        textViews.forEachIndexed { index, textView ->
            textView.background = ContextCompat.getDrawable(
                this,
                if (index == length) R.drawable.stroke2 else R.drawable.stroke1
            )
            textView.isCursorVisible = index == length
            if (index < length) {
                textView.text = binding.verifyOtpCode.text.substring(index, index + 1)
            } else {
                textView.text = ""
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
