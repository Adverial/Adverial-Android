package com.application.adverial

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import com.application.adverial.remote.Repository
import com.application.adverial.service.SharedPrefManager
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgr: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)

        Log.e("", getSharedPreferences("user", 0).getString("token", "")!!)
    }

    val base = "https://test.adverial.com/images/backgrounds/"

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

        val base = "https://test.adverial.com/images/backgrounds/"
        val repo = Repository(this)
        repo.getBackground()
        repo.backgroundLV.observe(this) {
            if (it.status == true) {
                sharedPrefManager.saveBackground(base+it.data?.image)
                Tools().goto(this, Home(), false)
            }
        }
    }

}