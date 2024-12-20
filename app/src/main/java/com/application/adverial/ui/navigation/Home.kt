package com.application.adverial.ui.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.Objects

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var showRoomStatus = false
    private var categoriesStatus = false
    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView
    private var theme = "light"
    private lateinit var homeAdsAdapter: HomePostsAdapter
    private var scrollPermission = false
    private var isFinished = false
    private var page = 0
    private val posts = ArrayList<ShowRoomData>()
    private var showRoomType = ""

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.homeRoot)

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchDataAds()
        }
        drawerInit()
        pageInit()
        Tools().setBasedLogo(this, R.id.home_logo)
        Tools().setBarBackground(this, R.id.image_bg_bar)

        if (!NotificationUtils.areNotificationsEnabled(this))
            NotificationUtils.requestNotificationPermission(this)
        else
            NewMessageNotification.setupNewMessageNotification(this)
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

    private fun pageInit() {
        Tools().viewEnable(window.decorView.rootView, true)
        binding.homeCategory.layoutManager = LinearLayoutManager(this)
        binding.homeProducts.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        homeAdsAdapter = HomePostsAdapter(posts)
        binding.homeProducts.adapter = homeAdsAdapter
        binding.homeProducts.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!binding.homeProducts.canScrollVertically(1)) nextPage()
        }

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO) {
            val repo = Repository(this@Home)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@Home) {
                    if (it.status) {
                        val list = ArrayList<SubCategory>()
                        for (i in it.data) list.add(i)
                        menuCategoriesRecyclerView.adapter =
                            MenuCategoryAdapter(list as List<SubCategory>)
                        binding.homeCategory.adapter = HomeCategoryAdapter(list as List<SubCategory>)
                    }
                    categoriesStatus = true
                }
            }
        }

        nextPage()
        updateLanguageUI()
    }

    private fun nextPage() {
        if (!isFinished) {
            scrollPermission = false
            page++
            val coroutineScope1 = CoroutineScope(Dispatchers.IO)
            coroutineScope1.async(Dispatchers.IO) {
                val repo = Repository(this@Home)
                repo.showRoom(showRoomType, page)
                runOnUiThread {
                    repo.getShowRoomData().observe(this@Home) {
                        if (it.data.isNullOrEmpty()) {
                            isFinished = true
                        } else {
                            for (i in it.data ?: listOf()) {
                                posts.add(i)
                            }
                            homeAdsAdapter.notifyDataSetChanged()
                            scrollPermission = true
                            showRoomStatus = true
                        }
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
        menuCategoriesRecyclerView = headerView.findViewById(R.id.menu_recyclerView)
        menuCategoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        if (Tools().authCheck(this)) {
            val repo = Repository(this)
            repo.user()
            repo.getUserData().observe(this) { userData ->
                userData?.let {
                    name.text = "Welcome " + it.data?.name + ","
                }
            }
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

    fun language(view: View) {
        val dialog = Language()
        dialog.show(supportFragmentManager, "Language")
        dialog.getStatus().observe(this) {
            getSharedPreferences("user", 0).edit().putString("languageName", it.name)
                .putString("languageId", it.id).putInt("languageFlag", it.flag).apply()
            updateLanguageUI()
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

    fun sort(view: View) {
        val sortItems = ArrayList<com.application.adverial.ui.model.DropList>()
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_default), ""))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_PriceLowToHigh), "3"))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_priceHighToLow), "4"))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_date), "2"))
        sortItems.add(com.application.adverial.ui.model.DropList(resources.getString(R.string.sort_name), "1"))
        val dialog = DropList(sortItems, resources.getString(R.string.sort_title))
        dialog.show(supportFragmentManager, "DropList")
        dialog.getStatus().observe(this) { itt ->
            Tools().viewEnable(view, false)
            showRoomType = itt.id
            page = 0
            scrollPermission = true
            isFinished = false
            posts.clear()
            nextPage()
        }
    }

    @SuppressLint("WrongConstant")
    fun menu(view: View) {
        binding.homeDrawerLayout.openDrawer(Gravity.START)
    }

    fun search(view: View) {
        val dialog = SearchDialog()
        dialog.show(supportFragmentManager, "SearchDialog")
    }

    fun voice(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        val lang = getSharedPreferences("user", 0)
        when (lang.getString("languageName", "English")) {
            "English" -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
            "Arabic" -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
            else -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        try {
            startActivityForResult(intent, 11)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.voiceError), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val intent = Intent(this, SearchResult::class.java)
                intent.putExtra("keyword", Objects.requireNonNull(result)?.get(0))
                startActivity(intent)
            }
        }
    }

    fun home(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun favorites(view: View) {
        if (Tools().authCheck(this)) {
            val intent = Intent(this, Favorites::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        } else {
            val intent = Intent(this, LoginWa::class.java)
            startActivity(intent)
        }
    }

    fun newAd(view: View) {
        val intent = Intent(this, NewAd::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun openNotifications(view: View) {
        if (Tools().authCheck(this)) {
            val intent = Intent(this, Notifications::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        } else {
            val intent = Intent(this, LoginWa::class.java)
            startActivity(intent)
        }
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
        Tools().autoDarkMode(this, theme)

        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection = if (language == "" || language == "0" || language == "1") {
            View.LAYOUT_DIRECTION_LTR
        } else {
            View.LAYOUT_DIRECTION_RTL
        }
    }
}
