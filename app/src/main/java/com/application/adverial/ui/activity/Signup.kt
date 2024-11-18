package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivitySignupBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.utils.CustomPhoneNumberFormattingTextWatcher
import java.util.regex.Pattern

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var show = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize View Binding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.signUpRoot)
        pageInit()

        // Set phone number formatter
        binding.signupPhone.addTextChangedListener(CustomPhoneNumberFormattingTextWatcher())
    }

    private fun pageInit() {
        val regularFont = ResourcesCompat.getFont(this, R.font.regular)
        binding.signupPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.signupPassword.typeface = regularFont

        binding.signupShowPassword.setOnClickListener {
            if (show) {
                binding.signupPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.signupPassword.typeface = regularFont
                binding.signupShowPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.im_invisible))
                show = false
            } else {
                binding.signupPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.signupPassword.typeface = regularFont
                binding.signupShowPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.im_visible))
                show = true
            }
        }
    }

    fun signup(view: View) {
        if (!isInputValid()) return

        binding.lottie14.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        val repo = Repository(this)
        repo.signup(
            binding.signupFirstname.text.toString(),
            binding.signupLastname.text.toString(),
            binding.signupEmail.text.toString(),
            binding.signupPassword.text.toString(),
            binding.signupPhone.text.toString().replace(" ", "")
        )

        observeSignupData(repo)
    }

    private fun isInputValid(): Boolean {
        if (!isNameEntered()) return false
        if (!isPhoneValid()) return false
        if (!isEmailValid()) return false
        if (!isPasswordValid()) return false
        if (!isTermsAccepted()) return false
        return true
    }

    private fun isNameEntered(): Boolean {
        if (binding.signupFirstname.text.isNotBlank() && binding.signupLastname.text.isNotBlank()) {
            return true
        }
        Toast.makeText(this, getString(R.string.nameIsNotEntered), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun isPhoneValid(): Boolean {
        if (binding.signupPhone.text.toString().replace(" ", "").length == 10) {
            return true
        }
        Toast.makeText(this, getString(R.string.phoneIsNotEntered), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun isEmailValid(): Boolean {
        val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
        val emailMatcher = emailPattern.matcher(binding.signupEmail.text.toString())
        if (emailMatcher.find()) {
            return true
        }
        Toast.makeText(this, getString(R.string.EmailAddressIsWrong), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun isPasswordValid(): Boolean {
        if (binding.signupPassword.text.length >= 6) {
            return true
        }
        Toast.makeText(this, getString(R.string.passwordMustBeMoreThan6), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun isTermsAccepted(): Boolean {
        if (binding.signupTerms1.isChecked) {
            return true
        }
        Toast.makeText(this, getString(R.string.termsAreNotAccepted), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun observeSignupData(repo: Repository) {
        repo.getSignupData().observe(this) { response ->
            binding.lottie14.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
            if (response.status) {
                val intent = Intent(this, PhoneAuth::class.java)
                intent.putExtra("email", binding.signupEmail.text.toString())
                intent.putExtra("parent", "register")
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.signup_unsuccessful), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clear(view: View) {
        binding.signupPhone.setText("")
    }

    fun back(view: View) {
        val intent = Intent("login")
        intent.putExtra("action", "finish")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
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
