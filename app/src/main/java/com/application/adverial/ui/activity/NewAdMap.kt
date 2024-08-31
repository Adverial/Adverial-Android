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
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_new_ad_map.lottie12

class NewAdMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var huaweiMap: HuaweiMap
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
        mapView = findViewById(R.id.newAdMap_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        pageInit()
        Tools().locationRequest(this)
        myLocation()

    }

    private fun pageInit(){
        adId = intent.getStringExtra("adId") ?: ""
        country = intent.getStringExtra("country") ?: ""
        city = intent.getStringExtra("city") ?: ""
        district = intent.getStringExtra("district") ?: ""


    }

    override fun onMapReady(map: HuaweiMap) {
        huaweiMap = map

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        huaweiMap.isMyLocationEnabled = true
        myLocation()
    }

    private fun myLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (lat.isBlank() && lon.isBlank()) {
                        lat = location.latitude.toString()
                        lon = location.longitude.toString()
                        val latLng = LatLng(location.latitude, location.longitude)

                        // Add a marker on the user's current location
                        val markerOptions = MarkerOptions().position(latLng).title("My Location")
                        huaweiMap.addMarker(markerOptions)

                        // Zoom the map to the user's location
                        huaweiMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                        // Optionally, you can stop location updates after getting the location
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun next(view: View){

        // alert lon and lat
        Toast.makeText(this, "lat: $lat, lon: $lon", Toast.LENGTH_SHORT).show()
        if(lat.isNotBlank() && lon.isNotBlank()){
            lottie12.visibility= View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo= Repository(this)
            repo.addAdLocation(adId, country, city, district, lat, lon)
            repo.getAddAdLocationData().observe(this) { result ->
                lottie12.visibility= View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                if(result.status){
                    val intent= Intent(this, NewAdImages::class.java)
                    intent.putExtra("adId", adId)
                    startActivity(intent)
                }
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.locationNotFound), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    fun back(view: View){ finish() }
}