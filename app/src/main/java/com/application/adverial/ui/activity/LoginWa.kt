package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.databinding.ActivityLoginWaBinding
import com.application.adverial.remote.AuthRepository
import com.application.adverial.remote.model.GenericResponse
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.VerifyWa
import com.application.adverial.utils.CustomPhoneNumberFormattingTextWatcher

class LoginWa : AppCompatActivity() {

    private lateinit var binding: ActivityLoginWaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.loginWaRoot)

        // Add text watcher for phone number formatting
        binding.loginPhone.addTextChangedListener(CustomPhoneNumberFormattingTextWatcher())
    }

    fun sendOTP(view: View) {
        val phoneNumberWithoutCountryCode = binding.loginPhone.text.toString().removePrefix("0").replace(" ", "")
        val phoneNumber = binding.countryCodePickerWaLogin.selectedCountryCodeWithPlus + phoneNumberWithoutCountryCode

        if (phoneNumberWithoutCountryCode.length == 10) {
            binding.lottie7.visibility = View.VISIBLE
            Tools().viewEnable(window.decorView.rootView, false)

            val repo = AuthRepository(this)
            repo.loginViaWa(phoneNumber)
            repo.getLoginResponse().observe(this) { response ->
                Tools().viewEnable(window.decorView.rootView, true)
                handleLoginResponse( response, phoneNumber)
            }
        } else {
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLoginResponse(response: GenericResponse?, phoneNumber: String) {
        binding.lottie7.visibility = View.GONE
        response?.let {
            if (it.message != null) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, VerifyWa::class.java)
                intent.putExtra("whatsapp_number", phoneNumber)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.error ?: "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    fun clear(view: View) {
        binding.loginPhone.setText("")
    }

    fun back(view: View) {
        finish()
    }

    fun gotoSignup(view: View) {
        val intent = Intent(this, SignupWa::class.java)
        startActivity(intent)
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
