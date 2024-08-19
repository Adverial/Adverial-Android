package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.LatLngBounds
import com.huawei.hms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_post.*

class Post : AppCompatActivity(), OnMapReadyCallback {

    private var mapFragment: SupportMapFragment? = null
    private lateinit var map: HuaweiMap
    private var id = ""
    private var phoneNumber = ""
    private var favorite = false
    private var lat = 0.0
    private var lon = 0.0
    private var type = ""
    private var ItemData: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        Tools().changeViewFromTheme(this, activityPostRoot)

        pageInit()
        fetchData()
        Tools().setBasedLogo(this, R.id.app_logo)

        // hide
        show_ad_details.visibility = View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun pageInit() {
        lottie13.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.post_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        id = intent.getStringExtra("id")!!
        mapFragment!!.view?.setOnTouchListener { _, _ ->
            post_mapLayout.requestDisallowInterceptTouchEvent(true)
            false
        }
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
    fun gotoMyCountry(map: HuaweiMap) {
        val one = LatLng(42.216071, 26.389816)
        val two = LatLng(36.690183, 44.747969)
        val builder = LatLngBounds.Builder()
        builder.include(one)
        builder.include(two)
        val bounds = builder.build()
        val padding = 10
        map.setLatLngBoundsForCameraTarget(bounds)
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                Tools().displayWidth(),
                Tools().displayHeight(),
                padding
            )
        )
        map.setMinZoomPreference(map.cameraPosition.zoom)
    }
    override fun onMapReady(huaweiMap: HuaweiMap) {
        map = huaweiMap

        // Example LatLng coordinates
        val lat = 42.216071
        val lon = 26.389816
        val latLng = LatLng(lat, lon)

        // Move camera to the location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        // Add a marker at the location
        val markerOptions = MarkerOptions().position(latLng).title("Marker at Location")
        map.addMarker(markerOptions)

        // Set up camera bounds to ensure the location is visible
        val boundsBuilder = LatLngBounds.Builder()
        boundsBuilder.include(latLng)
        val bounds = boundsBuilder.build()
        val padding = 10 // Offset from edges of the map in pixels
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
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
        map.mapType = HuaweiMap.MAP_TYPE_SATELLITE
}

fun terrain(view: View) {
    map.mapType = HuaweiMap.MAP_TYPE_TERRAIN
}

fun normal(view: View) {
    map.mapType = HuaweiMap.MAP_TYPE_NORMAL
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
    if (language == "" || language == "0" || language == "1") {
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        post_la.layoutDirection = View.LAYOUT_DIRECTION_LTR
    } else {
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
    post_layout.layoutDirection = View.LAYOUT_DIRECTION_LTR
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