package com.application.adverial.ui.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.databinding.ActivityProfileBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.*
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.profileRoot)
        Tools().setBasedLogo(this, binding.imageView23.id)
        drawerInit()
        pageInit()
        setupButtons()
    }

    private fun pageInit() {
        binding.lottie19.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        if (Tools().authCheck(this)) {
            binding.profileLogout.visibility = View.VISIBLE
            binding.profileLogoutSeparator.visibility = View.VISIBLE
        }

        binding.menuRecyclerView.layoutManager = LinearLayoutManager(this)

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO) {
            val repo = Repository(this@Profile)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@Profile) {
                    if (it.status) {
                        binding.menuRecyclerView.adapter = MenuCategoryAdapter(it.data)
                    }
                    binding.lottie19.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                }
            }
        }

        val homeButton: ImageView = findViewById(R.id.home_home)
        homeButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        val favoriteButton: ImageView = findViewById(R.id.home_favorite)
        favoriteButton.setOnClickListener {
            val intent = Intent(this, Favorites::class.java)
            startActivity(intent)
        }

        //home_add
        val addButton: ImageView = findViewById(R.id.home_add)
        addButton.setOnClickListener {
            val intent = Intent(this, NewAd::class.java)
            startActivity(intent)
        }

        //home_notification
        val notificationButton: ImageView = findViewById(R.id.home_notification)
        notificationButton.setOnClickListener {
            val intent = Intent(this, Notifications::class.java)
            startActivity(intent)
        }

        //home_profile
//        val profileButton: ImageView = findViewById(R.id.home_profile)
//        profileButton.setOnClickListener {
//            val intent = Intent(this, Profile::class.java)
//            startActivity(intent)
//        }
    }

    private fun setupButtons() {
        binding.profileFavorite1.setOnClickListener {
            navigateIfAuthenticated(Favorite::class.java)
        }

        binding.profileMyAdsActive.setOnClickListener {
            if (Tools().authCheck(this)) {
                val intent = Intent(this, MyAds::class.java)
                getSharedPreferences("data", 0).edit().putString("type", "active").apply()
                startActivity(intent)
            } else {
                navigateToLoginWa()
            }
        }

        binding.profileMessage.setOnClickListener {
            navigateIfAuthenticated(ChatActivity::class.java)
        }

        binding.profileSpecial.setOnClickListener {
            navigateIfAuthenticated(Special::class.java)
        }

        binding.profileNotifications.setOnClickListener {
            navigateIfAuthenticated(Notifications::class.java)
        }

        binding.profileAbout.setOnClickListener {
            Tools().openBrowser(this, BuildConfig.API_BASE_URL)
        }

        binding.profilePrivacyPolicy.setOnClickListener {
            Tools().openBrowser(this, "https://adverial.net/privacy.html")
        }

        binding.profileMyAccount.setOnClickListener {
            navigateIfAuthenticated(MyAccount::class.java)
        }

        binding.profileContactUs.setOnClickListener {
            navigateIfAuthenticated(ContactUs::class.java)
        }

        binding.profileLogout.setOnClickListener {
            getSharedPreferences("user", 0).edit().putString("token", "").apply()
            Tools().goto(this, Home(), false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun drawerInit() {
        val navigationView = binding.navMenu
        val headerView = navigationView.getHeaderView(0)
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
            navigateToActivity(NewAd::class.java)
        }
        profile.setOnClickListener {
            navigateToActivity(Profile::class.java)
        }
    }

    @SuppressLint("WrongConstant")
    fun menu(view: View) {
        binding.profileDrawerLayout.openDrawer(Gravity.START)
    }

    private fun navigateIfAuthenticated(targetActivity: Class<*>) {
        if (Tools().authCheck(this)) {
            startActivity(Intent(this, targetActivity))
        } else {
            navigateToLoginWa()
        }
    }

    private fun navigateToActivity(targetActivity: Class<*>) {
        startActivity(Intent(this, targetActivity))
        finish()
    }

    private fun navigateToLoginWa() {
        startActivity(Intent(this, LoginWa::class.java))
    }

    fun profile(view: View) {
        // Since we're already in the Profile activity, no need to navigate
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
