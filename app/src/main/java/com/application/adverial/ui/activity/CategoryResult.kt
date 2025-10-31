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
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.databinding.ActivitySearchResultBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.FilterAdapter
import com.application.adverial.ui.adapter.SearchResultsAdapter
import com.application.adverial.ui.dialog.DropList
import org.json.JSONArray
import org.json.JSONObject

class CategoryResult : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private var id = ""
    private var name = ""
    private var type = ""
    private lateinit var adapter: SearchResultsAdapter
    private var scrollPermission = false
    private var isFinished = false
    private var page = 0
    private val posts = ArrayList<Ad>()
    private var sort = ""

    override fun onBackPressed() {
        if (binding.filterMenu.isVisible) hideFilterMenu()
        else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.imageView6)
        pageInit()
        filterSliderInit()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun pageInit() {
        getSharedPreferences("filter", 0).edit().clear().apply()
        getSharedPreferences("filterOptions", 0).edit().clear().apply()
        binding.lottie5.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)

        id = intent.getStringExtra("id")!!
        name = intent.getStringExtra("name")!!
        type = intent.getStringExtra("type")!!

        binding.resultName.text = name
        binding.resultRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SearchResultsAdapter(posts)
        binding.resultRecyclerView.adapter = adapter

        binding.resultRecyclerView.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0 &&
                                scrollPermission &&
                                !recyclerView.canScrollVertically(1)
                        ) {
                            nextPage()
                        }
                    }
                }
        )

        nextPage()

        binding.filterMenuCover.setOnTouchListener { _, _ ->
            if (binding.filterMenu.isVisible) {
                hideFilterMenu()
            }
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun nextPage() {
        if (!isFinished) {
            scrollPermission = false
            page++

            val repo = Repository(this)
            repo.categoryAds(id, sort, page)
            repo.getCategoryAdsData().observe(this) {
                binding.lottie5.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                binding.resultCount.text = "${it.data.ad_list?.size} ${getString(R.string.result_count)}"
                if (it.data.ad_list.isNullOrEmpty()) {
                    isFinished = true
                } else {
                    posts.addAll(it.data.ad_list ?: listOf())
                    adapter.notifyDataSetChanged()
                    scrollPermission = true
                }
            }
        }
    }

    private fun filterSliderInit() {
        binding.filterMenu.layoutManager = LinearLayoutManager(this)
        val filterAdapter = FilterAdapter(type)
        binding.filterMenu.adapter = filterAdapter

        filterAdapter.getResult().observe(this) {
            when (it) {
                "show" -> {
                    binding.lottie5.visibility = View.VISIBLE
                    Tools().viewEnable(this.window.decorView.rootView, false)
                }
                "hide" -> {
                    binding.lottie5.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                }
                else -> {
                    applyFilter()
                }
            }
        }
    }

    private fun applyFilter() {
        val data = getSharedPreferences("filter", 0)
        val dataOptions = getSharedPreferences("filterOptions", 0)
        val addressData = JSONObject().put("country", data.getString("country", ""))
            .put("city", data.getString("city", ""))
            .put("district", data.getString("district", ""))
        val priceData = JSONObject().put("min", data.getString("priceMin", ""))
            .put("max", data.getString("priceMax", ""))
            .put("currency", data.getString("currency", ""))
        val othersData = JSONObject().put("photo", data.getString("photo", ""))
            .put("video", data.getString("video", ""))
        val optionsData = JSONArray()

        val allEntries: Map<String, *> = dataOptions.all
        for ((key, value) in allEntries) {
            val jsonObj = JSONObject().put("option_id", key).put("value", value.toString())
            optionsData.put(jsonObj)
        }

        val filter = JSONObject().put(
            "filter", JSONObject()
                .put("address", addressData)
                .put("price", priceData)
                .put("options", optionsData)
                .put("others", othersData)
        )

        hideFilterMenu()
        binding.lottie5.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        val repo = Repository(this)
        repo.filterCategory(id, filter.toString())
        repo.getFilterCategoryData().observe(this) {
            if (it.status) {
                binding.resultRecyclerView.adapter = SearchResultsAdapter(it.data as ArrayList<Ad>)
            }
            binding.lottie5.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
        }
    }

    fun filter(view: View) {
        if (!binding.filterMenu.isVisible) {
            binding.filterMenu.visibility = View.VISIBLE
            binding.filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left))
            binding.filterMenuCover.visibility = View.VISIBLE
        }
    }

    private fun hideFilterMenu() {
        binding.filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right))
        binding.filterMenu.visibility = View.GONE
        binding.filterMenuCover.visibility = View.GONE
        val imgr: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
    }

    @SuppressLint("SetTextI18n")
    fun sort(view: View) {
        val sortItems = arrayListOf(
            com.application.adverial.ui.model.DropList(getString(R.string.sort_default), ""),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_PriceLowToHigh), "3"),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_priceHighToLow), "4"),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_date), "2"),
            com.application.adverial.ui.model.DropList(getString(R.string.sort_name), "1")
        )
        val dialog = DropList(sortItems, getString(R.string.sort_title))
        dialog.show(supportFragmentManager, "DropList")
        dialog.getStatus().observe(this) { itt ->
            binding.lottie5.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            sort = itt.id
            page = 0
            scrollPermission = true
            isFinished = false
            posts.clear()
            nextPage()
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
            if (language == "" || language == "0" || language == "1") View.LAYOUT_DIRECTION_LTR
            else View.LAYOUT_DIRECTION_RTL
    }
}
