package com.application.adverial.ui.activity

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.dialog.DropList
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.activity_new_ad_category.*

class NewAdAddress : AppCompatActivity() {

    private val itemList= ArrayList<com.application.adverial.ui.model.DropList>()
    private var country= ""
    private var city= ""
    private var district= ""
    private var adId= ""
    private var nextPage= true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        Tools().rotateLayout(this,home_menu3)
        Tools().changeViewFromTheme(this,addressRoot)

        pageInit()
    }

    private fun pageInit(){
        Tools().rotateLayout(this,arrow1)
        Tools().rotateLayout(this,arrow2)
        Tools().rotateLayout(this,arrow3)
        adId= intent.getStringExtra("adId")!!
        newAdAddress_countrySelect.setOnClickListener{
            lottie9.visibility= View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo= Repository(this)
            repo.country()
            repo.getCountryData().observe(this, {
                lottie9.visibility= View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                itemList.clear()
                for(i in it.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(it.data[i].name, it.data[i].id.toString())) }
                val dialog= DropList(itemList, resources.getString(R.string.address_country))
                dialog.show(supportFragmentManager, "DropList")
                dialog.getStatus().observe(this, { result ->
                    newAdAddress_country.text= result.name
                    country= result.id
                    city= ""
                    district= ""
//                    Log.d("ddd", country)
                    newAdAddress_district.setTextColor(ContextCompat.getColor(this, R.color.gray))
                    newAdAddress_city.setTextColor(ContextCompat.getColor(this, R.color.gray))
                    newAdAddress_district.text= resources.getString(R.string.new_ad_info_select)
                    newAdAddress_city.text= resources.getString(R.string.new_ad_info_select)
                    newAdAddress_country.setTextColor(ContextCompat.getColor(this, R.color.white))
                })
            })
        }
        newAdAddress_citySelect.setOnClickListener{
            val repo= Repository(this)
            if(country.isNotBlank()){
                lottie9.visibility= View.VISIBLE
                Tools().viewEnable(this.window.decorView.rootView, false)
                repo.city(country)
                repo.getCityData().observe(this, {
                    lottie9.visibility= View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    itemList.clear()
                    for(i in it.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(it.data[i].name, it.data[i].id.toString())) }
                    val dialog= DropList(itemList, resources.getString(R.string.address_province))
                    dialog.show(supportFragmentManager, "DropList")
                    dialog.getStatus().observe(this, { result ->
                        newAdAddress_city.text= result.name
                        city= result.id
                        district= ""
//                        Log.d("ddd", city)
                        newAdAddress_district.setTextColor(ContextCompat.getColor(this, R.color.gray))
                        newAdAddress_district.text= resources.getString(R.string.new_ad_info_select)
                        newAdAddress_city.setTextColor(ContextCompat.getColor(this, R.color.white))
                    })
                })
            }
        }
        newAdAddress_DistrictSelect.setOnClickListener{
            val repo= Repository(this)
            if(city.isNotBlank()){
                lottie9.visibility= View.VISIBLE
                Tools().viewEnable(this.window.decorView.rootView, false)
                repo.district(city)
                repo.getDistrictData().observe(this, {
                    lottie9.visibility= View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    itemList.clear()
                    for(i in it.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(it.data[i].name, it.data[i].id.toString())) }
                    val dialog= DropList(itemList, resources.getString(R.string.address_district))
                    dialog.show(supportFragmentManager, "DropList")
                    dialog.getStatus().observe(this, { result ->
                        newAdAddress_district.text= result.name
                        district= result.id
//                        Log.d("ddd", district)
                        newAdAddress_district.setTextColor(ContextCompat.getColor(this, R.color.white))
                    })
                })
            }
        }
    }

    fun next(view: View){
        if(country.isNotBlank() && city.isNotBlank() && district.isNotBlank()){
            val permissionListener: PermissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    if(nextPage){
                        nextPage= false
                        val intent= Intent(this@NewAdAddress, NewAdMap::class.java)
                        intent.putExtra("adId", adId)
                        intent.putExtra("country", country)
                        intent.putExtra("city", city)
                        intent.putExtra("district", district)
                        startActivity(intent)
                    }
                }
                override fun onPermissionDenied(deniedPermissions: List<String?>) {

                }
            }
            TedPermission.with(this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).check()
        }else Toast.makeText(this, resources.getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
    }

    fun back(view: View){
        finish()
    }

    override fun onResume() {
        super.onResume()
        nextPage= true
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}