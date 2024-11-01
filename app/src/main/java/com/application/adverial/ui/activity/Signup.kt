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
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.utils.CustomPhoneNumberFormattingTextWatcher
import kotlinx.android.synthetic.main.activity_signup.lottie14
import kotlinx.android.synthetic.main.activity_signup.signUpRoot
import kotlinx.android.synthetic.main.activity_signup.signup_email
import kotlinx.android.synthetic.main.activity_signup.signup_firstname
import kotlinx.android.synthetic.main.activity_signup.signup_lastname
import kotlinx.android.synthetic.main.activity_signup.signup_password
import kotlinx.android.synthetic.main.activity_signup.signup_phone
import kotlinx.android.synthetic.main.activity_signup.signup_showPassword
import kotlinx.android.synthetic.main.activity_signup.signup_terms1
import java.util.regex.Pattern

class Signup : AppCompatActivity() {

    private var show= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        Tools().changeViewFromTheme(this,signUpRoot)

        pageInit()


        signup_phone.addTextChangedListener(CustomPhoneNumberFormattingTextWatcher())

    }

    private fun pageInit(){
        signup_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        val regular = ResourcesCompat.getFont(this, R.font.regular)
        signup_password.typeface = regular
        signup_showPassword.setOnClickListener {
            if(show){
                signup_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                signup_password.typeface = regular
                signup_showPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.im_invisible))
                show= false
            }else{
                signup_password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                signup_password.typeface = regular
                signup_showPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.im_visible))
                show= true
            }
        }
    }

    fun signup(view: View) {
        if (!isInputValid()) return
    
        lottie14.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
    
        val repo = Repository(this)
        repo.signup(
            signup_firstname.text.toString(),
            signup_lastname.text.toString(),
            signup_email.text.toString(),
            signup_password.text.toString(),
            signup_phone.text.toString().replace(" ", "")
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
        if (signup_firstname.text.isNotBlank() && signup_lastname.text.isNotBlank()) {
            return true
        }
        Toast.makeText(this, getString(R.string.nameIsNotEntered), Toast.LENGTH_SHORT).show()
        return false
    }
    
    private fun isPhoneValid(): Boolean {
        if (signup_phone.text.toString().replace(" ", "").length == 10) {
            return true
        }
        Toast.makeText(this, getString(R.string.phoneIsNotEntered), Toast.LENGTH_SHORT).show()
        return false
    }
    
    private fun isEmailValid(): Boolean {
        val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
        val emailMatcher = emailPattern.matcher(signup_email.text.toString())
        if (emailMatcher.find()) {
            return true
        }
        Toast.makeText(this, getString(R.string.EmailAddressIsWrong), Toast.LENGTH_SHORT).show()
        return false
    }
    
    private fun isPasswordValid(): Boolean {
        if (signup_password.text.length >= 6) {
            return true
        }
        Toast.makeText(this, getString(R.string.passwordMustBeMoreThan6), Toast.LENGTH_SHORT).show()
        return false
    }
    
    private fun isTermsAccepted(): Boolean {
        if (signup_terms1.isChecked) {
            return true
        }
        Toast.makeText(this, getString(R.string.termsAreNotAccepted), Toast.LENGTH_SHORT).show()
        return false
    }
    
    private fun observeSignupData(repo: Repository) {
        repo.getSignupData().observe(this) {
            lottie14.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
            if (it.status) {
                val intent = Intent(this, PhoneAuth::class.java)
                intent.putExtra("email", signup_email.text.toString())
                intent.putExtra("parent", "register")
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.signup_unsuccessful), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clear(view: View){
        signup_phone.setText("")
    }

    fun back(view: View){
        val intent = Intent("login")
        intent.putExtra("action", "finish")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        finish()
    }


    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}