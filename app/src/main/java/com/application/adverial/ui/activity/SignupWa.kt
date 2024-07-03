package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.application.adverial.R
import com.application.adverial.remote.AuthRepository
import com.application.adverial.service.Tools
import com.application.adverial.utils.CustomPhoneNumberFormattingTextWatcher
import kotlinx.android.synthetic.main.activity_signup_wa.*

class SignupWa : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_wa)
        Tools().changeViewFromTheme(this, signupWaRoot)
        signup_phone.addTextChangedListener(CustomPhoneNumberFormattingTextWatcher())
    }

    fun register(view: View) {
        val name = signup_name.text.toString().trim()
        val phoneNumberWithOutCountryCode = signup_phone.text.toString().removePrefix("0").replace(" ", "")
        val whatsappNumber = countryCodePickerWaSignup.selectedCountryCodeWithPlus + phoneNumberWithOutCountryCode

        if (name.isNotBlank() && phoneNumberWithOutCountryCode.length == 10) {
            lottie7.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = AuthRepository(this)
            repo.registerViaWa(name, whatsappNumber)
            repo.getSignupResponse().observe(this, Observer { response ->
                Tools().viewEnable(this.window.decorView.rootView, true)
                response?.let {
                    if (it.message?.contains("OTP sent to your WhatsApp") == true) {
                        lottie7.visibility = View.GONE
                        val intent = Intent(this, VerifyWa::class.java)
                        intent.putExtra("whatsapp_number", whatsappNumber)
                        startActivity(intent)
                    } else {
                        lottie7.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    lottie7.visibility = View.GONE
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Please enter valid details.", Toast.LENGTH_SHORT).show()
        }
    }

    fun gotoLogin(view: View) {
        val intent = Intent(this, LoginWa::class.java)
        startActivity(intent)
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
