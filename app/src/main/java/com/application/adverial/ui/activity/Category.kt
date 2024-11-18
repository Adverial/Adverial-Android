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
import com.application.adverial.ui.adapter.CategoryAdapter

class Category : AppCompatActivity() {

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
    private var typeArray = ArrayList<String>()
    private lateinit var adapter: CategoryAdapter

    override fun onBackPressed() {
        if (idArray.size > 1) {
            idArray.removeAt(idArray.size - 1)
            nameArray.removeAt(nameArray.size - 1)
            positionArray.removeAt(positionArray.size - 1)
            typeArray.removeAt(typeArray.size - 1)
            updateCheck()
        } else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().setBasedLogo(this, R.id.imageView12)
        Tools().changeViewFromTheme(this, binding.newAddCategoryRoot)
        Tools().rotateLayout(this, binding.newAdCategoryBack)

        pageInit()
    }

    private fun pageInit() {
        binding.lottie4.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        id = intent.getStringExtra("id")!!
        name = intent.getStringExtra("name")!!
        position = intent.getIntExtra("position", 0)
        binding.subCategoryName.text = name
        binding.newAdCategoryRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(subData)
        binding.newAdCategoryRecyclerView.adapter = adapter

        val repo = Repository(this)
        repo.mainCategory()
        repo.getMainCategoryData().observe(this) {
            data = it.data as ArrayList<SubCategory>
            idArray.add(id)
            nameArray.add(name)
            typeArray.add(type)
            positionArray.add(position)
            updateCheck()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCheck() {
        binding.lottie4.visibility = View.GONE
        Tools().viewEnable(window.decorView.rootView, true)

        when (positionArray.size) {
            1 -> handleCategoryLevel(0)
            2 -> handleCategoryLevel(1)
            3 -> handleCategoryLevel(2)
            4 -> handleCategoryLevel(3)
            5 -> handleCategoryLevel(4)
            6 -> handleCategoryLevel(5)
            7 -> handleCategoryLevel(6)
            8 -> handleCategoryLevel(7)
            9 -> handleCategoryLevel(8)
            10 -> handleCategoryLevel(9)
        }
    }

    private fun handleCategoryLevel(level: Int) {
        try {
            val currentCategory = getCategory(level)
            if (currentCategory.parent_categories.isNotEmpty()) {
                subData = currentCategory.parent_categories as ArrayList<SubCategory>
                update()
            } else if (currentCategory.total_ad_count != "0") {
                nextPage()
            } else {
                removeLastEntry()
                updateCheck()
            }
        } catch (e: Exception) {
            nextPage()
        }
    }

    private fun getCategory(level: Int): SubCategory {
        var category = data[positionArray[0]]
        for (i in 1..level) {
            category = category.parent_categories[positionArray[i]]
        }
        return category
    }

    private fun removeLastEntry() {
        idArray.removeAt(idArray.size - 1)
        nameArray.removeAt(nameArray.size - 1)
        typeArray.removeAt(typeArray.size - 1)
        positionArray.removeAt(positionArray.size - 1)
    }

    private fun renameTitle() {
        binding.subCategoryName.text = ""
        for (i in nameArray.indices) {
            binding.subCategoryName.append(nameArray[i])
            if (i < nameArray.size - 1) {
                binding.subCategoryName.append(" > ")
            }
        }
    }

    private fun nextPage() {
        val intent = Intent(this, CategoryResult::class.java).apply {
            putExtra("id", idArray[idArray.size - 1])
            putExtra("name", "$name > ${nameArray[nameArray.size - 1]}")
            putExtra("type", typeArray[typeArray.size - 1])
        }
        startActivity(intent)
        removeLastEntry()
        updateCheck()
    }

    private fun update() {
        adapter = CategoryAdapter(subData)
        binding.newAdCategoryRecyclerView.adapter = adapter
        renameTitle()
        adapter.getResult().observe(this) {
            nameArray.add(it.name)
            typeArray.add(it.type)
            idArray.add(it.id)
            positionArray.add(it.position)
            type = it.type
            updateCheck()
        }
    }

    fun back(view: View) {
        if (idArray.size > 1) {
            removeLastEntry()
            updateCheck()
        } else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection = if (language == "" || language == "0" || language == "1") {
            View.LAYOUT_DIRECTION_LTR
        } else {
            View.LAYOUT_DIRECTION_RTL
        }
    }
}
