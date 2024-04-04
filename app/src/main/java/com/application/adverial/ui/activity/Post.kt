package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.application.adverial.remote.Repository
import com.application.adverial.service.ScrollableMapFragment
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.PostPageAdapter
import kotlinx.android.synthetic.main.activity_post.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.view.ViewGroup.MarginLayoutParams
import com.application.adverial.service.OnSwipeTouchListener
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.provider.Telephony

import android.os.Build
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatDelegate

import androidx.core.content.res.ResourcesCompat
import com.application.adverial.R
import kotlinx.android.synthetic.main.item_post_page.*


class Post : AppCompatActivity(), OnMapReadyCallback {

    private var mapFragment: SupportMapFragment? = null
    private lateinit var map: GoogleMap
    private var id = ""
    private var phoneNumber = ""
    private var favorite = false
    private var lastX = 0F
    private var lat = 0.0
    private var lon = 0.0
    private var type = ""
    private var actionBarMode = "closed"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        Tools().rotateLayout(this, profile_menu2)
        Tools().changeViewFromTheme(this, activityPostRoot)

        pageInit()
        fetchData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun pageInit() {
        lottie13.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.post_map) as ScrollableMapFragment?
        mapFragment!!.getMapAsync(this)
        id = intent.getStringExtra("id")!!

