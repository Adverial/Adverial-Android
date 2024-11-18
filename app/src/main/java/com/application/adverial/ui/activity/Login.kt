package com.application.adverial.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityLoginBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var show = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Tools().changeViewFromTheme(this, binding.loginRoot)

        pageInit()
    }

    private fun pageInit() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("login"))

        val regularFont = ResourcesCompat.getFont(this, R.font.regular)
        binding.loginPassword.apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            typeface = regularFont
        }

        binding.loginShowPassword.setOnClickListener {
            togglePasswordVisibility(regularFont)
        }
    }

    private fun togglePasswordVisibility(regularFont: Any?) {
        if (show) {
            binding.loginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.loginShowPassword.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.im_invisible)
            )
            binding.loginPassword.typeface = regularFont as Typeface?
            show = false
        } else {
            binding.loginPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.loginShowPassword.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.im_visible)
            )
           // binding.loginPassword.typeface = regularFont
            show = true
        }
    }

    fun login(view: View) {
        val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
        val emailInput = binding.loginEmail.text.toString()
        val emailMatcher = emailPattern.matcher(emailInput)

        if (emailMatcher.find() && binding.loginPassword.text.isNotBlank()) {
            binding.lottie7.visibility = View.VISIBLE
            Tools().viewEnable(window.decorView.rootView, false)
            val repo = Repository(this)
            repo.login(emailInput, binding.loginPassword.text.toString())
            repo.getLoginData().observe(this) { response ->
                binding.lottie7.visibility = View.GONE
                Tools().viewEnable(window.decorView.rootView, true)
                handleLoginResponse(response)
            }
        } else {
            Toast.makeText(this, getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLoginResponse(response: Any) {
//        if (response.status) {
//            if (response.data.status == 1 || response.data.token != null) {
//                getSharedPreferences("user", 0).edit().putString("token", response.data.token).apply()
//                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
//                Tools().goto(this, Home(), false)
//            } else {
//                val intent = Intent(this, PhoneAuth::class.java)
//                intent.putExtra("email", binding.loginEmail.text.toString())
//                intent.putExtra("parent", "login")
//                startActivity(intent)
//            }
//        } else {
//            Toast.makeText(this, getString(R.string.login_unsuccessful), Toast.LENGTH_SHORT).show()
//        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getStringExtra("action")
            if (action == "finish") finish()
        }
    }

    fun signup(view: View) {
        startActivity(Intent(this, Signup::class.java))
    }

    fun goToForgotPassword(view: View) {
        startActivity(Intent(this, ForgotPassword::class.java))
    }

    fun back(view: View) {
        finish()
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
