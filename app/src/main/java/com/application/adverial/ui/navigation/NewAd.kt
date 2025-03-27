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
import com.application.adverial.databinding.ActivityNewAdBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.LoginWa
import com.application.adverial.ui.adapter.MenuCategoryAdapter
import com.application.adverial.ui.adapter.NewAdAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.Locale
import java.util.Objects

class NewAd : AppCompatActivity() {

    private lateinit var binding: ActivityNewAdBinding
    private lateinit var add: LinearLayout
    private lateinit var profile: LinearLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView
    private var itemList = ArrayList<com.application.adverial.remote.model.SubCategory>()
    private var filter = ArrayList<com.application.adverial.remote.model.SubCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.newAddRoot)
        Tools().setBasedLogo(this, binding.imageView12.id)
        drawerInit()
        pageInit()
    }

    private fun pageInit() {
        binding.lottie3.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        // Setting up RecyclerViews
        menuCategoriesRecyclerView = binding.navMenu.getHeaderView(0).findViewById(R.id.menu_recyclerView)
        menuCategoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.newAdRecyclerView.layoutManager = LinearLayoutManager(this)

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.async(Dispatchers.IO) {
            val repo = Repository(this@NewAd)
            repo.mainCategory()
            runOnUiThread {
                repo.getMainCategoryData().observe(this@NewAd) {
                    if (it.status) {
                        menuCategoriesRecyclerView.adapter = MenuCategoryAdapter(it.data)
                        itemList = it.data as ArrayList<com.application.adverial.remote.model.SubCategory>
                        binding.newAdRecyclerView.adapter = NewAdAdapter(itemList, itemList)
                    }
                    binding.lottie3.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                }
            }
        }

        // Search Functionality
        binding.homeSearch2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.homeSearch2.text.isNotBlank()) {
                    filter.clear()
                    for (item in itemList) {
                        if (item.name?.lowercase(Locale.getDefault())?.contains(
                                binding.homeSearch2.text.toString().lowercase(Locale.getDefault())
                            ) == true
                        ) {
                            filter.add(item)
                        }
                    }
                    binding.newAdRecyclerView.adapter = NewAdAdapter(filter, itemList)
                } else {
                    binding.newAdRecyclerView.adapter = NewAdAdapter(itemList, itemList)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Voice Search
        binding.newAdVoice.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        val lang = getSharedPreferences("user", 0).getString("languageName", "English")
        when (lang) {
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
//            repo.user().observe(this) { userData ->
//                name.text = "Welcome ${userData?.data?.name ?: ""},"
//            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.homeSearch2.setText(result?.get(0) ?: "")
        }
    }

    @SuppressLint("WrongConstant")
    public fun menu(view: View) {
        binding.newAdDrawerLayout.openDrawer(Gravity.START)
    }

    public fun home(view: View) {
        startActivity(Intent(this, Home::class.java))
        finish()
    }

    public fun favorites(view: View) {
        startActivity(Intent(this, Favorites::class.java))
        finish()
    }

    public fun newAd(view: View) {
        // Already in NewAd activity, no need to navigate
    }

    public fun notifications(view: View) {
        startActivity(Intent(this, Notifications::class.java))
        finish()
    }

    public fun profile(view: View) {
        startActivity(Intent(this, Profile::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" || language == "0" || language == "1") window.decorView.layoutDirection =
            View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
