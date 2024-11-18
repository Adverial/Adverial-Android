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
import com.application.adverial.databinding.ActivitySpecialBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.ShowRoomData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.SearchFilterAdapter
import com.application.adverial.ui.adapter.SpecialAdapter
import com.application.adverial.ui.dialog.DropList
import org.json.JSONArray
import org.json.JSONObject

class Special : AppCompatActivity() {

    // Declare the binding object
    private lateinit var binding: ActivitySpecialBinding

    override fun onBackPressed() {
        if (binding.filterMenu.isVisible) hideFilterMenu()
        else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivitySpecialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize tools and views
        Tools().rotateLayout(this, binding.newAdCategoryBack2)
        Tools().changeViewFromTheme(this, binding.specialRoot)
        Tools().setBasedLogo(this, binding.imageView38.id)

        pageInit()
        filterSliderInit()
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    private fun pageInit() {
        binding.lottie6.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        binding.specialRecyclerView.layoutManager = LinearLayoutManager(this)

        val repo = Repository(this)
        repo.showRoom("", 1)
        repo.getShowRoomData().observe(this) { response ->
            binding.lottie6.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
            if (response.status == true) {
                binding.specialRecyclerView.adapter = SpecialAdapter(response.data as ArrayList<ShowRoomData>)
            }
        }

        binding.filterMenuCover2.setOnTouchListener { _, _ ->
            if (binding.filterMenu.isVisible) hideFilterMenu()
            true
        }
    }

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
        dialog.getStatus().observe(this) { selection ->
            binding.lottie6.visibility = View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo = Repository(this)
            repo.showRoom(selection.id, 1)
            repo.getShowRoomData().observe(this) { response ->
                binding.lottie6.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                if (response.status == true) {
                    binding.specialRecyclerView.adapter = SpecialAdapter(response.data as ArrayList<ShowRoomData>)
                }
            }
        }
    }

    private fun filterSliderInit() {
        binding.filterMenu.layoutManager = LinearLayoutManager(this)
        val adapter = SearchFilterAdapter()
        binding.filterMenu.adapter = adapter
        adapter.getResult().observe(this) { result ->
            when (result) {
                "show" -> {
                    binding.lottie6.visibility = View.VISIBLE
                    Tools().viewEnable(this.window.decorView.rootView, false)
                }
                "hide" -> {
                    binding.lottie6.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                }
                else -> applyFilter()
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
            "filter",
            JSONObject().put("address", addressData)
                .put("price", priceData)
                .put("options", optionsData)
                .put("others", othersData)
        )
    }

    fun filter(view: View) {
        if (!binding.filterMenu.isVisible) {
            binding.filterMenu.visibility = View.VISIBLE
            binding.filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left))
            binding.filterMenuCover2.visibility = View.VISIBLE
        }
    }

    private fun hideFilterMenu() {
        binding.filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right))
        binding.filterMenu.visibility = View.GONE
        binding.filterMenuCover2.visibility = View.GONE
        val imgr = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
    }

    fun back(view: View) {
        onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }
}
