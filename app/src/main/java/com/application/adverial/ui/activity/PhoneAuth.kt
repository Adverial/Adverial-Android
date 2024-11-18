package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.application.adverial.R
import com.application.adverial.databinding.ActivityPhoneAuthBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home

class PhoneAuth : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.phoneAuthRoot)

        checkCode()
        binding.lottie15.setOnClickListener { }

        if (intent.hasExtra("parent") && intent.getStringExtra("parent") == "login") {
            resendCode(binding.root)
        }
    }

    fun resendCode(view: View) {
        binding.lottie15.visibility = View.VISIBLE
        with(Repository(this)) {
            binding.lottie15.visibility = View.GONE
            sendCode(intent.getStringExtra("email") ?: "")
            sendCode.observe(this@PhoneAuth) {
                Toast.makeText(this@PhoneAuth, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun verify(view: View) {
        if (binding.phoneAuthCode.length() == 6) {
            if (intent.hasExtra("parent")) {
                binding.lottie15.visibility = View.VISIBLE
                with(Repository(this)) {
                    verifyCode(intent.getStringExtra("email") ?: "", binding.phoneAuthCode.text.toString())
                    verifyCode.observe(this@PhoneAuth) {
                        binding.lottie15.visibility = View.GONE
                        if (it.status) {
                            getSharedPreferences("user", 0).edit().putString("token", it.data?.token).apply()
                            val intent = Intent(this@PhoneAuth, Home::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@PhoneAuth, it.message ?: "", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.accountUpdateDone), Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, getString(R.string.verificationCodeEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCode() {
        val codeInput = binding.phoneAuthCode
        val textFields = listOf(
            binding.phoneAuthT1, binding.phoneAuthT2, binding.phoneAuthT3,
            binding.phoneAuthT4, binding.phoneAuthT5, binding.phoneAuthT6
        )

        codeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = codeInput.text.length

                // Reset all text fields and backgrounds
                textFields.forEachIndexed { index, textView ->
                    textView.text = if (index < length) codeInput.text[index].toString() else ""
                    textView.background = ContextCompat.getDrawable(
                        this@PhoneAuth, if (index == length) R.drawable.stroke2 else R.drawable.stroke1
                    )
                    textView.isCursorVisible = index == length
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
