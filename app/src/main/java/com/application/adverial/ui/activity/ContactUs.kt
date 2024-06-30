package com.application.adverial.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import kotlinx.android.synthetic.main.activity_contact_us.contactUsRoot
import kotlinx.android.synthetic.main.activity_contact_us.contactUs_text
import kotlinx.android.synthetic.main.activity_contact_us.newAdCategory_back7

class ContactUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        Tools().rotateLayout(this,newAdCategory_back7)
        Tools().changeViewFromTheme(this,contactUsRoot)
        Tools().setBasedLogo(this, R.id.imageView55)
        pageInit()
    }

    private fun pageInit(){

    }

    fun send(view: View){
        if(contactUs_text.text.isNotBlank()){
            val repo= Repository(this)
            repo.contactUs(contactUs_text.text.toString())
            repo.getContactUsData().observe(this, {
                if(it.status){
                    Toast.makeText(this, getString(R.string.messageSent), Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        }else Toast.makeText(this, R.string.messageEmpty, Toast.LENGTH_SHORT).show()
    }

    fun website(view: View){
        Tools().openBrowser(this, "https://www.adverial.net")
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