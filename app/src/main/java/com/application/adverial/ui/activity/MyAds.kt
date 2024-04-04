package com.application.adverial.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.MyAdsAdapter
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.activity_my_ads.*
import kotlinx.android.synthetic.main.activity_new_ad_category.*

class MyAds : AppCompatActivity() {

    private var type=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ads)
        Tools().rotateLayout(this,newAdCategory_back4)
        Tools().changeViewFromTheme(this,myAdsRoot)

        pageInit()
        typeCheck()
    }

    private fun pageInit(){
        type= getSharedPreferences("data", 0).getString("type", "deactive")!!
        myAds_recyclerView.layoutManager= LinearLayoutManager(this)
    }

    private fun typeCheck(){
        if(type == "active"){
            myAds_active.background= ContextCompat.getDrawable(this, R.drawable.rounded_full3)
            myAds_active.backgroundTintList= ContextCompat.getColorStateList(this, R.color.yellow)
            myAds_active.setTextColor(ContextCompat.getColor(this, R.color.black))
            myAds_deactive.background= ContextCompat.getDrawable(this, R.drawable.rounded_full6)
            myAds_deactive.backgroundTintList= ContextCompat.getColorStateList(this, R.color.yellow)
            myAds_deactive.setTextColor(ContextCompat.getColor(this, R.color.yellow))
            activeAds()
        }else{
            myAds_deactive.background= ContextCompat.getDrawable(this, R.drawable.rounded_full3)
            myAds_deactive.backgroundTintList= ContextCompat.getColorStateList(this, R.color.yellow)
            myAds_deactive.setTextColor(ContextCompat.getColor(this, R.color.black))
            myAds_active.background= ContextCompat.getDrawable(this, R.drawable.rounded_full6)
            myAds_active.backgroundTintList= ContextCompat.getColorStateList(this, R.color.yellow)
            myAds_active.setTextColor(ContextCompat.getColor(this, R.color.yellow))
            deactiveAds()
        }
    }

    private fun activeAds(){
        lottie8.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        val repo= Repository(this)
        repo.activeAds()
        repo.getActiveAdsData().observe(this, {
            lottie8.visibility= View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
            if(it.status){
                val adapter= MyAdsAdapter(it.data as ArrayList<Ad>)
                myAds_recyclerView.adapter= adapter
                adapter.getResult().observe(this, {
                    val intent = intent
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    finish()
                    overridePendingTransition(0, 0)
                    startActivity(intent)
                })
            }else{
                val empty= ArrayList<Ad>()
                val adapter= MyAdsAdapter(empty)
                myAds_recyclerView.adapter= adapter
            }
        })
    }

    private fun deactiveAds(){
        lottie8.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        val repo= Repository(this)
        repo.deactiveAds()
        repo.getDeactiveAdsData().observe(this, {
            lottie8.visibility= View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
            if(it.status){
                val adapter= MyAdsAdapter(it.data as ArrayList<Ad>)
                myAds_recyclerView.adapter= adapter
                adapter.getResult().observe(this, {
                    val intent = intent
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    finish()
                    overridePendingTransition(0, 0)
                    startActivity(intent)
                })
            }else{
                val empty= ArrayList<Ad>()
                val adapter= MyAdsAdapter(empty)
                myAds_recyclerView.adapter= adapter
            }
        })
    }

    fun active(view: View){
        type= "active"
        getSharedPreferences("data", 0).edit().putString("type", "active").apply()
        typeCheck()
    }

    fun deactive(view: View){
        type= "deactive"
        getSharedPreferences("data", 0).edit().putString("type", "deactive").apply()
        typeCheck()
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