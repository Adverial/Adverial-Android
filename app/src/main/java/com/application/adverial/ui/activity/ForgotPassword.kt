package com.application.adverial.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.databinding.ActivityForgotPasswordBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.ForgotPasswordRequestModel
import com.application.adverial.service.Tools
import java.util.regex.Pattern

class ForgotPassword : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.forgotPasswordRoot)

        setup()
    }

    private fun setup() {
        binding.btnSend.setOnClickListener {
            val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
            val emailInput = binding.forgotPasswordEditText.text.toString().trim()
            val mail = emailPattern.matcher(emailInput)

            if (mail.find() && emailInput.isNotBlank()) {
                binding.lottie8.visibility = View.VISIBLE
                val repository = Repository(this)
                val requestModel = ForgotPasswordRequestModel(emailInput)
                repository.forgotPassword(requestModel)

                repository.forgotPassword.observe(this) { response ->
                    binding.lottie8.visibility = View.GONE
                    if (response.status) {
                        onBackPressed()
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.login_email_hint), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun back(view: View) = onBackPressed()

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
