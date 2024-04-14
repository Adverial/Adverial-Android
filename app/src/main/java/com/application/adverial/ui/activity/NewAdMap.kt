package com.application.adverial.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_new_ad_map.lottie12

class NewAdMap : AppCompatActivity(), OnMapReadyCallback {

    private var mapFragment: SupportMapFragment? = null
    private lateinit var map: GoogleMap
    private var lat= ""
    private var lon= ""
    private var adId= ""
    private var country= ""
    private var city= ""
    private var district= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad_map)

        pageInit()
        Tools().locationRequest(this)
        myLocation()
    }

    private fun pageInit(){
        adId= intent.getStringExtra("adId")!!
        country= intent.getStringExtra("country")!!
        city= intent.getStringExtra("city")!!
        district= intent.getStringExtra("district")!!
        mapFragment = supportFragmentManager.findFragmentById(R.id.newAdMap_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map= p0
        Tools().gotoMyCountry(map)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){return }
        map.isMyLocationEnabled= true
        val locationButton= (mapFragment?.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp= locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,100)
        map.setOnCameraChangeListener{
            lat= it.target.latitude.toString()
            lon= it.target.longitude.toString()
//            Log.d("ddd", "lat:$lat   lon:$lon")
        }
    }

    private fun myLocation(){
        SmartLocation.with(this).location().start {
            if(lat.isBlank() && lon.isBlank()) {
                lat= it.latitude.toString()
                lon= it.longitude.toString()
                val latLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//                Log.d("ddd", "lat:$lat   lon:$lon")
            }
        }
    }

    fun next(view: View){
        if(lat.isNotBlank() && lon.isNotBlank()){
            lottie12.visibility= View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo= Repository(this)
            repo.addAdLocation(adId, country, city, district, lat, lon)
            repo.getAddAdLocationData().observe(this, {
                lottie12.visibility= View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                if(it.status){
                    val intent= Intent(this, NewAdImages::class.java)
                    intent.putExtra("adId", adId)
                    startActivity(intent)
                }
            })
        }else Toast.makeText(this, resources.getString(R.string.locationNotFound), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}