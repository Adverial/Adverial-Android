package com.application.adverial.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_new_ad_map.*

class NewAdMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: HuaweiMap
    private lateinit var mMapView: MapView
    private var lat = ""
    private var lon = ""
    private var adId = ""
    private var country = ""
    private var city = ""
    private var district = ""

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    private var mMarker: Marker? = null

    // Fixed values for camera settings
    private val cameraLat = 33.3152
    private val cameraLng = 44.3661
    private val cameraZoom = 10f
    private val cameraTilt = 2.5f
    private val cameraBearing = 2.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad_map)

        Tools().setBasedLogo(this, R.id.imageView47)
        pageInit()
        Tools().locationRequest(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("TAG", "sdk >= 23 M")
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val strings = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        }

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
       // myLocation()
        mMapView = findViewById(R.id.newAdMap_map)
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }

    private fun pageInit() {
        adId = intent.getStringExtra("adId") ?: "34"
        country = intent.getStringExtra("country") ?: "default_country"
        city = intent.getStringExtra("city") ?: "default_city"
        district = intent.getStringExtra("district") ?: "default_district"
    }

    private fun zoomToAddress(latLng: LatLng, targetZoom: Float) {
        val currentZoom = map.cameraPosition.zoom
        val zoomStep = 0.5f

        var zoom = currentZoom
        while (zoom < targetZoom) {
            zoom += zoomStep
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
            map.animateCamera(cameraUpdate)
            Thread.sleep(250)
        }
    }

    fun addMarker() {
        if (mMarker != null) {
            mMarker?.remove()
        }
        val options = MarkerOptions()
            .position(LatLng(33.3152, 44.3661))
            .title("My Location")
            .snippet("This is a snippet!")
            .draggable(true)
        mMarker = map.addMarker(options)
    }

        override fun onMapReady(huaweiMap: HuaweiMap) {
    map = huaweiMap

    // Enable location if permission is granted
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        map.isMyLocationEnabled = true
    }

    // Set default camera position and zoom level
    val initialCameraPosition = CameraPosition.Builder()
        .target(LatLng(0.0, 0.0)) // Center the map at the equator
        .zoom(2f) // Set zoom level to show the entire world
        .build()

    map.moveCamera(
        com.huawei.hms.maps.CameraUpdateFactory.newCameraPosition(initialCameraPosition)
    )

    // Enable zoom controls and gestures
    map.uiSettings.isZoomControlsEnabled = true
    map.uiSettings.isZoomGesturesEnabled = true

    // Add listener to dynamically adjust tilt based on zoom level
    map.setOnCameraMoveListener {
        val currentZoom = map.cameraPosition.zoom

        // Adjust tilt only when the zoom level changes significantly
        if (currentZoom > 10) {
            val newTilt = currentZoom + 1
            if (map.cameraPosition.tilt != newTilt) {
                val updatedCameraPosition = CameraPosition.Builder(map.cameraPosition)
                    .tilt(newTilt)
                    .build()
                map.moveCamera(CameraUpdateFactory.newCameraPosition(updatedCameraPosition))
            }
        }
    }

    // Add listener to handle actions when the camera stops moving
    map.setOnCameraIdleListener {
        // Ensure that the map is given time to load tiles at high zoom levels
        // Avoid clearing the map or resetting the camera during idle state
    }
}

    private fun myLocation() {
        SmartLocation.with(this).location().start {
            if (lat.isBlank() && lon.isBlank()) {
                lat = it.latitude.toString()
                lon = it.longitude.toString()
                val latLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun setCameraPosition() {
        val latLng = LatLng(cameraLat, cameraLng)
        val cameraPosition = CameraPosition.builder()
            .target(latLng)
            .zoom(cameraZoom)
            .bearing(cameraBearing)
            .tilt(cameraTilt)
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        map.moveCamera(cameraUpdate)
    }

    fun next(view: View) {
        // toast lat and lon
        Toast.makeText(this, "lat: $lat, lon: $lon", Toast.LENGTH_SHORT).show()
//        if (lat.isNotBlank() && lon.isNotBlank()) {
//            lottie12.visibility = View.VISIBLE
//            Tools().viewEnable(this.window.decorView.rootView, false)
//            val repo = Repository(this)
//            repo.addAdLocation(adId, country, city, district, lat, lon)
//            repo.getAddAdLocationData().observe(this) {
//                lottie12.visibility = View.GONE
//                Tools().viewEnable(this.window.decorView.rootView, true)
//                if (it.status) {
//                    val intent = Intent(this, NewAdImages::class.java)
//                    intent.putExtra("adId", adId)
//                    startActivity(intent)
//                }
//            }
//        } else Toast.makeText(this, resources.getString(R.string.locationNotFound), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        mMapView.onResume()
    }

    fun back(view: View) {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle: Bundle? = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mMapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}