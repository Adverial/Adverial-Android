package com.application.adverial.ui.activity

// Import Huawei-specific classes instead of Google Maps
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
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.LatLng
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_new_ad_map.lottie12

class NewAdMap : AppCompatActivity(), OnMapReadyCallback {

    private var mapFragment: SupportMapFragment? = null
    private lateinit var map: HuaweiMap
    private var lat= ""
    private var lon= ""
    private var adId= ""
    private var country= ""
    private var city= ""
    private var district= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad_map)

        Tools().setBasedLogo(this, R.id.imageView47)
        pageInit()
        Tools().locationRequest(this)
        myLocation()
    }

    private fun pageInit() {
        adId = intent.getStringExtra("adId") ?: "defaultAdId"
country = intent.getStringExtra("country") ?: "defaultCountry"
city = intent.getStringExtra("city") ?: "defaultCity"
district = intent.getStringExtra("district") ?: "defaultDistrict"

        // Initialize HuaweiMapFragment instead of Google Map's SupportMapFragment
        val huaweiMapOptions = HuaweiMapOptions().zoomControlsEnabled(false)
            .compassEnabled(true)
            .zoomGesturesEnabled(true)
            .scrollGesturesEnabled(true)
            .rotateGesturesEnabled(false)
            .tiltGesturesEnabled(true)
            .zOrderOnTop(true)
            .useViewLifecycleInFragment(true)
            .liteMode(true)
            .minZoomPreference(3f)
            .maxZoomPreference(13f)
// Set a standard map.
        huaweiMapOptions.mapType(HuaweiMap.MAP_TYPE_NORMAL)
        var mSupportMapFragment = SupportMapFragment.newInstance(huaweiMapOptions)
        mSupportMapFragment = supportFragmentManager.findFragmentById(R.id.newAdMap_map) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(huaweiMap: HuaweiMap) {
        map = huaweiMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map.isMyLocationEnabled = true
        map.setOnCameraIdleListener {
            val target = map.cameraPosition.target
            lat = target.latitude.toString()
            lon = target.longitude.toString()
        }
    }


    private fun myLocation() {
        // Using SmartLocation for retrieving the location
        SmartLocation.with(this).location().start { location ->
            if (lat.isBlank() && lon.isBlank()) {
                lat = location.latitude.toString()
                lon = location.longitude.toString()
                val latLng = LatLng(location.latitude, location.longitude)

                // Move camera on Huawei Map
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    fun next(view: View) {

        //toast the lan and lon
       // Toast.makeText(this, "lat: $lat, lon: $lon", Toast.LENGTH_SHORT).show()
        if (lat.isNotBlank() && lon.isNotBlank()) {
            lottie12.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)

            val repo = Repository(this)
            repo.addAdLocation(adId, country, city, district, lat, lon)
            repo.getAddAdLocationData().observe(this, {
                lottie12.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)

                if (it.status) {
                    val intent = Intent(this, NewAdImages::class.java)
                    intent.putExtra("adId", adId)
                    startActivity(intent)
                }
            })
        } else {
            Toast.makeText(this, resources.getString(R.string.locationNotFound), Toast.LENGTH_SHORT).show()
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
    }

    fun back(view: View) {
        finish()
    }
}
