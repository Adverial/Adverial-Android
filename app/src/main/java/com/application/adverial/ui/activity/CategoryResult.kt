package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Ad
import com.application.adverial.remote.model.ShowRoomData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.FilterAdapter
import com.application.adverial.ui.adapter.HomePostsAdapter
import com.application.adverial.ui.adapter.SearchResultsAdapter
import com.application.adverial.ui.dialog.DropList
import kotlinx.android.synthetic.main.activity_home.home_products
import kotlinx.android.synthetic.main.activity_new_ad_category.*
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.json.JSONArray
import org.json.JSONObject

class CategoryResult : AppCompatActivity() {

    private var id= ""
    private var name= ""
    private var type= ""
    private lateinit var adapter: SearchResultsAdapter
    private var scrollPermission= false
    private var isFinished= false
    private var page= 0
    private val posts= ArrayList<Ad>()
    private var sort= ""

    override fun onBackPressed() {
        if(filterMenu.isVisible) hideFilterMenu()
        else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        Tools().rotateLayout(this,imageView6)
        pageInit()
        filterSliderInit()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun pageInit(){
        getSharedPreferences("filter", 0).edit().clear().apply()
        getSharedPreferences("filterOptions", 0).edit().clear().apply()
        lottie5.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        id= intent.getStringExtra("id")!!
        name= intent.getStringExtra("name")!!
        type= intent.getStringExtra("type")!!
        result_name.text= name
        result_recyclerView.layoutManager= LinearLayoutManager(this)
        adapter= SearchResultsAdapter(posts)
        result_recyclerView.adapter= adapter
        result_recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!result_recyclerView.canScrollVertically(1)) nextPage()
        }

        nextPage()

        filterMenuCover.setOnTouchListener { _, _ ->
            if (filterMenu.isVisible) {
                hideFilterMenu()
            }
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun nextPage(){
        if(!isFinished){
            scrollPermission= false
            page++
            Log.e("", page.toString())
            val repo= Repository(this)
            repo.categoryAds(id, sort, page)
            repo.getCategoryAdsData().observe(this) {
                lottie5.visibility = View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                result_count.text = "${it.data.ad_list?.size} ${getString(R.string.result_count)}"
                if (it.data.ad_list.isNullOrEmpty()) {
                    isFinished = true
                } else {
                    for (i in it.data.ad_list ?: listOf()) {
                        posts.add(i)
                    }
                    adapter.notifyDataSetChanged()
                    scrollPermission = true
                }
            }
        }
    }

    private fun filterSliderInit(){
        filterMenu.layoutManager= LinearLayoutManager(this)
        val adapter= FilterAdapter(type)
        filterMenu.adapter= adapter
        adapter.getResult().observe(this) {
            when (it) {
                "show" -> {
                    lottie5.visibility = View.VISIBLE
                    Tools().viewEnable(this.window.decorView.rootView, false)
                }
                "hide" -> {
                    lottie5.visibility = View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                }
                else -> {
                    applyFilter()
                }
            }
        }
    }

    private fun applyFilter(){
        val data= getSharedPreferences("filter", 0)
        val dataOptions= getSharedPreferences("filterOptions", 0)
        val addressData= JSONObject().put("country", data.getString("country", "")).put("city", data.getString("city", "")).put("district", data.getString("district", ""))
        val priceData= JSONObject().put("min", data.getString("priceMin", "")).put("max", data.getString("priceMax", "")).put("currency", data.getString("currency", ""))
        val othersData= JSONObject().put("photo", data.getString("photo", "")).put("video", data.getString("video", ""))
        val optionsData = JSONArray()
        val allEntries: Map<String, *> = dataOptions.all
        for ((key, value) in allEntries) {
            val jsonObj = JSONObject().put("option_id", key).put("value", value.toString())
            optionsData.put(jsonObj)
        }
        val filter = JSONObject().put("filter", JSONObject().put("address", addressData).put("price", priceData).put("options", optionsData).put("others", othersData))
        Log.d("ddd", filter.toString())
        hideFilterMenu()
        lottie5.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        val repo= Repository(this)
        repo.filterCategory(id, filter.toString())
        repo.getFilterCategoryData().observe(this) {
            if (it.status) {
                result_recyclerView.adapter = SearchResultsAdapter(it.data as ArrayList<Ad>)
            }
            lottie5.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
        }
    }

    fun filter(view: View){
        if(!filterMenu.isVisible){
            filterMenu.visibility= View.VISIBLE
            filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left))
            filterMenuCover.visibility= View.VISIBLE
        }
    }

    private fun hideFilterMenu(){
        filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right))
        filterMenu.visibility = View.GONE
        filterMenuCover.visibility=View.GONE
        val imgr: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
    }

    @SuppressLint("SetTextI18n")
    fun sort(view: View){
        val sortItems= ArrayList<com.application.adverial.ui.model.DropList>()
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_default), ""))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_PriceLowToHigh), "3"))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_priceHighToLow), "4"))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_date), "2"))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_name), "1"))
        val dialog= DropList(sortItems, resources.getString(R.string.sort_title))
        dialog.show(supportFragmentManager, "DropList")
        dialog.getStatus().observe(this, { itt->
            lottie5.visibility= View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            sort= itt.id
            page= 0
            scrollPermission= true
            isFinished= false
            posts.clear()
            nextPage()
        })
    }

    fun back(view: View){
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}