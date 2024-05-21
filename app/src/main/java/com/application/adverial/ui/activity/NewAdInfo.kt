package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.CategoryOptionsData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NewAdInfoAdapter
import kotlinx.android.synthetic.main.activity_new_ad_info.lottie11
import kotlinx.android.synthetic.main.activity_new_ad_info.newAdCategory_back3
import kotlinx.android.synthetic.main.activity_new_ad_info.newAdInfoRoot
import kotlinx.android.synthetic.main.activity_new_ad_info.newAdInfo_recyclerView

class NewAdInfo : AppCompatActivity() {

    private var type= ""
    private var idArray= ""
    private var itemList= ArrayList<CategoryOptionsData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_ad_info)
        Tools().rotateLayout(this,newAdCategory_back3)
        Tools().changeViewFromTheme(this,newAdInfoRoot)

        if (Tools().getTheme(this)=="dark") {
            findViewById<View>(R.id.imageView24).setBackgroundResource(R.drawable.test1)
        }
        else
        {
            findViewById<View>(R.id.imageView24).setBackgroundResource(R.drawable.logo_dark)
        }
        pageInit()
    }

    private fun pageInit(){
        lottie11.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        getSharedPreferences("newAdImages", 0).edit().clear().apply()
        getSharedPreferences("newAd", 0).edit().clear().apply()
        getSharedPreferences("newAdOptions", 0).edit().clear().apply()
        type= intent.getStringExtra("type")!!
        idArray= intent.getStringExtra("idArray")!!
        newAdInfo_recyclerView.layoutManager= LinearLayoutManager(this)
        val repo= Repository(this)
        repo.categoryOptions(type)
        repo.getCategoryOptionsData().observe(this, {
            itemList.add(CategoryOptionsData(1,"", resources.getString(R.string.new_ad_info_title), "", listOf()))
            itemList.add(CategoryOptionsData(1,"", resources.getString(R.string.new_ad_info_price), "", listOf()))
            itemList.add(CategoryOptionsData(1,"", resources.getString(R.string.new_ad_info_currency), "", listOf()))
            for(i in it.data.indices) itemList.add(it.data[i])
            itemList.add(CategoryOptionsData(1,"", resources.getString(R.string.new_ad_info_description), "", listOf()))
            val adapter= NewAdInfoAdapter(itemList)
            newAdInfo_recyclerView.adapter= adapter
            adapter.getResult().observe(this, {
                if(it == "show"){
                    lottie11.visibility= View.VISIBLE
                    Tools().viewEnable(this.window.decorView.rootView, false)
                }else if(it == "hide"){
                    lottie11.visibility= View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                }
            })
            newAdInfo_recyclerView.setHasFixedSize(true)
            newAdInfo_recyclerView.setItemViewCacheSize(itemList.size)
            lottie11.visibility= View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
        })
    }

    fun next(view: View){
        val data= getSharedPreferences("newAd", 0)
        if(data.getString("title", "") != ""){
            if(data.getString("price", "") != ""){
                if(data.getString("currency", "") != ""){
                    lottie11.visibility= View.VISIBLE
                    Tools().viewEnable(this.window.decorView.rootView, false)
                    val repo= Repository(this)
                    repo.addAdInfo(data.getString("title", "")!!, data.getString("price", "")!!, data.getString("description", "")!!, idArray, data.getString("currency", "")!!)
                    repo.getAddAdInfoData().observe(this, {
                        lottie11.visibility= View.GONE
                        Tools().viewEnable(this.window.decorView.rootView, true)
                        if(it.status){
                            val intent= Intent(this, NewAdAddress::class.java)
                            intent.putExtra("adId", it.data.ad_id.toString())
                            startActivity(intent)
                        }
                    })
                }else Toast.makeText(this, resources.getString(R.string.enterCurrency), Toast.LENGTH_SHORT).show()
            }else Toast.makeText(this, resources.getString(R.string.enterPrice), Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this, resources.getString(R.string.enterTitle), Toast.LENGTH_SHORT).show()
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