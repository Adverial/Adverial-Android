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
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_new_ad_map.*

class NewAdMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: HuaweiMap
    private lateinit var mMapView: MapView
    private var lat= ""
    private var lon= ""
    private var adId= ""
    private var country= ""
    private var city= ""
    private var district= ""

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }
    private var mMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad_map)

        Tools().setBasedLogo(this, R.id.imageView47)
        pageInit()
        Tools().locationRequest(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("TAG", "sdk >= 23 M")
            // Check whether your app has the specified  and whether the app operation corresponding to the permission is allowed.
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request permissions for your app.
                val strings = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                // Request permissions.
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        }

        // Initialize Huawei MapView
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        myLocation()
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
    val zoomStep = 0.5f // Adjust zoom step as needed

    // Use a loop to incrementally zoom in
    var zoom = currentZoom
    while (zoom < targetZoom) {
        zoom += zoomStep
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        map.animateCamera(cameraUpdate)

        // Add a delay to allow the camera to animate
        Thread.sleep(250) // Adjust delay as needed
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
// Call this method with the desired address coordinates and zoom level
override fun onMapReady(huaweiMap: HuaweiMap) {
    map = huaweiMap
    Post().gotoMyCountry(map)
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    map.isMyLocationEnabled = true


    // Enable the my-location layer.
    map.isMyLocationEnabled = true

    map.uiSettings.isMyLocationButtonEnabled = true

    map.setOnMarkerClickListener { marker ->
        Toast.makeText(applicationContext, "onMarkerClick:${marker.title}", Toast.LENGTH_SHORT).show()
        false
    }
    map.setOnMyLocationButtonClickListener {

        false
    }
    map.setOnMarkerDragListener(object : HuaweiMap.OnMarkerDragListener {
        override fun onMarkerDragStart(marker: Marker) {
            Log.i("TAG", "onMarkerDragStart: ${marker.position}")
        }

        override fun onMarkerDrag(marker: Marker) {
            Log.i("TAG", "onMarkerDrag: ${marker.position}")
        }

        override fun onMarkerDragEnd(marker: Marker) {
            Log.i("TAG", "onMarkerDragEnd: ${marker.position}")
        }
    })

    map.setOnMarkerDragListener(object : HuaweiMap.OnMarkerDragListener {
        override fun onMarkerDragStart(marker: Marker) {
         //  Toast.makeText(applicationContext, "onMarkerDragStart:${marker.position}", Toast.LENGTH_SHORT).show()
        }

        override fun onMarkerDrag(marker: Marker) {
           // Toast.makeText(applicationContext, "onMarkerDrag:${marker.position}", Toast.LENGTH_SHORT).show()
        }

        override fun onMarkerDragEnd(marker: Marker) {
           // Toast.makeText(applicationContext, "onMarkerDragEnd:${marker.position}", Toast.LENGTH_SHORT).show()
        }
    })
    addMarker()
    // Set the initial camera position with iraq coordinates
    val latLng = LatLng(33.3152, 44.3661)
    val initialZoom = 5f
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, initialZoom)
    map.animateCamera(cameraUpdate)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(33.3152, 44.3661), 5f))


    // Dynamically zoom to the address
    val targetZoom = 5f
    zoomToAddress(latLng, targetZoom)
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

    fun next(view: View) {
       // Toast.makeText(this, "lat: $lat, lon: $lon", Toast.LENGTH_SHORT).show()
        if (lat.isNotBlank() && lon.isNotBlank()) {
            lottie12.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = Repository(this)
            repo.addAdLocation(adId, country, city, district, lat, lon)
            repo.getAddAdLocationData().observe(this) {
                lottie12.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                if (it.status) {
                    val intent = Intent(this, NewAdImages::class.java)
                    intent.putExtra("adId", adId)
                    startActivity(intent)
                }
            }
        } else Toast.makeText(this, resources.getString(R.string.locationNotFound), Toast.LENGTH_SHORT).show()
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

    // MapView Lifecycle Methods
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