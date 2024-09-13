package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.dialog.AlertDialog1
import com.application.adverial.ui.navigation.Home
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_my_account.deleteAccount
import kotlinx.android.synthetic.main.activity_my_account.imageView62
import kotlinx.android.synthetic.main.activity_my_account.myAccountRoot
import kotlinx.android.synthetic.main.activity_my_account.myaccount_firstname
import kotlinx.android.synthetic.main.activity_my_account.myaccount_phone
import kotlinx.android.synthetic.main.activity_signup.lottie14
import java.util.regex.Pattern

class MyAccount : AppCompatActivity() {

    private var lastPhone= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)
        Tools().changeViewFromTheme(this,myAccountRoot)
        Tools().setBasedLogo(this, R.id.imageView42)
        //disable the phone number field
        myaccount_phone.isEnabled= false
        imageView62.visibility= View.GONE
        pageInit()



    }

    private fun pageInit(){
        lottie14.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        val repo= Repository(this)
        repo.user()
        repo.getUserData().observe(this) {
            lottie14.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
            if (it.status) {
                lastPhone = it.data.whatsappNumber ?: ""
                myaccount_firstname.setText(it.data.name ?: "")
               // countryCodePicker.setCountryForPhoneCode(it.data.whatsappNumber?.substring(0, 4)?.toInt() ?: 0)
                myaccount_phone.setText(it.data.whatsappNumber)
                // make the phone number field not editable and disable color background
                myaccount_phone.isEnabled= false

            }
        }

        deleteAccount.setOnClickListener {
            val dialog= AlertDialog1(getString(R.string.attention), getString(R.string.deleteAccount))
            dialog.show(supportFragmentManager, "AlertDialog1")
            dialog.getStatus().observe(this){ittt->
                if(ittt == "ok"){
                    val dialog1= AlertDialog1(getString(R.string.attention), getString(R.string.deleteAccount1))
                    dialog1.show(supportFragmentManager, "AlertDialog1")
                    dialog1.getStatus().observe(this){ itt->
                        if(itt == "ok"){
                            lottie14.visibility= View.VISIBLE
                            Tools().viewEnable(this.window.decorView.rootView, false)
                            with(Repository(this)){
                                deleteAccount()
                                deleteAccount.observe(this@MyAccount){
                                    lottie14.visibility= View.GONE
                                    Tools().viewEnable(this@MyAccount.window.decorView.rootView, true)
                                    if(it.status){
                                        getSharedPreferences("user", 0).edit().clear().apply()
                                        val intent= Intent(this@MyAccount, Home::class.java)
                                        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun save(view: View) {
        if (myaccount_phone.length() == 14) {
            lottie14.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)

            val repo = Repository(this)
            // Updated to remove name and email from userUpdate call
            repo.userUpdate(phone = myaccount_phone.text.toString(),name = myaccount_firstname.text.toString(),
                email = "", last_name = "")

            repo.getUserUpdateData().observe(this) { updateResult ->
                if (updateResult.status) {
                    lottie14.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                        Toast.makeText(this, resources.getString(R.string.accountUpdateDone), Toast.LENGTH_SHORT).show()
                        finish()
                }
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    fun clear(view: View){
        myaccount_phone.setText("")
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