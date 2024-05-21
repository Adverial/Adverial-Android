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
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.FavoriteData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.FavoriteAdapter
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_favorites.activityFavoritesRoot
import kotlinx.android.synthetic.main.activity_favorites.favorite_recyclerView
import kotlinx.android.synthetic.main.activity_favorites.favorites_drawerLayout
import kotlinx.android.synthetic.main.activity_favorites.favorites_no
import kotlinx.android.synthetic.main.activity_favorites.lottie2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class Favorites : AppCompatActivity() {

    private var itemList= ArrayList<FavoriteData>()
    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView
    private var favoriteStatus= false
    private var categoriesStatus= false
    private var lang = "en"

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        Tools().changeViewFromTheme(this,activityFavoritesRoot)
        if (Tools().getTheme(this)=="dark") {
//            findViewById<View>(R.id.profile_menu3).setBackgroundResource(R.drawable.im_menu)
            findViewById<View>(R.id.imageView51).setBackgroundResource(R.drawable.test1)
        }
        else
        {
//            findViewById<View>(R.id.profile_menu3).setBackgroundResource(R.drawable.im_menu_dark)
            findViewById<View>(R.id.imageView51).setBackgroundResource(R.drawable.logo_dark)
        }
        drawerInit()
        pageInit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun pageInit(){
        lottie2.visibility= View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        menuCategoriesRecyclerView.layoutManager= LinearLayoutManager(this)
        favorite_recyclerView.layoutManager= LinearLayoutManager(this)
        val coroutineScope= CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO){
            val repo= Repository(this@Favorites)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@Favorites) {
                    if (it.status) menuCategoriesRecyclerView.adapter = MenuCategoryAdapter(it.data)
                    categoriesStatus = true
                    lottieHide()
                }
            }
        }
        val coroutineScope1= CoroutineScope(Dispatchers.IO)
        coroutineScope1.async(Dispatchers.IO){
            val repo= Repository(this@Favorites)
            repo.favorite()
            runOnUiThread {
                repo.getFavoriteData().observe(this@Favorites) { favorites ->
                    favorites?.let {
                        if (it.status) {
                            itemList = it.data as ArrayList<FavoriteData>
                            itemList.reverse()
                            val adapter = FavoriteAdapter(itemList)
                            favorite_recyclerView.adapter = adapter
                            adapter.getResult().observe(this@Favorites) { position ->
                                itemList.removeAt(position)
                                adapter.notifyDataSetChanged()
                                if (itemList.isEmpty()) favorites_no.visibility = View.VISIBLE
                            }
                            if (itemList.isEmpty()) favorites_no.visibility = View.VISIBLE
                        } else {
                            favorites_no.visibility = View.VISIBLE
                        }
                        favoriteStatus = true
                        lottieHide()
                    } ?: run {
                        // Handle case when favorites is null
                        favorites_no.visibility = View.VISIBLE
                        favoriteStatus = true
                        lottieHide()
                    }
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
            repo.getUserData().observe(this) { userData ->
                userData?.let {
                    name.text = "Welcome " + it.data?.name + ","
                }
            }
        }else{
            name.text= "Welcome"
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
    fun menu(view: View){ favorites_drawerLayout.openDrawer(Gravity.START) }

    private fun lottieHide(){
        if(categoriesStatus && favoriteStatus){
            lottie2.visibility= View.GONE
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