package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.databinding.ActivitySignupWaBinding
import com.application.adverial.remote.AuthRepository
import com.application.adverial.service.Tools
import com.application.adverial.utils.CustomPhoneNumberFormattingTextWatcher

class SignupWa : AppCompatActivity() {

    // Declare the binding object
    private lateinit var binding: ActivitySignupWaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivitySignupWaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.signupWaRoot)

        // Add text watcher for phone number formatting
        binding.signupPhone.addTextChangedListener(CustomPhoneNumberFormattingTextWatcher())

        // Set up button click listeners using binding
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Register button click
        binding.signupButton.setOnClickListener {
            register()
        }

        // "Already have an account" link click
        binding.loginLink.setOnClickListener {
            gotoLogin()
        }

        // Back button click
        binding.newAdCategoryBack5.setOnClickListener {
            back()
        }
    }

    private fun register() {
        val name = binding.signupName.text.toString().trim()
        val phoneNumberWithoutCountryCode = binding.signupPhone.text.toString().removePrefix("0").replace(" ", "")
        val whatsappNumber = binding.countryCodePickerWaSignup.selectedCountryCodeWithPlus + phoneNumberWithoutCountryCode

        if (name.isNotBlank() && phoneNumberWithoutCountryCode.length == 10) {
            binding.lottie7.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)

            val repo = AuthRepository(this)
            repo.registerViaWa(name, whatsappNumber)
            observeSignupResponse(repo, whatsappNumber)
        } else {
            Toast.makeText(this, "Please enter valid details.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeSignupResponse(repo: AuthRepository, whatsappNumber: String) {
        repo.getSignupResponse().observe(this) { response ->
            Tools().viewEnable(this.window.decorView.rootView, true)
            binding.lottie7.visibility = View.GONE

            response?.let {
                when {
                    it.message?.contains("OTP sent to your WhatsApp") == true -> {
                        navigateToVerifyWa(whatsappNumber)
                    }
                    it.whatsappNumber != null -> {
                        Toast.makeText(this, it.whatsappNumber.joinToString(), Toast.LENGTH_SHORT).show()
                    }
                    it.error != null -> {
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToVerifyWa(whatsappNumber: String) {
        val intent = Intent(this, VerifyWa::class.java).apply {
            putExtra("whatsapp_number", whatsappNumber)
        }
        startActivity(intent)
    }

    private fun gotoLogin() {
        val intent = Intent(this, LoginWa::class.java)
        startActivity(intent)
    }

    private fun back() {
        finish()
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
    }
}
