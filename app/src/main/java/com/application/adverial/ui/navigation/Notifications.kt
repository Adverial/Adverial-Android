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
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.databinding.ActivityNotifications2Binding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.NotificationData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.application.adverial.ui.adapter.NotificationAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class Notifications : AppCompatActivity() {

    private lateinit var binding: ActivityNotifications2Binding
    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private var notificationsStatus = false
    private var categoriesStatus = false

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifications2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.notificationRoot)
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        Tools().setBasedLogo(this, binding.imageView47.id)

        loadNotifications()
        drawerInit()
        pageInit()
    }

    private fun loadNotifications() {
        val repo = Repository(this)
        repo.notification()
        repo.getNotificationData().observe(this) { notifications ->
            notifications?.let {
                if (it.status) {
                    binding.notificationsRecyclerView.adapter = NotificationAdapter(it.data as ArrayList<NotificationData>)
                    if (it.data.isEmpty()) binding.notificationNo.visibility = View.VISIBLE
                } else {
                    binding.notificationNo.visibility = View.VISIBLE
                }
                notificationsStatus = true
                lottieHide()
            } ?: run {
                binding.notificationNo.visibility = View.VISIBLE
                notificationsStatus = true
                lottieHide()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun pageInit() {
        binding.lottie17.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO) {
            val repo = Repository(this@Notifications)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@Notifications) {
                    if (it.status) {
                        binding.navMenu.getHeaderView(0).findViewById<RecyclerView>(R.id.menu_recyclerView).apply {
                            layoutManager = LinearLayoutManager(this@Notifications)
                            adapter = MenuCategoryAdapter(it.data)
                        }
                        categoriesStatus = true
                        lottieHide()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun drawerInit() {
        val navigationView = binding.navMenu
        val headerView = navigationView.getHeaderView(0)
        add = headerView.findViewById(R.id.menu_newAd)
        profile = headerView.findViewById(R.id.menu_profile)
        val name = headerView.findViewById<TextView>(R.id.menu_name)
        val menuCategoriesRecyclerView = headerView.findViewById<RecyclerView>(R.id.menu_recyclerView)
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
            startActivity(Intent(this, NewAd::class.java))
            finish()
        }
        profile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
            finish()
        }
    }

    @SuppressLint("WrongConstant")
    fun menu(view: View) {
        binding.notificationsDrawerLayout.openDrawer(Gravity.START)
    }

    private fun lottieHide() {
        if (categoriesStatus && notificationsStatus) {
            binding.lottie17.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }
    }

    fun home(view: View) {
        startActivity(Intent(this, Home::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    fun favorites(view: View) {
        startActivity(Intent(this, Favorites::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    fun newAd(view: View) {
        startActivity(Intent(this, NewAd::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    fun profile(view: View) {
        startActivity(Intent(this, Profile::class.java))
        overridePendingTransition(0, 0)
        finish()
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
