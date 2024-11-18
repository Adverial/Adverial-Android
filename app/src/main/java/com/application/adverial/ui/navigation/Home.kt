package com.application.adverial.ui.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.databinding.ActivityHomeBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.ShowRoomData
import com.application.adverial.remote.model.SubCategory
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.LoginWa
import com.application.adverial.ui.activity.SearchResult
import com.application.adverial.ui.adapter.HomeCategoryAdapter
import com.application.adverial.ui.adapter.HomePostsAdapter
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.application.adverial.ui.dialog.DropList
import com.application.adverial.ui.dialog.Language
import com.application.adverial.ui.dialog.SearchDialog
import com.application.adverial.utils.NewMessageNotification
import com.application.adverial.utils.NotificationUtils
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeAdsAdapter: HomePostsAdapter
    private val posts = ArrayList<ShowRoomData>()
    private var showRoomType = ""
    private var page = 0
    private var scrollPermission = true
    private var isFinished = false
    private var showRoomStatus = false
    private var categoriesStatus = false
    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.homeRoot)

        setupSwipeRefresh()
        drawerInit()
        pageInit()
        Tools().setBasedLogo(this, R.id.home_logo)
        Tools().setBarBackground(this, R.id.image_bg_bar)

        if (!NotificationUtils.areNotificationsEnabled(this)) {
            NotificationUtils.requestNotificationPermission(this)
        } else {
            NewMessageNotification.setupNewMessageNotification(this)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchDataAds()
        }
    }

    private fun fetchDataAds() {
        posts.clear()
        homeAdsAdapter.notifyDataSetChanged()
        page = 0
        scrollPermission = true
        isFinished = false
        nextPage()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pageInit() {
        Tools().viewEnable(window.decorView.rootView, true)
        binding.homeCategory.layoutManager = LinearLayoutManager(this)
        binding.homeProducts.layoutManager = GridLayoutManager(this, 2)
        homeAdsAdapter = HomePostsAdapter(posts)
        binding.homeProducts.adapter = homeAdsAdapter

        binding.homeProducts.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!binding.homeProducts.canScrollVertically(1)) nextPage()
        }

        fetchCategories()
        nextPage()
        updateLanguageUI()
    }

    private fun fetchCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            val repo = Repository(this@Home)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@Home) { response ->
                    if (response.status) {
                        val list = ArrayList<SubCategory>(response.data)
                        val headerView = binding.navMenu.getHeaderView(0)
                        val menuRecyclerView = headerView.findViewById<RecyclerView>(R.id.menu_recyclerView)
                        menuRecyclerView.layoutManager = LinearLayoutManager(this@Home)
                        menuRecyclerView.adapter = MenuCategoryAdapter(list)

                        binding.homeCategory.adapter = HomeCategoryAdapter(list)
                        categoriesStatus = true
                    }
                }
            }
        }
    }
    fun language(view: View) {
        val dialog = Language()
        dialog.show(supportFragmentManager, "Language")
        dialog.getStatus().observe(this) { selectedLanguage ->
            getSharedPreferences("user", 0).edit()
                .putString("languageName", selectedLanguage.name)
                .putString("languageId", selectedLanguage.id)
                .putInt("languageFlag", selectedLanguage.flag)
                .apply()
            updateLanguageUI()
        }
    }

    fun menu(view: View) {
        // Open the navigation drawer
        binding.homeDrawerLayout.openDrawer(GravityCompat.START)
    }

    private fun nextPage() {
        if (isFinished || !scrollPermission) return
        scrollPermission = false
        page++

        lifecycleScope.launch(Dispatchers.IO) {
            val repo = Repository(this@Home)
            repo.showRoom(showRoomType, page)
            runOnUiThread {
                repo.getShowRoomData().observe(this@Home) { response ->
                    if (response.data.isNullOrEmpty()) {
                        isFinished = true
                    } else {
                        posts.addAll(response.data!!)
                        homeAdsAdapter.notifyDataSetChanged()
                        scrollPermission = true
                        showRoomStatus = true
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun drawerInit() {
        val headerView = binding.navMenu.getHeaderView(0)
        add = headerView.findViewById(R.id.menu_newAd)
        profile = headerView.findViewById(R.id.menu_profile)
        val name = headerView.findViewById<TextView>(R.id.menu_name)

        if (Tools().authCheck(this)) {
            val repo = Repository(this)
            repo.user()
            repo.getUserData().observe(this) {
                name.text = "Welcome ${it?.data?.name ?: ""},"
               // if (it.status) it.data.id?.let { userId -> messageAdapter.setCurrentUserId(userId) }
            }
//            repo.user().observe(this) { userData ->
//                name.text = "Welcome ${userData?.data?.name ?: ""},"
//            }
        } else {
            name.text = getString(R.string.menu_welcome)
        }

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

    private fun updateLanguageUI() {
        binding.homeLanguage.text = when (getSharedPreferences("user", 0).getString("languageId", "0")) {
            "0" -> "En"
            "1" -> "Tr"
            "2" -> "Ar"
            "3" -> "Kb"
            else -> "Ks"
        }
    }

    private fun lottieHide() {
        if (categoriesStatus && showRoomStatus) {
            Tools().viewEnable(window.decorView.rootView, true)
        }
    }

    fun home(view: View) {
        startActivity(Intent(this, Home::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    fun favorites(view: View) {
        if (Tools().authCheck(this)) {
            startActivity(Intent(this, Favorites::class.java))
        } else {
            startActivity(Intent(this, LoginWa::class.java))
        }
    }

    fun newAd(view: View) {
        startActivity(Intent(this, NewAd::class.java))
    }

    fun profile(view: View) {
        startActivity(Intent(this, Profile::class.java))
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection = if (language == "0" || language == "1") {
            View.LAYOUT_DIRECTION_LTR
        } else {
            View.LAYOUT_DIRECTION_RTL
        }
    }
}
