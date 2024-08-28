
package com.application.adverial.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.remote.ConversationRepository
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.remote.model.Conversation
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.PostPageAdapter
import com.huawei.hms.maps.CameraUpdate
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.MapFragment
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.LatLngBounds
import com.huawei.hms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_post.*


@Suppress("CAST_NEVER_SUCCEEDS")
class Post : AppCompatActivity(), OnMapReadyCallback {
    private var id = ""
    private var phoneNumber = ""
    private var favorite = false
    private var lat = 0.0
    private var lon = 0.0
    private var type = ""
    private var ItemData: Ad? = null

    private lateinit var hMap: HuaweiMap
    companion object {
        private const val TAG = "MapFragmentCodeActivity"
    }

    private var mMapFragment: MapFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(this);
        //MapsInitializer.setApiKey("DQEDACRuLE5ygNAVgf/C/jiDIULciSgrEQuOKKATQxwiZFYsqUFTLr4CJke4SudwvutlZqfvK5OWVYZ6B16ZeM/hojk/RC6ScXsgaw==")
        setContentView(R.layout.activity_post)
        requestPermission()
        val activityPostRoot = findViewById<View>(R.id.activityPostRoot)
        Tools().changeViewFromTheme(this, activityPostRoot)

        pageInit()
        fetchData()
        Tools().setBasedLogo(this, R.id.app_logo)

