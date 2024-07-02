package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.remote.AuthRepository
import com.application.adverial.service.Tools
import com.application.adverial.utils.CustomPhoneNumberFormattingTextWatcher
import kotlinx.android.synthetic.main.activity_login_wa.*

class LoginWa : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_wa)
        Tools().changeViewFromTheme(this, loginWaRoot)
        login_phone.addTextChangedListener(CustomPhoneNumberFormattingTextWatcher())

    }

    fun sendOTP(view: View) {

        //login_phone.text.toString().removePrefix("0")
        val phoneNumberWithOutCountryCode = login_phone.text.toString().removePrefix("0").replace(" ", "")

        val phoneNumber =
            countryCodePickerWaLogin.selectedCountryCodeWithPlus + phoneNumberWithOutCountryCode



        if (phoneNumberWithOutCountryCode.length == 10) {
            lottie7.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = AuthRepository(this)
            repo.loginViaWa(phoneNumber)
            repo.getLoginResponse().observe(this) { response ->
                Tools().viewEnable(this.window.decorView.rootView, true)
                response?.let {
                    if (it.message == "Please verify the OTP sent to your WhatsApp to complete login.") {
                        lottie7.visibility = View.GONE
                        val intent = Intent(this, VerifyWa::class.java)
                        intent.putExtra("whatsapp_number", phoneNumber)
                        startActivity(intent)
                    } else {
                        lottie7.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
        }
    }

    fun clear(view: View) {
        login_phone.setText("")
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection =
            View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
