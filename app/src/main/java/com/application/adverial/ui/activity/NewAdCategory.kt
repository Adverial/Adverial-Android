package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityNewAdCategoryBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.SubCategory
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NewAdCategoryAdapter
import com.application.adverial.ui.navigation.Favorites
import com.application.adverial.ui.navigation.Home
import com.application.adverial.ui.navigation.Notifications
import com.application.adverial.ui.navigation.Profile

class NewAdCategory : AppCompatActivity() {

    private lateinit var binding: ActivityNewAdCategoryBinding
    private var id = ""
    private var name = ""
    private var position = 0
    private var type = ""
    private var data = ArrayList<SubCategory>()
    private var subData = ArrayList<SubCategory>()
    private var nameArray = ArrayList<String>()
    private var positionArray = ArrayList<Int>()
    private var idArray = ArrayList<String>()
    private lateinit var adapter: NewAdCategoryAdapter

    override fun onBackPressed() {
        if (idArray.size > 1) {
            idArray.removeAt(idArray.size - 1)
            nameArray.removeAt(nameArray.size - 1)
            positionArray.removeAt(positionArray.size - 1)
            updateCheck()
        } else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.newAdCategoryBack)
        Tools().setBasedLogo(this, R.id.imageView12)
        Tools().changeViewFromTheme(this, binding.newAddCategoryRoot)

        pageInit()
    }

    private fun pageInit() {
        binding.lottie4.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        id = intent.getStringExtra("id") ?: ""
        name = intent.getStringExtra("name") ?: ""
        position = intent.getIntExtra("position", 0)
        binding.subCategoryName.text = name
        binding.newAdCategoryRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NewAdCategoryAdapter(subData)
        binding.newAdCategoryRecyclerView.adapter = adapter

        val repo = Repository(this)
        repo.mainCategory()
        repo.getMainCategoryData().observe(this) {
            data = it.data as ArrayList<SubCategory>
            idArray.add(id)
            nameArray.add(name)
            positionArray.add(position)
            updateCheck()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCheck() {
        binding.lottie4.visibility = View.GONE
        Tools().viewEnable(window.decorView.rootView, true)

        when (positionArray.size) {
            1 -> loadSubData(positionArray[0])
            2 -> loadSubData(positionArray[0], positionArray[1])
            3 -> loadSubData(positionArray[0], positionArray[1], positionArray[2])
            4 -> loadSubData(positionArray[0], positionArray[1], positionArray[2], positionArray[3])
            5 -> loadSubData(positionArray[0], positionArray[1], positionArray[2], positionArray[3], positionArray[4])
            6 -> loadSubData(positionArray[0], positionArray[1], positionArray[2], positionArray[3], positionArray[4], positionArray[5])
            7 -> loadSubData(positionArray[0], positionArray[1], positionArray[2], positionArray[3], positionArray[4], positionArray[5], positionArray[6])
            8 -> loadSubData(positionArray[0], positionArray[1], positionArray[2], positionArray[3], positionArray[4], positionArray[5], positionArray[6], positionArray[7])
            9 -> loadSubData(positionArray[0], positionArray[1], positionArray[2], positionArray[3], positionArray[4], positionArray[5], positionArray[6], positionArray[7], positionArray[8])
            10 -> loadSubData(positionArray[0], positionArray[1], positionArray[2], positionArray[3], positionArray[4], positionArray[5], positionArray[6], positionArray[7], positionArray[8], positionArray[9])
        }
    }

    private fun loadSubData(vararg positions: Int) {
        var subCategoryList = data
        for (position in positions) {
            subCategoryList = subCategoryList[position].parent_categories as ArrayList<SubCategory>
        }
        if (subCategoryList.isNotEmpty()) {
            subData = subCategoryList
            update()
        } else {
            nextPage()
        }
    }

    private fun renameTitle() {
        binding.subCategoryName.text = nameArray.joinToString(" > ")
    }

    private fun nextPage() {
        if (Tools().authCheck(this)) {
            val idString = idArray.joinToString(",")
            val intent = Intent(this, NewAdInfo()::class.java).apply {
                putExtra("type", type)
                putExtra("idArray", idString)
            }
            startActivity(intent)
        } else {
            startActivity(Intent(this, LoginWa::class.java))
        }

        if (idArray.isNotEmpty()) {
            idArray.removeAt(idArray.size - 1)
            nameArray.removeAt(nameArray.size - 1)
            positionArray.removeAt(positionArray.size - 1)
            updateCheck()
        }
    }

    private fun update() {
        adapter = NewAdCategoryAdapter(subData)
        binding.newAdCategoryRecyclerView.adapter = adapter
        renameTitle()
        adapter.getResult().observe(this) {
            nameArray.add(it.name)
            idArray.add(it.id)
            positionArray.add(it.position)
            type = it.type
            updateCheck()
        }
    }

    fun back(view: View) {
        if (idArray.size > 1) {
            idArray.removeAt(idArray.size - 1)
            nameArray.removeAt(nameArray.size - 1)
            positionArray.removeAt(positionArray.size - 1)
            updateCheck()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") View.LAYOUT_DIRECTION_LTR
            else View.LAYOUT_DIRECTION_RTL
    }
    fun home(view: View) {
        startActivity(Intent(this, Home::class.java))
        finish()
    }

    fun favorites(view: View) {
        startActivity(Intent(this, Favorites::class.java))
        finish()
    }

    fun newAd(view: View) {
        // Already in NewAd activity, no need to navigate
    }

    fun notifications(view: View) {
        startActivity(Intent(this, Notifications::class.java))
        finish()
    }

    fun profile(view: View) {
        startActivity(Intent(this, Profile::class.java))
        finish()
    }
}
