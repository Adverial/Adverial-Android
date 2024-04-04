package com.application.adverial.ui.navigation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.NotificationData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.application.adverial.ui.adapter.NotificationAdapter
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_notifications2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class Notifications : AppCompatActivity() {

    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView
    private var notificationsStatus= false
    private var categoriesStatus= false

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications2)
        Tools().changeViewFromTheme(this,notificationRoot)

        drawerInit()
        pageInit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun pageInit(){
        lottie17.visibility= View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        menuCategoriesRecyclerView.layoutManager= LinearLayoutManager(this)
        notifications_recyclerView.layoutManager= LinearLayoutManager(this)
        val coroutineScope= CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO){
            val repo= Repository(this@Notifications)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@Notifications) {
                    if (it.status) menuCategoriesRecyclerView.adapter = MenuCategoryAdapter(it.data)
                    categoriesStatus = true
                    lottieHide()
                }
            }
        }
        val coroutineScope1= CoroutineScope(Dispatchers.IO)
        coroutineScope1.async(Dispatchers.IO){
            val repo= Repository(this@Notifications)
            repo.notification()
            runOnUiThread {
                repo.getNotificationData().observe(this@Notifications) {
                    if (it.status) {
                        notifications_recyclerView.adapter = NotificationAdapter(it.data as ArrayList<NotificationData>)
                        if (it.data.isEmpty()) notification_no.visibility = View.VISIBLE
                    } else notification_no.visibility = View.VISIBLE
                    notificationsStatus = true
                    lottieHide()
                }
            }
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
            val repo= Repository(this)
            repo.user()
            repo.getUserData().observe(this) {
                name.text = getString(R.string.menu_welcome) + it.data.name + ","
            }
        }else{
            name.text= getString(R.string.menu_welcome)
        }
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
    fun menu(view: View){ notifications_drawerLayout.openDrawer(Gravity.START) }

    private fun lottieHide(){
        if(categoriesStatus && notificationsStatus){
            lottie17.visibility= View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }
    }

    fun home(view: View){
        val intent= Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }

    fun favorites(view: View){
        val intent= Intent(this, Favorites::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }

    fun newAd(view: View){
        val intent= Intent(this, NewAd::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }

    fun notifications(view: View){
        val intent= Intent(this, Notifications::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
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