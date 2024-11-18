package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityFavoriteBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.FavoriteData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.FavoriteAdapter

class Favorite : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private var itemList = ArrayList<FavoriteData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.favoriteRoot)
        Tools().setBasedLogo(this, R.id.imageView38)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun pageInit() {
        binding.lottie6.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(this)

        val repo = Repository(this)
        repo.favorite()
        repo.getFavoriteData().observe(this) { response ->
            binding.lottie6.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)

            if (response.status) {
                itemList = response.data as ArrayList<FavoriteData>
                itemList.reverse()

                if (itemList.isEmpty()) {
                    binding.notificationNo2.visibility = View.VISIBLE
                } else {
                    val adapter = FavoriteAdapter(itemList)
                    binding.favoriteRecyclerView.adapter = adapter

                    adapter.getResult().observe(this) { position ->
                        itemList.removeAt(position)
                        adapter.notifyDataSetChanged()
                        if (itemList.isEmpty()) binding.notificationNo2.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.notificationNo2.visibility = View.VISIBLE
            }
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        pageInit()
        Log.d("Favorite", "onResume")
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") {
                View.LAYOUT_DIRECTION_LTR
            } else {
                View.LAYOUT_DIRECTION_RTL
            }
    }
}
