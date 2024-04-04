package com.application.adverial.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home
import com.application.adverial.utils.CustomPhoneNumberFormattingTextWatcher
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_my_account.*
import kotlinx.android.synthetic.main.activity_new_ad_category.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.lottie14
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

    fun signup(view: View){
        val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
        val emailMatcher = emailPattern.matcher(signup_email.text.toString())
        if(signup_firstname.text.isNotBlank() && signup_lastname.text.isNotBlank()){
            if(signup_phone.text.toString().replace(" ", "").length == 10){
                if(emailMatcher.find()){
                    if(signup_password.text.length >= 6){
                        if(signup_terms1.isChecked){
                            lottie14.visibility= View.VISIBLE
                            Tools().viewEnable(this.window.decorView.rootView, false)
                            val repo= Repository(this)
                            repo.signup(signup_firstname.text.toString(), signup_lastname.text.toString(), signup_email.text.toString(), signup_password.text.toString(), signup_phone.text.toString().replace(" ", ""))
                            repo.getSignupData().observe(this) {
                                lottie14.visibility = View.GONE
                                Tools().viewEnable(this.window.decorView.rootView, true)
                                if (it.status) {
                                    //Toast.makeText(this, getString(R.string.signup_successful), Toast.LENGTH_SHORT).show()
                                    val intent= Intent(this, PhoneAuth::class.java)
                                    intent.putExtra("email", signup_email.text.toString())
                                    intent.putExtra("parent", "register")
                                    startActivity(intent)
                                } else Toast.makeText(this, getString(R.string.signup_unsuccessful), Toast.LENGTH_SHORT).show()
                            }
                        }else Toast.makeText(this, getString(R.string.termsAreNotAccepted), Toast.LENGTH_SHORT).show()
                    }else Toast.makeText(this, getString(R.string.passwordMustBeMoreThan6), Toast.LENGTH_SHORT).show()
                }else Toast.makeText(this, getString(R.string.EmailAddressIsWrong), Toast.LENGTH_SHORT).show()
            }else Toast.makeText(this, getString(R.string.phoneIsNotEntered), Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this, getString(R.string.nameIsNotEntered), Toast.LENGTH_SHORT).show()
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