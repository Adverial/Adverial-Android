package com.application.adverial.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.application.adverial.R
import com.application.adverial.service.Tools
import kotlinx.android.synthetic.main.activity_phone.*

class Phone : AppCompatActivity() {

    private var type= ""
    private var idArray= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        Tools().changeViewFromTheme(this,phoneRoot)

        pageInit()
    }

    private fun pageInit(){
        type= intent.getStringExtra("type")!!
        idArray= intent.getStringExtra("idArray")!!
        phoneAuth_phoneNumber.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(phoneAuth_phoneNumber.text.length == 10){
                    phoneAuth_ok.backgroundTintList= ContextCompat.getColorStateList(this@Phone, R.color.red)
                }else{
                    phoneAuth_ok.backgroundTintList= ContextCompat.getColorStateList(this@Phone, R.color.red_cover)
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    fun ok(view: View){
        if(phoneAuth_phoneNumber.text.length == 10){
            val phone= countryCodePicker.fullNumber + phoneAuth_phoneNumber.text.toString()
            val intent= Intent(this, NewAdInfo::class.java)
            intent.putExtra("type", type)
            intent.putExtra("idArray", idArray)
            intent.putExtra("phone", phone)
            startActivity(intent)
            finish()
        }
    }

    fun clear(view: View){
        phoneAuth_phoneNumber.setText("")
        phoneAuth_ok.backgroundTintList= ContextCompat.getColorStateList(this@Phone, R.color.red_cover)
    }

    fun back(view: View){
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