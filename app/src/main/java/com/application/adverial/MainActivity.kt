package com.application.adverial

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.service.Tools
import com.application.adverial.ui.navigation.Home
import com.application.adverial.utils.DialogUtils
import com.application.adverial.utils.NetworkUtils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imgr: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Enable cleartext traffic
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .permitNetwork().build()
            )
        }
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

//        val sharedPrefManager: SharedPrefManager by lazy {
//            SharedPrefManager(this)
//        }


        if (NetworkUtils.isNetworkAvailable(this)) {
                Tools().goto(this, Home(), false)
        } else {
            DialogUtils.showNoInternetDialog(this)
            finish()
        }

    }
}