        val mSupportMapFragment: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this)
        val huaweiMapOptions = HuaweiMapOptions()
        huaweiMapOptions.compassEnabled(true)
        huaweiMapOptions.zoomGesturesEnabled(true)
        mMapFragment = MapFragment.newInstance(huaweiMapOptions)
        show_ad_details.visibility = View.GONE
    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        } else {
            // Permissions are automatically granted on older versions
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun pageInit() {
        lottie13.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
         id = intent.getStringExtra("id").toString()
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        val repo = Repository(this)
        repo.adDetails(id)
        repo.getAdDetailsData().observe(this) {
            post_page.layoutManager = LinearLayoutManager(this)
            post_page.adapter = PostPageAdapter(it.data!!, id)
            ItemData = it.data

            val userId = getSharedPreferences("user", 0).getString("user_id", "")
            if (userId == ItemData?.user_id) {
                phone_call_btn.visibility = View.GONE
                message_call_btn.visibility = View.GONE
            }
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
    override fun onMapReady(huaweiMap: HuaweiMap) {
        val hMap = huaweiMap
/// gotoMyCountry(hMap)
        // Set the map type to normal
        hMap.mapType = HuaweiMap.MAP_TYPE_NORMAL
        hMap.isBuildingsEnabled = true
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(48.893478, 2.334595), 10f))
        // Enable all required gestures and controls
//        hMap.uiSettings.isZoomControlsEnabled = true
//        hMap.uiSettings.isScrollGesturesEnabled = true
//        hMap.uiSettings.isZoomGesturesEnabled = true
//        hMap.uiSettings.isTiltGesturesEnabled = true
//        hMap.uiSettings.isRotateGesturesEnabled = true
//        hMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
//       //  Load the map at a default location iraq
//        val defaultLatLng = LatLng(33.3152, 44.3661)
//        val defaultZoom = 10f
//        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoom))
//        // Set a callback to be notified when the map is fully loaded
//        hMap.setOnMapLoadedCallback {
//            Log.d("MapStatus", "Map has loaded successfully.")
//            val repo = Repository(this)
//            repo.adDetails(id)
//            repo.getAdDetailsData().observe(this) { adDetails ->
//                adDetails?.data?.let { data ->
//                    val lat = data.lat?.toDoubleOrNull()
//                    val lon = data.lon?.toDoubleOrNull()
//
//                    if (lat != null && lon != null) {
//                        val latLng = LatLng(lat, lon)
//
//                        // Add a marker at the retrieved location
//                        val markerOptions = MarkerOptions()
//                            .position(latLng)
//                            .title("Marker at Ad Location")
//                        hMap.addMarker(markerOptions)
//
//                        // Create a CameraPosition object to move and zoom the camera
//                        val cameraPosition = CameraPosition.Builder()
//                            .target(latLng) // Set the target to the marker location
//                            .zoom(15f) // Set the zoom level
//                            .bearing(0f) // Set the orientation of the camera (0 degrees is north)
//                            .tilt(30f) // Set the tilt of the camera
//                            .build()
//
//                        // Animate the camera to the CameraPosition
//                        hMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, object : HuaweiMap.CancelableCallback {
//                            override fun onFinish() {
//                                Log.d("MapStatus", "Camera animation finished.")
//                            }
//
//                            override fun onCancel() {
//                                Log.d("MapStatus", "Camera animation canceled.")
//                            }
//                        })
//
//                    } else {
//                        Log.e("MapError", "Invalid latitude or longitude values.")
//                    }
//                } ?: run {
//                    Log.e("MapError", "Ad details data is null.")
//                    Toast.makeText(this, "Ad details data is null.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }


    fun favorite(view: View) {
        if (Tools().authCheck(this)) {
            lottie13.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = Repository(this)
            if (favorite) {
                repo.removeFavorite(id)
                repo.getRemoveFavoriteData().observe(this) {
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
                }
            } else {
                repo.addFavorite(id)
                repo.getAddFavoriteData().observe(this) {
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
                }
            }
        } else {
            val intent = Intent(this, LoginWa::class.java)
            startActivity(intent)
        }
    }

    fun location(view: View) {
        val regular = ResourcesCompat.getFont(this, R.font.regular)
        val bold = ResourcesCompat.getFont(this, R.font.bold)
        post_mapLayout.visibility = View.VISIBLE
        post_page.visibility = View.GONE
    }

    fun about(view: View) {
        val regular = ResourcesCompat.getFont(this, R.font.regular)
        val bold = ResourcesCompat.getFont(this, R.font.bold)
        post_mapLayout.visibility = View.GONE
        post_page.visibility = View.VISIBLE
        val userId = getSharedPreferences("user", 0).getString("user_id", "")
        if (userId == ItemData?.user_id) {
            phone_call_btn.visibility = View.GONE
            message_call_btn.visibility = View.GONE
        } else {
            phone_call_btn.visibility = View.VISIBLE
            message_call_btn.visibility = View.VISIBLE
        }
    }

    fun call(view: View) {
        if (phoneNumber.isNotBlank() && (type == "1" || type == "2")) {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(dialIntent)
        } else {
            Toast.makeText(this, getString(R.string.phoneNumberNotFound), Toast.LENGTH_SHORT).show()
        }
    }

    fun message(view: View) {
        if (Tools().authCheck(this)) {
            lottie13.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            var item_id = id
            ItemData?.let {
                item_id = it.id.toString()
            }
            val partnerUserId = ItemData?.user_detail?.id ?: 0
            val chatPartnerName = ItemData?.user_detail?.name ?: ""

            val repo = ConversationRepository(this)
            repo.initialConversation(partnerUserId, item_id.toInt())
            repo.initialConversationLiveData.observe(this) { conversationResponse ->
                val conversationId = conversationResponse.conversionId

                if (conversationId != 0) {
                    val intent = Intent(this, MessageActivity::class.java).apply {
                        val conversationObject = Conversation(
                            chatPartnerId = partnerUserId,
                            conversionId = conversationId,
                            chatPartnerName = chatPartnerName,
                            chatPartnerEmail = "",
                            lastMessage = "",
                            lastMessageAt = "",
                            avatar = "",
                            adId = ItemData?.id,
                            adTitle = ItemData?.title,
                            adPrice = ItemData?.price,
                            adPriceCurrency = ItemData?.price_currency,
                            adImage = ItemData?.ad_images?.get(0)?.image
                        )
                        putExtra("show_item", true)
                        putExtra("conversation", conversationObject)
                    }
                    lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    startActivity(intent)
                } else {
                    lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    Toast.makeText(this, "Failed to initiate conversation", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val intent = Intent(this, LoginWa::class.java)
            startActivity(intent)
        }
    }

    fun satellite(view: View) {
        hMap.mapType = HuaweiMap.MAP_TYPE_SATELLITE
    }

    fun terrain(view: View) {
        hMap.mapType = HuaweiMap.MAP_TYPE_TERRAIN
    }

    fun normal(view: View) {
        hMap.mapType = HuaweiMap.MAP_TYPE_NORMAL
    }

    fun share(view: View) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = "App advertisement\n" +
                    "Ad link: ${BuildConfig.API_BASE_URL}"
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
        val postLa = findViewById<View>(R.id.post_la)
        val postLayout = findViewById<View>(R.id.post_layout)
        if (language == "" || language == "0" || language == "1") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            postLa.layoutDirection = View.LAYOUT_DIRECTION_LTR
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        postLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR

    }

    fun showLocation(view: View) {
        location_btn.visibility = View.GONE
        phone_call_btn.visibility = View.GONE
        message_call_btn.visibility = View.GONE
        show_ad_details.visibility = View.VISIBLE
        location(view)
    }

    fun showAdDetails(view: View) {
        location_btn.visibility = View.VISIBLE
        phone_call_btn.visibility = View.VISIBLE
        message_call_btn.visibility = View.VISIBLE
        show_ad_details.visibility = View.GONE
        about(view)
    }
}