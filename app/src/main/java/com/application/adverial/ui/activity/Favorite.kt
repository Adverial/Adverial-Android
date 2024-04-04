package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.FavoriteData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.FavoriteAdapter
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.activity_favorite.lottie6

class Favorite : AppCompatActivity() {

    private var itemList= ArrayList<FavoriteData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        Tools().rotateLayout(this,favoriteRoot)
        Tools().changeViewFromTheme(this,favoriteRoot)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun pageInit(){
        lottie6.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        favorite_recyclerView.layoutManager= LinearLayoutManager(this)
        val repo= Repository(this)
        repo.favorite()
        repo.getFavoriteData().observe(this, {
            lottie6.visibility= View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
            if(it.status){
                itemList= it.data as ArrayList<FavoriteData>
                itemList.reverse()
                if(itemList.isEmpty()) notification_no2.visibility= View.VISIBLE
                val adapter= FavoriteAdapter(itemList)
                favorite_recyclerView.adapter= adapter
                adapter.getResult().observe(this, { position ->
                    itemList.removeAt(position)
                    adapter.notifyDataSetChanged()
                    if(itemList.isEmpty()) notification_no2.visibility= View.VISIBLE
                })
            }else notification_no2.visibility= View.VISIBLE
        })
    }

    fun back(view: View){ finish() }

    override fun onResume() {
        super.onResume()
        pageInit()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }


}