package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityPostBinding
import com.application.adverial.remote.ConversationRepository
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.remote.model.Conversation
import com.application.adverial.service.ScrollableMapFragment
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.PostPageAdapter
import com.application.adverial.ui.adapter.RecommendedAdsAdapter
import com.application.adverial.ui.adapter.ReviewAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Post : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPostBinding
    private var mapFragment: SupportMapFragment? = null
    private lateinit var map: GoogleMap
    private var id = ""
    private var phoneNumber = ""
    private var favorite = false
    private var lat = 0.0
    private var lon = 0.0
    private var type = ""
    private var actionBarMode = "closed"
    private var itemData: Ad? = null
    private var currentReviewPage = 1
    private var hasMoreReviews = false
    private var userHasReviewed = false
    private var reviewAdapter: ReviewAdapter? = null
    private var currentRating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.activityPostRoot)
        pageInit()
        fetchData()
        Tools().setBasedLogo(this, R.id.app_logo)

        // Hide ad details by default
        binding.showAdDetails.visibility = View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun pageInit() {
        binding.lottie13.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        mapFragment =
                supportFragmentManager.findFragmentById(R.id.post_map) as ScrollableMapFragment?
        mapFragment!!.getMapAsync(this)
        id = intent.getStringExtra("id")!!
        (mapFragment as ScrollableMapFragment).setListener {
            binding.postMapLayout.requestDisallowInterceptTouchEvent(true)
        }

        // Setup recommended ads RecyclerView with horizontal orientation
        binding.recommendedAdsRecyclerview.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Setup reviews RecyclerView
        binding.reviewsRecyclerview.layoutManager = LinearLayoutManager(this)

        // Setup click listener for the write review button
        binding.btnWriteReview.setOnClickListener {
            if (userHasReviewed) {
                Toast.makeText(this, getString(R.string.already_reviewed), Toast.LENGTH_SHORT)
                        .show()
            } else {
                showReviewDialog()
            }
        }

        // Setup click listener for load more reviews button
        binding.btnLoadMoreReviews.setOnClickListener {
            if (hasMoreReviews) {
                loadMoreReviews()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        val repo = Repository(this)
        repo.adDetails(id)
        repo.getAdDetailsData().observe(this) {
            binding.postPage.layoutManager = LinearLayoutManager(this)
            binding.postPage.adapter = PostPageAdapter(it.data!!, id)
            itemData = it.data

            val userId = getSharedPreferences("user", 0).getString("user_id", "")
            if (userId == itemData?.user_id) {
                binding.phoneCallBtn.visibility = View.GONE
                binding.messageCallBtn.visibility = View.GONE
            }
            binding.postTitle1.text = it.data!!.price_currency
            binding.postCity1.text = it.data!!.title
            phoneNumber = it.data!!.phone ?: ""

            if (it.data!!.is_favorite == 1) {
                binding.postFavorite.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.ic_favorite)
                )
                favorite = true
            } else {
                binding.postFavorite.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.ic_favorite_empty)
                )
                favorite = false
            }
            type = it.data!!.type ?: ""
            binding.lottie13.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)

            // Now that we've added category_id to the Ad model, use it directly
            val categoryId = it.data!!.category_id?.toString()
            if (!categoryId.isNullOrEmpty()) {
                Log.d("RecommendedAdsDebug", "Using ad's category ID: $categoryId")
                fetchRecommendedAds(categoryId)
            } else {
                Log.d("RecommendedAdsDebug", "Category ID is null or empty")
            }

            // Fetch reviews for this ad
            fetchReviews()
        }
    }

    private fun fetchMainCategories() {
        // This method is no longer used
    }

    private fun getCategoryIdFromAd(ad: Ad?): String? {
        if (ad == null) return null

        // Try different possible property names for category ID
        return try {
            // Using reflection to check all possibilities
            ad.javaClass.declaredFields.forEach { field ->
                field.isAccessible = true
                val fieldName = field.name.lowercase()
                if (fieldName.contains("category") && fieldName.contains("id")) {
                    val value = field.get(ad)
                    if (value != null) {
                        return value.toString()
                    }
                }
            }

            // If reflection doesn't work, try some common property names
            ad.javaClass.getDeclaredField("category_id")?.let {
                it.isAccessible = true
                return it.get(ad)?.toString()
            }

            ad.javaClass.getDeclaredField("categoryId")?.let {
                it.isAccessible = true
                return it.get(ad)?.toString()
            }

            // If we can't find a category ID, return null
            null
        } catch (e: Exception) {
            Log.e("RecommendedAdsDebug", "Error getting category ID: ${e.message}")
            null
        }
    }

    private fun fetchRecommendedAds(categoryId: String) {
        if (categoryId.isEmpty()) return

        val repo = Repository(this)
        repo.recommendedAds(categoryId)
        repo.getRecommendedAdsData().observe(this) { recommendedAds ->
            if (recommendedAds != null && recommendedAds.status && recommendedAds.data.isNotEmpty()
            ) {
                // Filter out the current ad from recommended ads
                val filteredAds = recommendedAds.data.filter { it.id.toString() != id }

                if (filteredAds.isNotEmpty()) {
                    Log.d(
                            "RecommendedAdsDebug",
                            "Setting recommended ads visible with ${filteredAds.size} items"
                    )
                    binding.recommendedAdsTitle.visibility = View.VISIBLE
                    binding.recommendedAdsRecyclerview.visibility = View.VISIBLE
                    binding.recommendedAdsRecyclerview.adapter =
                            RecommendedAdsAdapter(this, filteredAds)

                    // Make sure the recommended ads section is properly visible by scrolling to it
                    binding.recommendedAdsTitle.post { binding.recommendedAdsTitle.requestFocus() }
                } else {
                    Log.d("RecommendedAdsDebug", "No filtered ads remaining")
                }
            } else {
                Log.d("RecommendedAdsDebug", "No recommended ads found or invalid response")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Tools().gotoMyCountry(map)
        val repo = Repository(this)
        repo.adDetails(id)
        repo.getAdDetailsData().observe(this) {
            if (it.data != null && !it.data!!.lat.isNullOrBlank() && !it.data!!.lon.isNullOrBlank()
            ) {
                val latLng = LatLng(it.data!!.lat!!.toDouble(), it.data!!.lon!!.toDouble())
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                val smallMarker =
                        Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(resources, R.drawable.im_geo),
                                60,
                                60,
                                false
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
            binding.lottie13.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = Repository(this)
            if (favorite) {
                repo.removeFavorite(id)
                repo.getRemoveFavoriteData().observe(this) {
                    binding.lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    if (it.status) {
                        binding.postFavorite.setImageDrawable(
                                ContextCompat.getDrawable(this, R.drawable.ic_favorite_empty)
                        )
                        favorite = false
                    }
                }
            } else {
                repo.addFavorite(id)
                repo.getAddFavoriteData().observe(this) {
                    binding.lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    if (it.status) {
                        binding.postFavorite.setImageDrawable(
                                ContextCompat.getDrawable(this, R.drawable.ic_favorite)
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
        binding.postMapLayout.visibility = View.VISIBLE
        binding.postPage.visibility = View.GONE
    }

    fun about(view: View) {
        binding.postMapLayout.visibility = View.GONE
        binding.postPage.visibility = View.VISIBLE
        val userId = getSharedPreferences("user", 0).getString("user_id", "")
        if (userId == itemData?.user_id) {
            binding.phoneCallBtn.visibility = View.GONE
            binding.messageCallBtn.visibility = View.GONE
        } else {
            binding.phoneCallBtn.visibility = View.VISIBLE
            binding.messageCallBtn.visibility = View.VISIBLE
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
            binding.lottie13.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val partnerUserId = itemData?.user_detail?.id ?: 0
            val chatPartnerName = itemData?.user_detail?.name ?: ""
            val repo = ConversationRepository(this)
            repo.initialConversation(partnerUserId, id.toInt())
            repo.initialConversationLiveData.observe(this) { conversationResponse ->
                val conversationId = conversationResponse.conversionId
                if (conversationId != 0) {
                    val intent =
                            Intent(this, MessageActivity::class.java).apply {
                                val conversationObject =
                                        Conversation(
                                                chatPartnerId = partnerUserId,
                                                conversionId = conversationId,
                                                chatPartnerName = chatPartnerName,
                                                chatPartnerEmail = "",
                                                lastMessage = "",
                                                lastMessageAt = "",
                                                avatar = "",
                                                adId = itemData?.id,
                                                adTitle = itemData?.title,
                                                adPrice = itemData?.price,
                                                adPriceCurrency = itemData?.price_currency,
                                                adImage = itemData?.ad_images?.get(0)?.image
                                        )
                                putExtra("show_item", true)
                                putExtra("conversation", conversationObject)
                            }
                    binding.lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    startActivity(intent)
                } else {
                    binding.lottie13.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    Toast.makeText(this, "Failed to initiate conversation", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        } else {
            val intent = Intent(this, LoginWa::class.java)
            startActivity(intent)
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
            binding.postLa.layoutDirection = View.LAYOUT_DIRECTION_LTR
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
            binding.postLa.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        binding.postLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
    }

    fun showLocation(view: View) {
        binding.locationBtn.visibility = View.GONE
        binding.phoneCallBtn.visibility = View.GONE
        binding.messageCallBtn.visibility = View.GONE
        binding.showAdDetails.visibility = View.VISIBLE
        location(view)
    }

    fun showAdDetails(view: View) {
        binding.locationBtn.visibility = View.VISIBLE
        binding.phoneCallBtn.visibility = View.VISIBLE
        binding.messageCallBtn.visibility = View.VISIBLE
        binding.showAdDetails.visibility = View.GONE
        about(view)
    }

    private fun fetchReviews() {
        val repo = Repository(this)
        repo.getAdReviews(id, currentReviewPage)
        repo.getAdReviewsData().observe(this) { response ->
            if (response != null && response.status) {
                val reviewData = response.data

                // Show the reviews section
                binding.reviewsTitle.visibility = View.VISIBLE
                binding.reviewsSummary.visibility = View.VISIBLE

                // Set average rating display
                val averageRating = reviewData.average_rating
                binding.reviewsAverageRating.text = String.format("%.1f", averageRating)
                binding.reviewsCount.text =
                        getString(R.string.reviews_count, reviewData.reviews_count)

                // Set the stars for average rating
                setAverageRatingStars(averageRating)

                // Check if there are reviews to display
                if (reviewData.reviews.data.isNotEmpty()) {
                    binding.reviewsRecyclerview.visibility = View.VISIBLE

                    // If this is the first page, create the adapter
                    if (currentReviewPage == 1) {
                        reviewAdapter = ReviewAdapter(this, reviewData.reviews.data)
                        binding.reviewsRecyclerview.adapter = reviewAdapter
                    } else {
                        // Otherwise add to the existing adapter
                        reviewAdapter?.addMoreReviews(reviewData.reviews.data)
                    }

                    // Check if there are more reviews to load
                    hasMoreReviews =
                            reviewData.reviews.total >
                                    (currentReviewPage * reviewData.reviews.per_page)
                    binding.btnLoadMoreReviews.visibility =
                            if (hasMoreReviews) View.VISIBLE else View.GONE

                    // Check if user has already submitted a review (naive approach - should use
                    // API)
                    val userId = getSharedPreferences("user", 0).getString("user_id", "")
                    userHasReviewed =
                            reviewData.reviews.data.any { it.user_id.toString() == userId }
                }
            }
        }
    }

    private fun loadMoreReviews() {
        currentReviewPage++
        fetchReviews()
    }

    private fun setAverageRatingStars(rating: Float) {
        val stars =
                listOf(
                        binding.averageStar1,
                        binding.averageStar2,
                        binding.averageStar3,
                        binding.averageStar4,
                        binding.averageStar5
                )

        // Set filled and empty stars based on rating
        for (i in stars.indices) {
            if (i < rating.toInt()) {
                stars[i].setImageResource(R.drawable.ic_star_filled)
            } else if (i == rating.toInt() && rating % 1 != 0f) {
                // This would be for half-stars if we had that icon
                stars[i].setImageResource(R.drawable.ic_star_filled)
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty)
            }
        }
    }

    private fun showReviewDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_write_review)
        dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val starViews =
                listOf<ImageView>(
                        dialog.findViewById(R.id.review_star1),
                        dialog.findViewById(R.id.review_star2),
                        dialog.findViewById(R.id.review_star3),
                        dialog.findViewById(R.id.review_star4),
                        dialog.findViewById(R.id.review_star5)
                )

        // Setup star click listeners
        for (i in starViews.indices) {
            starViews[i].setOnClickListener {
                currentRating = i + 1
                updateStarRating(starViews, currentRating)
            }
        }

        // Setup button listeners
        dialog.findViewById<Button>(R.id.btn_cancel_review).setOnClickListener { dialog.dismiss() }

        dialog.findViewById<Button>(R.id.btn_submit_review).setOnClickListener {
            val reviewText = dialog.findViewById<EditText>(R.id.review_text).text.toString()

            if (currentRating == 0) {
                Toast.makeText(this, getString(R.string.select_rating), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (reviewText.trim().isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_review), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitReview(currentRating, reviewText)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateStarRating(stars: List<ImageView>, rating: Int) {
        for (i in stars.indices) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star_filled)
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty)
            }
        }
    }

    private fun submitReview(rating: Int, review: String) {
        val repo = Repository(this)

        // Log the actual user input
        android.util.Log.d(
                "REVIEW_DEBUG",
                "Submitting user review - rating: $rating, review: $review"
        )

        // Only submit the actual user input values, not hardcoded test values
        repo.submitAdReview(id, rating, review, 1)

        repo.getSubmitReviewData().observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    Toast.makeText(this, getString(R.string.review_submitted), Toast.LENGTH_SHORT)
                            .show()

                    // Reset to first page and refresh reviews
                    currentReviewPage = 1
                    fetchReviews()

                    // Mark that user has reviewed
                    userHasReviewed = true
                } else {
                    android.util.Log.e("REVIEW_DEBUG", "Error response: ${response.message}")
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                android.util.Log.e("REVIEW_DEBUG", "Null response received")
                Toast.makeText(this, "Error submitting review", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
