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
import com.application.adverial.databinding.ActivityNewAdMapBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import io.nlopez.smartlocation.SmartLocation

class NewAdMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityNewAdMapBinding
    private lateinit var map: GoogleMap
    private var lat = ""
    private var lon = ""
    private var adId = ""
    private var country = ""
    private var city = ""
    private var district = ""
    private var mapFragment: SupportMapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().setBasedLogo(this, R.id.imageView47)

        pageInit()
        Tools().locationRequest(this)
        myLocation()
    }

    private fun pageInit() {
        adId = intent.getStringExtra("adId") ?: ""
        country = intent.getStringExtra("country") ?: ""
        city = intent.getStringExtra("city") ?: ""
        district = intent.getStringExtra("district") ?: ""
        mapFragment = supportFragmentManager.findFragmentById(R.id.newAdMap_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Tools().gotoMyCountry(map)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return

        map.isMyLocationEnabled = true

        // Move the location button to the bottom right corner
        val locationButton = (mapFragment?.view?.findViewById<View>("1".toInt())?.parent as View)
            .findViewById<View>("2".toInt())
        val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 30, 100)

        // Update location when camera changes
        map.setOnCameraChangeListener { cameraPosition ->
            lat = cameraPosition.target.latitude.toString()
            lon = cameraPosition.target.longitude.toString()
        }
    }

    private fun myLocation() {
        SmartLocation.with(this).location().start { location ->
            if (lat.isBlank() && lon.isBlank()) {
                lat = location.latitude.toString()
                lon = location.longitude.toString()
                val latLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    fun next(view: View) {
        if (lat.isNotBlank() && lon.isNotBlank()) {
            binding.lottie12.visibility = View.VISIBLE
            Tools().viewEnable(window.decorView.rootView, false)

            val repo = Repository(this)
            repo.addAdLocation(adId, country, city, district, lat, lon)
            repo.getAddAdLocationData().observe(this) { response ->
                binding.lottie12.visibility = View.GONE
                Tools().viewEnable(window.decorView.rootView, true)
                if (response.status) {
                    val intent = Intent(this, NewAdImages::class.java)
                    intent.putExtra("adId", adId)
                    startActivity(intent)
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.locationNotFound), Toast.LENGTH_SHORT).show()
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") View.LAYOUT_DIRECTION_LTR
            else View.LAYOUT_DIRECTION_RTL
    }
}
