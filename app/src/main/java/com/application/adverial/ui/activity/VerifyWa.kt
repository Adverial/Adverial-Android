package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.application.adverial.R
import com.application.adverial.remote.AuthRepository
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home
import kotlinx.android.synthetic.main.activity_verify_wa.*

class VerifyWa : AppCompatActivity() {
    private lateinit var whatsappNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_wa)
        Tools().changeViewFromTheme(this, verifyWaRoot)

        whatsappNumber = intent.getStringExtra("whatsapp_number") ?: ""
        val verificationSubtext = findViewById<TextView>(R.id.verification_subtext)
        verificationSubtext.text =
            "we've sent a verification code to your whatsapp number $whatsappNumber"
        checkCode()
    }

    fun verify(view: View) {
        val otp = verify_otp_code.text.toString().toIntOrNull()

        if (otp != null) {
            lottie15.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = AuthRepository(this)
            repo.verifyOtpWa(whatsappNumber, otp)
            repo.getVerifyResponse().observe(this) {
                lottie15.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)

                if (it.token != null) {
                    Log.e("token", it.token)
                    lottie15.visibility = View.GONE
                    getSharedPreferences("user", 0).edit().putString("token", it.token).apply()
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    //Tools().goto(this, Home(), false)
                } else {
                    lottie15.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.verificationCodeEmpty), Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun resendCode(view: View) {
        lottie15.visibility = View.VISIBLE
        val repo = AuthRepository(this)
        repo.resendOtpWa(whatsappNumber)
        repo.getLoginResponse().observe(this) {
            lottie15.visibility = View.GONE
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCode() {
        val code = verify_otp_code
        val c1 = verify_otp_t1
        val c2 = verify_otp_t2
        val c3 = verify_otp_t3
        val c4 = verify_otp_t4
        val c5 = verify_otp_t5
        val c6 = verify_otp_t6

        code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    code.text.isEmpty() -> {
                        resetCodeBackground(c1, c2, c3, c4, c5, c6)
                    }

                    code.text.length == 1 -> {

                        updateCodeBackground(arrayOf(c1, c2, c3, c4, c5, c6), 1)
                    }

                    code.text.length == 2 -> {
                        updateCodeBackground(arrayOf(c1, c2, c3, c4, c5, c6), 2)
                    }

                    code.text.length == 3 -> {
                        updateCodeBackground(arrayOf(c1, c2, c3, c4, c5, c6), 3)
                    }

                    code.text.length == 4 -> {
                        updateCodeBackground(arrayOf(c1, c2, c3, c4, c5, c6), 4)
                    }

                    code.text.length == 5 -> {
                        updateCodeBackground(arrayOf(c1, c2, c3, c4, c5, c6), 5)
                    }

                    code.text.length == 6 -> {
                        updateCodeBackground(arrayOf(c1, c2, c3, c4, c5, c6), 6)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun resetCodeBackground(vararg textViews: TextView) {
        textViews.forEachIndexed { index, textView ->
            textView.background = ContextCompat.getDrawable(
                this,
                if (index == 0) R.drawable.stroke2 else R.drawable.stroke1
            )
            textView.text = ""
            textView.isCursorVisible = index == 0
        }
    }

    private fun updateCodeBackground(textViews: Array<TextView>, length: Int) {
        textViews.forEachIndexed { index, textView ->
            textView.background = ContextCompat.getDrawable(
                this,
                if (index == length) R.drawable.stroke2 else R.drawable.stroke1
            )
            textView.isCursorVisible = index == length
            if (index < length) {
                textView.text = verify_otp_code.text.substring(index, index + 1)
            } else {
                textView.text = "" // Clear the text if index is >= length
            }
        }
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
