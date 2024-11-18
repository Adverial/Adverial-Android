package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivitySearchResultBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.SearchFilterAdapter
import com.application.adverial.ui.adapter.SearchResultsAdapter
import com.application.adverial.ui.dialog.DropList
import org.json.JSONArray
import org.json.JSONObject

class SearchResult : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private var keyword = ""

    override fun onBackPressed() {
        if (binding.filterMenu.isVisible) hideFilterMenu()
        else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize View Binding
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.imageView6)
        Tools().changeViewFromTheme(this, binding.searchResultRoot)

        pageInit()
        filterSliderInit()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun pageInit() {
        binding.lottie5.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        keyword = intent.getStringExtra("keyword") ?: ""
        binding.resultName.text = keyword

        binding.resultRecyclerView.layoutManager = LinearLayoutManager(this)
        val repo = Repository(this)
        repo.search(keyword, "")
        repo.getSearchData().observe(this) { response ->
            response.data?.let { ads ->
                binding.resultCount.text = "${ads.size} ${getString(R.string.result_count)}"
                binding.resultRecyclerView.adapter = SearchResultsAdapter(ads as ArrayList<Ad>)
            }
            binding.lottie5.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }

        binding.filterMenuCover.setOnTouchListener { _, _ ->
            if (binding.filterMenu.isVisible) hideFilterMenu()
            true
        }
    }

    private fun filterSliderInit() {
        binding.filterMenu.layoutManager = LinearLayoutManager(this)
        val adapter = SearchFilterAdapter()
        binding.filterMenu.adapter = adapter
        adapter.getResult().observe(this) { status ->
            when (status) {
                "show" -> {
                    binding.lottie5.visibility = View.VISIBLE
                    Tools().viewEnable(window.decorView.rootView, false)
                }
                "hide" -> {
                    binding.lottie5.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                }
                else -> applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val data = getSharedPreferences("filter", 0)
        val dataOptions = getSharedPreferences("filterOptions", 0)

        val addressData = JSONObject().apply {
            put("country", data.getString("country", ""))
            put("city", data.getString("city", ""))
            put("district", data.getString("district", ""))
        }

        val priceData = JSONObject().apply {
            put("min", data.getString("priceMin", ""))
            put("max", data.getString("priceMax", ""))
            put("currency", data.getString("currency", ""))
        }

        val othersData = JSONObject().apply {
            put("photo", data.getString("photo", ""))
            put("video", data.getString("video", ""))
        }

        val optionsData = JSONArray()
        dataOptions.all.forEach { (key, value) ->
            val jsonObj = JSONObject().put("option_id", key).put("value", value.toString())
            optionsData.put(jsonObj)
        }

        val filter = JSONObject().apply {
            put("filter", JSONObject().apply {
                put("address", addressData)
                put("price", priceData)
                put("options", optionsData)
                put("others", othersData)
            })
        }
        // Log the filter data (for debugging)
        // Log.d("Filter", filter.toString())
    }

    @SuppressLint("SetTextI18n")
    fun sort(view: View) {
        val sortItems = listOf(
            com.application.adverial.ui.model.DropList(getString(R.string.sort_default), ""),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_PriceLowToHigh), "3"),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_priceHighToLow), "4"),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_date), "2"),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_name), "1")
        )

        val dialog = DropList(sortItems as ArrayList<com.application.adverial.ui.model.DropList>, getString(R.string.sort_title))
        dialog.show(supportFragmentManager, "DropList")
        dialog.getStatus().observe(this) { sortOption ->
            binding.lottie5.visibility = View.VISIBLE
            Tools().viewEnable(window.decorView.rootView, false)
            val repo = Repository(this)
            repo.search(keyword, sortOption.id)
            repo.getSearchData().observe(this) { response ->
                response.data?.let { ads ->
                    binding.resultCount.text = "${ads.size} ${getString(R.string.result_count)}"
                    binding.resultRecyclerView.adapter = SearchResultsAdapter(ads as ArrayList<Ad>)
                }
                binding.lottie5.visibility = View.GONE
                Tools().viewEnable(window.decorView.rootView, true)
            }
        }
    }

    private fun hideFilterMenu() {
        binding.filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right))
        binding.filterMenu.visibility = View.GONE
        binding.filterMenuCover.visibility = View.GONE
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }

    fun back(view: View) {
        onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language.isNullOrEmpty() || language == "0" || language == "1") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }
}