        post_sideSlide.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeRight() {
                actionBarMode = "opened"
                val params: MarginLayoutParams = post_sideSlide.layoutParams as MarginLayoutParams
                params.leftMargin = resources.getDimension(R.dimen._minus1sdp).toInt()
                post_sideSlide.requestLayout()
                post_sideSlide.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@Post,
                        R.anim.slide_right1
                    )
                )
            }

            override fun onSwipeLeft() {
                actionBarMode = "closed"
                val params: MarginLayoutParams = post_sideSlide.layoutParams as MarginLayoutParams
                params.leftMargin = resources.getDimension(R.dimen._minus40sdp).toInt()
                post_sideSlide.requestLayout()
                post_sideSlide.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@Post,
                        R.anim.slide_left1
                    )
                )
            }
        })

        post_sideBarAction.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (actionBarMode == "opened") {
                    actionBarMode = "closed"
                    val params: MarginLayoutParams =
                        post_sideSlide.layoutParams as MarginLayoutParams
                    params.leftMargin = resources.getDimension(R.dimen._minus40sdp).toInt()
                    post_sideSlide.requestLayout()
                    post_sideSlide.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@Post,
                            R.anim.slide_left1
                        )
                    )
                } else {
                    actionBarMode = "opened"
                    val params: MarginLayoutParams =
                        post_sideSlide.layoutParams as MarginLayoutParams
                    params.leftMargin = resources.getDimension(R.dimen._minus1sdp).toInt()
                    post_sideSlide.requestLayout()
                    post_sideSlide.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@Post,
                            R.anim.slide_right1
                        )
                    )
                }
            }
            true
        }

        post_sideBarAction.setOnClickListener {
            if (actionBarMode == "opened") {
                actionBarMode = "closed"
                val params: MarginLayoutParams = post_sideSlide.layoutParams as MarginLayoutParams
                params.leftMargin = resources.getDimension(R.dimen._minus40sdp).toInt()
                post_sideSlide.requestLayout()
                post_sideSlide.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@Post,
                        R.anim.slide_left1
                    )
                )
            } else {
                actionBarMode = "opened"
                val params: MarginLayoutParams = post_sideSlide.layoutParams as MarginLayoutParams
                params.leftMargin = resources.getDimension(R.dimen._minus1sdp).toInt()
                post_sideSlide.requestLayout()
                post_sideSlide.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@Post,
                        R.anim.slide_right1
                    )
                )
            }
        }

        (mapFragment as ScrollableMapFragment).setListener {
            post_mapLayout.requestDisallowInterceptTouchEvent(
                true
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        val repo = Repository(this)
        repo.adDetails(id)
        repo.getAdDetailsData().observe(this) {
            post_page.layoutManager = LinearLayoutManager(this)
            post_page.adapter = PostPageAdapter(it.data!!, id)
            //post_page.scrollToPosition(0)
            post_title1.text = it.data!!.price_currency
            post_city1.text = it.data!!.title
            phoneNumber = it.data!!.phone ?: ""
            if (it.data!!.is_favorite == 1) {
                post_favorite.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_favorite
                    )
                )
                favorite = true
            } else {
                post_favorite.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_favorite_empty
                    )
                )
                favorite = false
            }
            type = it.data!!.type ?: ""
            lottie13.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        Tools().gotoMyCountry(map)
        val repo = Repository(this)
        repo.adDetails(id)
        repo.getAdDetailsData().observe(this) {
            if (it.data != null && !it.data!!.lat.isNullOrBlank() && !it.data!!.lon.isNullOrBlank()) {
                val latLng = LatLng(it.data!!.lat!!.toDouble(), it.data!!.lon!!.toDouble())
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                val smallMarker = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.im_geo
                    ), 60, 60, false
                )
                val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
                val marker = MarkerOptions().position(latLng).icon(smallMarkerIcon)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                map.addMarker(marker)

            }

        }
    }

    fun favorite(view: View) {
        if (Tools().authCheck(this)) {
            lottie13.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = Repository(this)
            if (favorite) {
                repo.removeFavorite(id)
                repo.getRemoveFavoriteData().observe(this, {
                    lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    if (it.status) {
                        post_favorite.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_favorite_empty
                            )
                        )
                        favorite = false
                    }
                })
            } else {
                repo.addFavorite(id)
                repo.getAddFavoriteData().observe(this, {
                    lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    if (it.status) {
                        post_favorite.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_favorite
                            )
                        )
                        favorite = true
                    }
                })
            }
        } else {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    fun location(view: View) {
        location_indicator.visibility = View.VISIBLE
        about_indicator.visibility = View.INVISIBLE
        val regular = ResourcesCompat.getFont(this, R.font.regular)
        val bold = ResourcesCompat.getFont(this, R.font.bold)
        post_about.typeface = regular
        post_location.typeface = bold
        /*post_location.typeface = Typeface.DEFAULT_BOLD
        post_about.typeface = Typeface.DEFAULT*/
        post_mapLayout.visibility = View.VISIBLE
        post_page.visibility = View.GONE
    }

    fun about(view: View) {
        location_indicator.visibility = View.INVISIBLE
        about_indicator.visibility = View.VISIBLE
        /*post_location.typeface = Typeface.DEFAULT
        post_about.typeface = Typeface.DEFAULT_BOLD*/
        val regular = ResourcesCompat.getFont(this, R.font.regular)
        val bold = ResourcesCompat.getFont(this, R.font.bold)
        post_location.typeface = regular
        post_about.typeface = bold
        post_mapLayout.visibility = View.GONE
        post_page.visibility = View.VISIBLE
    }

    fun call(view: View) {
        if (phoneNumber.isNotBlank() && (type == "1" || type == "2")) {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(dialIntent)
        } else {
            Toast.makeText(this, getString(R.string.phoneNumberNotFound), Toast.LENGTH_SHORT).show()
            /*val dialog= AlertDialog(getString(R.string.error), getString(R.string.phoneNumberNotFound))
            dialog.show(supportFragmentManager, "AlertDialog")*/
        }
    }

    fun message(view: View) {
        /*if(Tools().authCheck(this)){
            val intent= Intent(this, Message::class.java)
            startActivity(intent)
        }else{
            val intent= Intent(this, Login::class.java)
            startActivity(intent)
        }*/
        if (phoneNumber.isNotBlank() && (type == "1" || type == "3")) {
            val message = ""
            val phone = phoneNumber
            val uri = Uri.parse("smsto:+$phone")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            with(intent) {
                putExtra("address", "+$phone")
                putExtra("sms_body", message)
            }
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                    val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this)
                    if (defaultSmsPackageName != null) intent.setPackage(defaultSmsPackageName)
                    startActivity(intent)
                }
                else -> startActivity(intent)
            }
        } else {
            Toast.makeText(this, getString(R.string.phoneNumberNotFound), Toast.LENGTH_SHORT).show()
            /*val dialog= AlertDialog(getString(R.string.error), getString(R.string.phoneNumberNotFound))
            dialog.show(supportFragmentManager, "AlertDialog")*/
        }
    }

    fun satellite(view: View) {
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
    }

    fun terrain(view: View) {
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    fun normal(view: View) {
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    fun share(view: View) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = "Luuk App advertisement\n" +
                    "Ad title: ${post_city.text}" +
                    "\n" +
                    "Ad price: ${post_title.text}" +
                    "\n" +
                    "Ad location: ${post_address.text}" +
                    "\n" +
                    "Ad link: https://www.wookweb.com"
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, shareBody)
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, shareBody))
        } catch (e: Exception) {
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            post_la.layoutDirection = View.LAYOUT_DIRECTION_LTR
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
            post_la.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        post_layout.layoutDirection = View.LAYOUT_DIRECTION_LTR
    }
}