package com.application.adverial.ui.navigation

//import kotlinx.android.synthetic.main.activity_profile.profile_message
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
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.ContactUs
import com.application.adverial.ui.activity.Favorite
import com.application.adverial.ui.activity.Login
import com.application.adverial.ui.activity.MyAccount
import com.application.adverial.ui.activity.MyAds
import com.application.adverial.ui.activity.Notifications
import com.application.adverial.ui.activity.Special
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_profile.lottie19
import kotlinx.android.synthetic.main.activity_profile.profileRoot
import kotlinx.android.synthetic.main.activity_profile.profile_about
import kotlinx.android.synthetic.main.activity_profile.profile_contactUs
import kotlinx.android.synthetic.main.activity_profile.profile_drawerLayout
import kotlinx.android.synthetic.main.activity_profile.profile_favorite1
import kotlinx.android.synthetic.main.activity_profile.profile_logout
import kotlinx.android.synthetic.main.activity_profile.profile_logoutSeparator
import kotlinx.android.synthetic.main.activity_profile.profile_myAccount
import kotlinx.android.synthetic.main.activity_profile.profile_myAdsActive
import kotlinx.android.synthetic.main.activity_profile.profile_notifications
import kotlinx.android.synthetic.main.activity_profile.profile_privacy_policy
import kotlinx.android.synthetic.main.activity_profile.profile_special
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class Profile : AppCompatActivity() {

    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Tools().changeViewFromTheme(this,profileRoot)
        Tools().setThemeBasedImage(this, R.id.imageView23)
        drawerInit()
        pageInit()
        buttons()
    }

    private fun pageInit(){
        lottie19.visibility= View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        if(Tools().authCheck(this)){
            profile_logout.visibility= View.VISIBLE
            profile_logoutSeparator.visibility= View.VISIBLE
        }
        menuCategoriesRecyclerView.layoutManager= LinearLayoutManager(this)
        val coroutineScope= CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO){
            val repo= Repository(this@Profile)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@Profile) {
                    if (it.status) menuCategoriesRecyclerView.adapter = MenuCategoryAdapter(it.data)
                    lottie19.visibility= View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                }
            }
        }
    }

    private fun buttons(){
        profile_favorite1.setOnClickListener{
            if(Tools().authCheck(this)){
                val intent= Intent(this, Favorite::class.java)
                startActivity(intent)
            }else{
                val intent= Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
        profile_myAdsActive.setOnClickListener{
            if(Tools().authCheck(this)){
                val intent= Intent(this, MyAds::class.java)
                getSharedPreferences("data", 0).edit().putString("type", "active").apply()
                startActivity(intent)
            }else{
                val intent= Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
//        profile_myAdsDeactive.setOnClickListener{
//            if(Tools().authCheck(this)){
//                val intent= Intent(this, MyAds::class.java)
//                getSharedPreferences("data", 0).edit().putString("type", "deactive").apply()
//                startActivity(intent)
//            }else{
//                val intent= Intent(this, Login::class.java)
//                startActivity(intent)
//            }
//        }
//        profile_message.setOnClickListener{
//            if(Tools().authCheck(this)){
//                val intent= Intent(this, MessagesList::class.java)
//                startActivity(intent)
//            }else{
//                val intent= Intent(this, Login::class.java)
//                startActivity(intent)
//            }
//        }
        profile_special.setOnClickListener{
            if(Tools().authCheck(this)){
                val intent= Intent(this, Special::class.java)
                startActivity(intent)
            }else{
                val intent= Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
        profile_notifications.setOnClickListener{
            if(Tools().authCheck(this)){
                val intent= Intent(this, Notifications::class.java)
                startActivity(intent)
            }else{
                val intent= Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
        profile_about.setOnClickListener{
            Tools().openBrowser(this, "https://www.adverial.com")
        }
        profile_privacy_policy.setOnClickListener{
            Tools().openBrowser(this, "https://adverial.com/privacy.html")
        }
        profile_myAccount.setOnClickListener{
            if(Tools().authCheck(this)){
                val intent= Intent(this, MyAccount::class.java)
                startActivity(intent)
            }else{
                val intent= Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
        profile_contactUs.setOnClickListener{
            if(Tools().authCheck(this)){
                val intent= Intent(this, ContactUs::class.java)
                startActivity(intent)
            }else{
                val intent= Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
        profile_logout.setOnClickListener{
            getSharedPreferences("user", 0).edit().putString("token", "").apply()
            Tools().goto(this, Home(), false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun drawerInit(){
        val navigationView = findViewById<View>(R.id.nav_menu) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        add= headerView.findViewById(R.id.menu_newAd)
        profile= headerView.findViewById(R.id.menu_profile)
        val name= headerView.findViewById<TextView>(R.id.menu_name)
        menuCategoriesRecyclerView= headerView.findViewById(R.id.menu_recyclerView)
        menuCategoriesRecyclerView.layoutManager= LinearLayoutManager(this)
        if(Tools().authCheck(this)){
            val repo = Repository(this)
            repo.user()
            repo.getUserData().observe(this) { userData ->
                userData?.let {
                    name.text = "Welcome " + it.data?.name + ","
                }
            }

        }else name.text= "Welcome"
        add.setOnClickListener{
            val intent= Intent(this, NewAd::class.java)
            startActivity(intent)
            finish()
        }
        profile.setOnClickListener{
            val intent= Intent(this, Profile::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("WrongConstant")
    fun menu(view: View){ profile_drawerLayout.openDrawer(Gravity.START) }

    fun home(view: View){
        val intent= Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }

    fun favorites(view: View){
        if(Tools().authCheck(this)){
            val intent= Intent(this, Favorites::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }else{
            val intent= Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    fun newAd(view: View){
        val intent= Intent(this, NewAd::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }

    fun notifications(view: View){
        if(Tools().authCheck(this)){
            val intent= Intent(this, com.application.adverial.ui.navigation.Notifications::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }else{
            val intent= Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    fun profile(view: View){
        val intent= Intent(this, Profile::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
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