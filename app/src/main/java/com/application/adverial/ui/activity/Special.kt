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
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.ShowRoomData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.SearchFilterAdapter
import com.application.adverial.ui.adapter.SpecialAdapter
import com.application.adverial.ui.dialog.DropList
import kotlinx.android.synthetic.main.activity_favorite.lottie6
import kotlinx.android.synthetic.main.activity_favorite.newAdCategory_back2
import kotlinx.android.synthetic.main.activity_search_result.filterMenu
import kotlinx.android.synthetic.main.activity_special.filterMenuCover2
import kotlinx.android.synthetic.main.activity_special.specialRoot
import kotlinx.android.synthetic.main.activity_special.special_recyclerView
import org.json.JSONArray
import org.json.JSONObject

class Special : AppCompatActivity() {

    override fun onBackPressed() {
        if(filterMenu.isVisible) hideFilterMenu()
        else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special)
        Tools().rotateLayout(this,newAdCategory_back2)
        Tools().changeViewFromTheme(this,specialRoot)

        pageInit()
        filterSliderInit()
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    private fun pageInit(){
        lottie6.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        special_recyclerView.layoutManager= LinearLayoutManager(this)
        val repo= Repository(this)
        repo.showRoom("", 1)
        repo.getShowRoomData().observe(this, {
            lottie6.visibility= View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
            if(it.status == true) {
                special_recyclerView.adapter = SpecialAdapter(it.data as ArrayList<ShowRoomData>)
            }
        })

        filterMenuCover2.setOnTouchListener { _, _ ->
            if (filterMenu.isVisible) {
                hideFilterMenu()
            }
            true
        }
    }

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
            lottie6.visibility= View.VISIBLE
            Tools().viewEnable(this.window.decorView.rootView, false)
            val repo= Repository(this)
            repo.showRoom(itt.id, 1)
            repo.getShowRoomData().observe(this, {
                lottie6.visibility= View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                if(it.status == true) {
                    special_recyclerView.adapter = SpecialAdapter(it.data as ArrayList<ShowRoomData>)
                }
            })
        })
    }

    private fun filterSliderInit(){
        filterMenu.layoutManager= LinearLayoutManager(this)
        val adapter= SearchFilterAdapter()
        filterMenu.adapter= adapter
        adapter.getResult().observe(this, {
            if(it == "show"){
                lottie6.visibility= View.VISIBLE
                Tools().viewEnable(this.window.decorView.rootView, false)
            }else if(it == "hide"){
                lottie6.visibility= View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
            }else{
                applyFilter()
            }
        })
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
//        Log.d("ddd", filter.toString())
    }

    fun filter(view: View){
        if(!filterMenu.isVisible){
            filterMenu.visibility= View.VISIBLE
            filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left))
            filterMenuCover2.visibility= View.VISIBLE
        }
    }

    private fun hideFilterMenu(){
        filterMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right))
        filterMenu.visibility = View.GONE
        filterMenuCover2.visibility=View.GONE
        val imgr: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
    }

    fun back(view: View){ onBackPressed() }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}