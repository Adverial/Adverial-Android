package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home
import kotlinx.android.synthetic.main.activity_phone_auth.*

class PhoneAuth : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)
        Tools().changeViewFromTheme(this,phoneAuthRoot)

        checkCode()
        lottie15.setOnClickListener {  }
        if(intent.hasExtra("parent") && intent.getStringExtra("parent") == "login") resendCode(window.decorView.rootView)
    }

    fun resendCode(view: View){
        lottie15.visibility= View.VISIBLE
        with(Repository(this)){
            lottie15.visibility= View.GONE
            sendCode(intent.getStringExtra("email")!!)
            sendCode.observe(this@PhoneAuth){
                Toast.makeText(this@PhoneAuth, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun verify(view: View){
        if(phoneAuth_code.length() == 6){
            if(intent.hasExtra("parent")){
                lottie15.visibility= View.VISIBLE
                with(Repository(this)){
                    verifyCode(intent.getStringExtra("email")!!, phoneAuth_code.text.toString())
                    verifyCode.observe(this@PhoneAuth){
                        lottie15.visibility= View.GONE
                        if(it.status){
                            getSharedPreferences("user", 0).edit().putString("token", it.data?.token).apply()
                            val intent= Intent(this@PhoneAuth, Home::class.java)
                            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }else Toast.makeText(this@PhoneAuth, it.message?:"", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, resources.getString(R.string.accountUpdateDone), Toast.LENGTH_SHORT).show()
                finish()
            }
        }else Toast.makeText(this, resources.getString(R.string.verificationCodeEmpty), Toast.LENGTH_SHORT).show()
    }

    private fun checkCode(){
        val code= findViewById<TextView>(R.id.phoneAuth_code)
        val c1= findViewById<TextView>(R.id.phoneAuth_t1)
        val c2= findViewById<TextView>(R.id.phoneAuth_t2)
        val c3= findViewById<TextView>(R.id.phoneAuth_t3)
        val c4= findViewById<TextView>(R.id.phoneAuth_t4)
        val c5= findViewById<TextView>(R.id.phoneAuth_t5)
        val c6= findViewById<TextView>(R.id.phoneAuth_t6)

        code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    code.text.isEmpty() -> {
                        c2.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c2.isCursorVisible= false
                        c3.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c3.isCursorVisible= false
                        c4.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c4.isCursorVisible= false
                        c5.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c5.isCursorVisible= false
                        c6.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c6.isCursorVisible= false
                        c1.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke2)
                        c1.isCursorVisible= true
                        c1.text= ""
                        c2.text= ""
                        c3.text= ""
                        c4.text= ""
                        c5.text= ""
                        c6.text= ""
                    }
                    code.text.length== 1 -> {
                        c1.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c1.isCursorVisible= false
                        c3.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c3.isCursorVisible= false
                        c4.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c4.isCursorVisible= false
                        c5.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c5.isCursorVisible= false
                        c6.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c6.isCursorVisible= false
                        c2.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke2)
                        c2.isCursorVisible= true
                        c1.text= code.text.substring(0,1)
                        c2.text= ""
                        c3.text= ""
                        c4.text= ""
                        c5.text= ""
                        c6.text= ""
                    }
                    code.text.length== 2 -> {
                        c1.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c1.isCursorVisible= false
                        c2.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c2.isCursorVisible= false
                        c4.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c4.isCursorVisible= false
                        c5.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c5.isCursorVisible= false
                        c6.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c6.isCursorVisible= false
                        c3.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke2)
                        c3.isCursorVisible= true
                        c1.text= code.text.substring(0,1)
                        c2.text= code.text.substring(1,2)
                        c3.text= ""
                        c4.text= ""
                        c5.text= ""
                        c6.text= ""
                    }
                    code.text.length== 3 -> {
                        c1.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c1.isCursorVisible= false
                        c3.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c3.isCursorVisible= false
                        c2.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c2.isCursorVisible= false
                        c5.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c5.isCursorVisible= false
                        c6.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c6.isCursorVisible= false
                        c4.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke2)
                        c4.isCursorVisible= true
                        c1.text= code.text.substring(0,1)
                        c2.text= code.text.substring(1,2)
                        c3.text= code.text.substring(2,3)
                        c4.text= ""
                        c5.text= ""
                        c6.text= ""
                    }
                    code.text.length== 4 -> {
                        c1.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c1.isCursorVisible= false
                        c3.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c3.isCursorVisible= false
                        c4.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c4.isCursorVisible= false
                        c2.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c2.isCursorVisible= false
                        c6.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c6.isCursorVisible= false
                        c5.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke2)
                        c5.isCursorVisible= true
                        c1.text= code.text.substring(0,1)
                        c2.text= code.text.substring(1,2)
                        c3.text= code.text.substring(2,3)
                        c4.text= code.text.substring(3,4)
                        c5.text= ""
                        c6.text= ""
                    }
                    code.text.length== 5 -> {
                        c1.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c1.isCursorVisible= false
                        c3.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c3.isCursorVisible= false
                        c4.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c4.isCursorVisible= false
                        c5.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c5.isCursorVisible= false
                        c2.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c2.isCursorVisible= false
                        c6.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke2)
                        c6.isCursorVisible= true
                        c1.text= code.text.substring(0,1)
                        c2.text= code.text.substring(1,2)
                        c3.text= code.text.substring(2,3)
                        c4.text= code.text.substring(3,4)
                        c5.text= code.text.substring(4,5)
                        c6.text= ""
                    }
                    code.text.length== 6 -> {
                        c1.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c1.isCursorVisible= false
                        c2.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c2.isCursorVisible= false
                        c3.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c3.isCursorVisible= false
                        c4.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c4.isCursorVisible= false
                        c5.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c5.isCursorVisible= false
                        c6.background= ContextCompat.getDrawable(this@PhoneAuth, R.drawable.stroke1)
                        c6.isCursorVisible= false
                        c1.text= code.text.substring(0,1)
                        c2.text= code.text.substring(1,2)
                        c3.text= code.text.substring(2,3)
                        c4.text= code.text.substring(3,4)
                        c5.text= code.text.substring(4,5)
                        c6.text= code.text.substring(5,6)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}

        })
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