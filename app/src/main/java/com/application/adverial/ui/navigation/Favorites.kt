package com.application.adverial.ui.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.databinding.ActivityFavoritesBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.FavoriteData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.FavoriteAdapter
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class Favorites : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private var itemList = ArrayList<FavoriteData>()
    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private var favoriteStatus = false
    private var categoriesStatus = false
    private var lang = "en"

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.activityFavoritesRoot)
        Tools().setBasedLogo(this, binding.imageView51.id)

        drawerInit()
        pageInit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun pageInit() {
        binding.lottie2.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        // Accessing RecyclerView within the NavigationView header
        val headerView = binding.navMenu.getHeaderView(0)
        val menuCategoriesRecyclerView = headerView.findViewById<RecyclerView>(R.id.menu_recyclerView)
        menuCategoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the favorite recycler view
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(this)

        // Using lifecycleScope to launch coroutines
        lifecycleScope.launch {
            val repo = Repository(this@Favorites)

            // Fetch categories
            repo.mainCategory()
            repo.getMainCategoryData().observe(this@Favorites) { categoryResponse ->
                categoryResponse?.let {
                    if (it.status) {
                        menuCategoriesRecyclerView.adapter = MenuCategoryAdapter(it.data)
                        categoriesStatus = true
                        lottieHide()
                    }
                }
            }

            // Fetch favorites
            repo.favorite()
            repo.getFavoriteData().observe(this@Favorites) { favoriteResponse ->
                favoriteResponse?.let {
                    if (it.status) {
                        itemList = it.data as ArrayList<FavoriteData>
                        itemList.reverse()
                        val adapter = FavoriteAdapter(itemList)
                        binding.favoriteRecyclerView.adapter = adapter
                        adapter.getResult().observe(this@Favorites) { position ->
                            itemList.removeAt(position)
                            adapter.notifyDataSetChanged()
                            if (itemList.isEmpty()) binding.favoritesNo.visibility = View.VISIBLE
                        }
                        if (itemList.isEmpty()) binding.favoritesNo.visibility = View.VISIBLE
                    } else {
                        binding.favoritesNo.visibility = View.VISIBLE
                    }
                    favoriteStatus = true
                    lottieHide()
                } ?: run {
                    binding.favoritesNo.visibility = View.VISIBLE
                    favoriteStatus = true
                    lottieHide()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun drawerInit() {
        val navigationView = binding.navMenu
        val headerView = navigationView.getHeaderView(0)

        // Accessing views inside the navigation header
        add = headerView.findViewById(R.id.menu_newAd)
        profile = headerView.findViewById(R.id.menu_profile)
        val name = headerView.findViewById<TextView>(R.id.menu_name)
        val menuCategoriesRecyclerView = headerView.findViewById<RecyclerView>(R.id.menu_recyclerView)

        menuCategoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch user data if authenticated
        if (Tools().authCheck(this)) {
            val repo = Repository(this)
            repo.user()
            repo.getUserData().observe(this) { userData ->
                userData?.let {
                    name.text = "Welcome " + it.data?.name + ","
                }
            }
        } else {
            name.text = "Welcome"
        }

        // Set click listeners for navigation items
        add.setOnClickListener {
            val intent = Intent(this, NewAd::class.java)
            startActivity(intent)
            finish()
        }
        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            finish()
        }
    }


    @SuppressLint("WrongConstant")
    fun menu(view: View) {
        binding.favoritesDrawerLayout.openDrawer(Gravity.START)
    }

    private fun lottieHide() {
        if (categoriesStatus && favoriteStatus) {
            binding.lottie2.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }
    }

    fun home(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun favorites(view: View) {
        val intent = Intent(this, Favorites::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun newAd(view: View) {
        val intent = Intent(this, NewAd::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun notifications(view: View) {
        val intent = Intent(this, Notifications::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun profile(view: View) {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1")
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        else
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
