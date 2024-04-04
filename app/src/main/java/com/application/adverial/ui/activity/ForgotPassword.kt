package com.application.adverial.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.ForgotPasswordRequestModel
import com.application.adverial.service.Tools
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        Tools().changeViewFromTheme(this, forgotPasswordRoot)

        setup()
    }

    private fun setup() {
        btn_send.setOnClickListener {
            val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
            val mail = emailPattern.matcher(forgotPasswordEditText.text.toString().trim())

            if (mail.find() && forgotPasswordEditText.text.isNotBlank()) {
                lottie8.visibility = View.VISIBLE
                val repository = Repository(this)
                val requestModel = ForgotPasswordRequestModel(forgotPasswordEditText.text.toString().trim())
                repository.forgotPassword(requestModel)
                repository.forgotPassword.observe(this) {
                    val status = it.status
                    if (status) {
                        onBackPressed()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun back(view: View) = onBackPressed()


    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection =
            View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}