package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.remote.AuthRepository
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import kotlinx.android.synthetic.main.activity_login_wa.*

class LoginWa : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_wa)
        Tools().changeViewFromTheme(this, loginWaRoot)
    }

    fun sendOTP(view: View) {
        val whatsappNumber = countryCodePicker.fullNumberWithPlus + login_phone.text.toString().trim()
        Log.d("whatsappNumber", whatsappNumber)
        if (whatsappNumber.isNotBlank()) {
            lottie7.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = AuthRepository(this)
            repo.loginViaWa(whatsappNumber).observe(this) {
             Log.d("loginViaWa", it.toString())

                if (it.message == "Please verify the OTP sent to your WhatsApp to complete login.") {
                    lottie7.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    val intent = Intent(this, VerifyWa::class.java)
                    intent.putExtra("whatsapp_number", whatsappNumber)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
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
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
