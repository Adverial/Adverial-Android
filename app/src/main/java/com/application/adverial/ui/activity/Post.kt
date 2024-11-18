package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.databinding.ActivityPostBinding
import com.application.adverial.remote.ConversationRepository
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.remote.model.Conversation
import com.application.adverial.service.ScrollableMapFragment
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.PostPageAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Post : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPostBinding
    private lateinit var map: GoogleMap
    private var id = ""
    private var phoneNumber = ""
    private var favorite = false
    private var lat = 0.0
    private var lon = 0.0
    private var type = ""
    private var ItemData: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.activityPostRoot)
        Tools().setBasedLogo(this, binding.appLogo.id)

        pageInit()
        fetchData()
        binding.showAdDetails.visibility = View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun pageInit() {
        binding.lottie13.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.post_map) as ScrollableMapFragment
        mapFragment.getMapAsync(this)
        id = intent.getStringExtra("id") ?: ""
        mapFragment.setListener {
            binding.postMapLayout.requestDisallowInterceptTouchEvent(true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        val repo = Repository(this)
        repo.adDetails(id)
        repo.getAdDetailsData().observe(this) {
            val adData = it.data ?: return@observe
            binding.postPage.layoutManager = LinearLayoutManager(this)
            binding.postPage.adapter = PostPageAdapter(adData, id)

            ItemData = adData
            val userId = getSharedPreferences("user", 0).getString("user_id", "")
            if (userId == ItemData?.user_id) {
                binding.phoneCallBtn.visibility = View.GONE
                binding.messageCallBtn.visibility = View.GONE
            }
            binding.postTitle1.text = adData.price_currency
            binding.postCity1.text = adData.title
            phoneNumber = adData.phone ?: ""
            favorite = adData.is_favorite == 1

            updateFavoriteIcon()
            type = adData.type ?: ""
            binding.lottie13.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }
    }

    private fun updateFavoriteIcon() {
        if (favorite) {
            binding.postFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite))
        } else {
            binding.postFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_empty))
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        Tools().gotoMyCountry(map)

        val repo = Repository(this)
        repo.adDetails(id)
        repo.getAdDetailsData().observe(this) {
            val adData = it.data ?: return@observe
            if (!adData.lat.isNullOrBlank() && !adData.lon.isNullOrBlank()) {
                val latLng = LatLng(adData.lat!!.toDouble(), adData.lon!!.toDouble())
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                val smallMarker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.im_geo), 60, 60, false)
                val marker = MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                map.addMarker(marker)
            }
        }
    }

    fun favorite(view: View) {
        if (Tools().authCheck(this)) {
            binding.lottie13.visibility = View.VISIBLE
            Tools().viewEnable(window.decorView.rootView, false)
            val repo = Repository(this)
            if (favorite) {
                repo.removeFavorite(id)
                repo.getRemoveFavoriteData().observe(this) {
                    binding.lottie13.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    if (it.status) {
                        favorite = false
                        updateFavoriteIcon()
                    }
                }
            } else {
                repo.addFavorite(id)
                repo.getAddFavoriteData().observe(this) {
                    binding.lottie13.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    if (it.status) {
                        favorite = true
                        updateFavoriteIcon()
                    }
                }
            }
        } else {
            startActivity(Intent(this, LoginWa::class.java))
        }
    }

    fun call(view: View) {
        if (phoneNumber.isNotBlank() && (type == "1" || type == "2")) {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(dialIntent)
        } else {
            Toast.makeText(this, getString(R.string.phoneNumberNotFound), Toast.LENGTH_SHORT).show()
        }
    }

    fun message(view: View) {
        if (Tools().authCheck(this)) {
            binding.lottie13.visibility = View.VISIBLE
            Tools().viewEnable(window.decorView.rootView, false)
            val partnerUserId = ItemData?.user_detail?.id ?: 0
            val chatPartnerName = ItemData?.user_detail?.name ?: ""
            val repo = ConversationRepository(this)
            repo.initialConversation(partnerUserId, id.toInt())
            repo.initialConversationLiveData.observe(this) {
                binding.lottie13.visibility = View.GONE
                Tools().viewEnable(window.decorView.rootView, true)
                if (it.conversionId != 0) {
                    val intent = Intent(this, MessageActivity::class.java).apply {
//                        putExtra("conversation", Conversation(
//                            chatPartnerId = partnerUserId,
//                            conversionId = it.conversionId,
//                            chatPartnerName = chatPartnerName,
//                            adId = ItemData?.id,
//                            adTitle = ItemData?.title,
//                            adPrice = ItemData?.price,
//                            adPriceCurrency = ItemData?.price_currency,
//                            adImage = ItemData?.ad_images?.getOrNull(0)?.image
//                        ))
//                        putExtra("show_item", true)
                    }
//                    startActivity(intent)
                    Toast.makeText(this, "Failed to initiate conversation", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to initiate conversation", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            startActivity(Intent(this, LoginWa::class.java))
        }
    }

    fun back(view: View) {
        finish()
    }
}
