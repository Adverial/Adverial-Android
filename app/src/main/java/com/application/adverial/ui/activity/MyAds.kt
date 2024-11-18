package com.application.adverial.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityMyAdsBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.MyAdsAdapter

class MyAds : AppCompatActivity() {

    private lateinit var binding: ActivityMyAdsBinding
    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.newAdCategoryBack4)
        Tools().changeViewFromTheme(this, binding.myAdsRoot)

        pageInit()
        typeCheck()
    }

    private fun pageInit() {
        type = getSharedPreferences("data", 0).getString("type", "deactive") ?: "deactive"
        binding.myAdsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun typeCheck() {
        if (type == "active") {
            binding.myAdsActive.apply {
                background = ContextCompat.getDrawable(this@MyAds, R.drawable.rounded_full3)
                backgroundTintList = ContextCompat.getColorStateList(this@MyAds, R.color.yellow)
                setTextColor(ContextCompat.getColor(this@MyAds, R.color.black))
            }
            binding.myAdsDeactive.apply {
                background = ContextCompat.getDrawable(this@MyAds, R.drawable.rounded_full6)
                backgroundTintList = ContextCompat.getColorStateList(this@MyAds, R.color.yellow)
                setTextColor(ContextCompat.getColor(this@MyAds, R.color.yellow))
            }
            activeAds()
        } else {
            binding.myAdsDeactive.apply {
                background = ContextCompat.getDrawable(this@MyAds, R.drawable.rounded_full3)
                backgroundTintList = ContextCompat.getColorStateList(this@MyAds, R.color.yellow)
                setTextColor(ContextCompat.getColor(this@MyAds, R.color.black))
            }
            binding.myAdsActive.apply {
                background = ContextCompat.getDrawable(this@MyAds, R.drawable.rounded_full6)
                backgroundTintList = ContextCompat.getColorStateList(this@MyAds, R.color.yellow)
                setTextColor(ContextCompat.getColor(this@MyAds, R.color.yellow))
            }
            deactiveAds()
        }
    }

    private fun activeAds() {
        binding.lottie8.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        val repo = Repository(this)
        repo.activeAds()
        repo.getActiveAdsData().observe(this) {
            binding.lottie8.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
            val adapter = if (it.status) {
                MyAdsAdapter(it.data as ArrayList<Ad>)
            } else {
                MyAdsAdapter(ArrayList())
            }
            binding.myAdsRecyclerView.adapter = adapter
            adapter.getResult().observe(this) {
                reloadActivity()
            }
        }
    }

    private fun deactiveAds() {
        binding.lottie8.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        val repo = Repository(this)
        repo.deactiveAds()
        repo.getDeactiveAdsData().observe(this) {
            binding.lottie8.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
            val adapter = if (it.status) {
                MyAdsAdapter(it.data as ArrayList<Ad>)
            } else {
                MyAdsAdapter(ArrayList())
            }
            binding.myAdsRecyclerView.adapter = adapter
            adapter.getResult().observe(this) {
                reloadActivity()
            }
        }
    }

    private fun reloadActivity() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    fun active(view: View) {
        type = "active"
        getSharedPreferences("data", 0).edit().putString("type", "active").apply()
        typeCheck()
    }

    fun deactive(view: View) {
        type = "deactive"
        getSharedPreferences("data", 0).edit().putString("type", "deactive").apply()
        typeCheck()
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") {
                View.LAYOUT_DIRECTION_LTR
            } else {
                View.LAYOUT_DIRECTION_RTL
            }
    }
}
