package com.application.adverial.ui.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.Login
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.application.adverial.ui.adapter.NewAdAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_new_ad.home_search2
import kotlinx.android.synthetic.main.activity_new_ad.lottie3
import kotlinx.android.synthetic.main.activity_new_ad.newAd_drawerLayout
import kotlinx.android.synthetic.main.activity_new_ad.newAd_recyclerView
import kotlinx.android.synthetic.main.activity_new_ad.newAd_voice
import kotlinx.android.synthetic.main.activity_new_ad.newAddRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.Locale
import java.util.Objects

class NewAd : AppCompatActivity() {

    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView
    private var itemList= ArrayList<com.application.adverial.remote.model.SubCategory>()
    private var filter= ArrayList<com.application.adverial.remote.model.SubCategory>()

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad)
        Tools().changeViewFromTheme(this,newAddRoot)
        if (Tools().getTheme(this)=="dark") {
           // findViewById<View>(R.id.home_menu2).setBackgroundResource(R.drawable.im_menu)
            findViewById<View>(R.id.imageView12).setBackgroundResource(R.drawable.test1)
        }
        else
        {
            //findViewById<View>(R.id.home_menu2).setBackgroundResource(R.drawable.im_menu_dark)
            findViewById<View>(R.id.imageView12).setBackgroundResource(R.drawable.logo_dark)
        }
        //AutoComplete(this, recyclerView, home_search2, "category")
        drawerInit()
        pageInit()
    }

    private fun pageInit(){
        lottie3.visibility= View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        menuCategoriesRecyclerView.layoutManager= LinearLayoutManager(this)
        newAd_recyclerView.layoutManager= LinearLayoutManager(this)
        val coroutineScope= CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO){
            val repo= Repository(this@NewAd)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@NewAd) {
                    if (it.status){
                        menuCategoriesRecyclerView.adapter = MenuCategoryAdapter(it.data)
                        itemList= it.data as ArrayList<com.application.adverial.remote.model.SubCategory>
                        newAd_recyclerView.adapter= NewAdAdapter(itemList, itemList)
                    }
                    lottie3.visibility= View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                }
            }
        }
        home_search2.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(home_search2.text.isNotBlank()){
                    filter.clear()
                    for(i in itemList.indices){
                        if(itemList[i].name!!.lowercase(Locale.getDefault()).contains(home_search2.text.toString().lowercase(
                                Locale.getDefault()))){
                            filter.add(itemList[i])
                        }
                    }
                    newAd_recyclerView.adapter= NewAdAdapter(filter, itemList)
                }else{
                    newAd_recyclerView.adapter= NewAdAdapter(itemList, itemList)
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        newAd_voice.setOnClickListener{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            val lang= getSharedPreferences("user", 0)
            when(lang.getString("languageName", "English")){
                "English" -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
                "Germany" -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de_DE")
                "Türkçe" -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr")
                "عربی" -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
                else -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
            }
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
            try {
                startActivityForResult(intent, 10)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.voiceError), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                home_search2.setText(Objects.requireNonNull(result)?.get(0))
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
    fun menu(view: View){ newAd_drawerLayout.openDrawer(Gravity.START) }

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
            val intent= Intent(this, Notifications::class.java)
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