package com.application.adverial.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.application.adverial.ui.navigation.Home
import kotlinx.android.synthetic.main.activity_login.loginRoot
import kotlinx.android.synthetic.main.activity_login.login_email
import kotlinx.android.synthetic.main.activity_login.login_password
import kotlinx.android.synthetic.main.activity_login.login_showPassword
import kotlinx.android.synthetic.main.activity_login.lottie7
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    private var show= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Tools().changeViewFromTheme(this,loginRoot)

        pageInit()
    }

    private fun pageInit(){
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("login"))
        login_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        val regular = ResourcesCompat.getFont(this, R.font.regular)
        login_password.typeface = regular
        login_showPassword.setOnClickListener {
            if(show){
                login_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                login_showPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.im_invisible))
                login_password.typeface = regular
                show= false
            }else{
                login_password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                login_showPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.im_visible))
                login_password.typeface = regular
                show= true
            }
        }
    }

    fun login(view: View){
        val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
        val emailMatcher = emailPattern.matcher(login_email.text.toString())
        if(emailMatcher.find() && login_password.text.isNotBlank()){
            lottie7.visibility= View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo= Repository(this)
            repo.login(login_email.text.toString(), login_password.text.toString())
            repo.getLoginData().observe(this) {
                lottie7.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                if (it.status) {
                    if(it.data.status == 1 || it.data.token != null){
                        getSharedPreferences("user", 0).edit().putString("token", it.data.token).apply()
                        Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()

                    }else{
                        val intent= Intent(this, PhoneAuth::class.java)
                        intent.putExtra("email", login_email.text.toString())
                        intent.putExtra("parent", "login")
                        startActivity(intent)
                        Tools().goto(this, Home(), false)
                    }
                } else Toast.makeText(this, getString(R.string.login_unsuccessful), Toast.LENGTH_SHORT).show()
            }
        }else Toast.makeText(this, getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action= intent.getStringExtra("action")
            if(action == "finish") finish()
        }
    }

    fun signup(view: View){
        val intent= Intent(this, Signup::class.java)
        startActivity(intent)
    }

    fun goToForgotPassword(view: View){
        val intent= Intent(this, ForgotPassword::class.java)
        startActivity(intent)
    }

    fun back(view: View){ finish() }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}