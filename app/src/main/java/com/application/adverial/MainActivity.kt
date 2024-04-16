package com.application.adverial

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.service.SharedPrefManager
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgr: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)


    }



    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection =
            View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        val sharedPrefManager: SharedPrefManager by lazy {
            SharedPrefManager(this)
        }

//        val base = "https://test.adverial.com/images/backgrounds/"
//        val repo = Repository(this)
//        repo.getBackground()
//        repo.backgroundLV.observe(this) {
//            if (it.status == true) {
//                Log.d("background", it.data?.image.toString())
                sharedPrefManager.saveBackground("")
                Tools().goto(this, Home(), false)
//            }
//        }
    }

}