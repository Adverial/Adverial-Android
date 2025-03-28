package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityNewAdInfoBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.CategoryOptionsData
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NewAdInfoAdapter

class NewAdInfo : AppCompatActivity() {

    private lateinit var binding: ActivityNewAdInfoBinding
    private var type = ""
    private var idArray = ""
    private var itemList = ArrayList<CategoryOptionsData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.newAdCategoryBack3)
        Tools().changeViewFromTheme(this, binding.newAdInfoRoot)
        Tools().setBasedLogo(this, R.id.imageView24)

        pageInit()
    }

    private fun pageInit() {
        binding.lottie11.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        clearPreviousData()

        type = intent.getStringExtra("type") ?: ""
       // android.util.Log.d("TypeDebug", "Type in new ad info: $type")

//        if (type.isEmpty()) {
//           // android.util.Log.e("TypeDebug", "Error: No type received in NewAdInfo")
//        }

        idArray = intent.getStringExtra("idArray") ?: ""

        binding.newAdInfoRecyclerView.layoutManager = LinearLayoutManager(this)
        val repo = Repository(this)

        //android.util.Log.d("TypeDebug", "Making categoryOptions call with type: $type")

        repo.categoryOptions(type)
        repo.getCategoryOptionsData().observe(this) { response ->
           // android.util.Log.d("TypeDebug", "Received categoryOptions response for type: $type")

            itemList.add(
                    CategoryOptionsData(1, "", getString(R.string.new_ad_info_title), "", listOf())
            )
            itemList.add(
                    CategoryOptionsData(1, "", getString(R.string.new_ad_info_price), "", listOf())
            )
            itemList.add(
                    CategoryOptionsData(
                            1,
                            "",
                            getString(R.string.new_ad_info_currency),
                            "",
                            listOf()
                    )
            )
            itemList.addAll(response.data)
            itemList.add(
                    CategoryOptionsData(
                            1,
                            "",
                            getString(R.string.new_ad_info_description),
                            "",
                            listOf()
                    )
            )

            val adapter = NewAdInfoAdapter(itemList)
            binding.newAdInfoRecyclerView.adapter = adapter

            adapter.getResult().observe(this) { result -> handleAdapterResult(result) }

            binding.newAdInfoRecyclerView.setHasFixedSize(true)
            binding.newAdInfoRecyclerView.setItemViewCacheSize(itemList.size)

            binding.lottie11.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }
    }

    private fun clearPreviousData() {
        getSharedPreferences("newAdImages", 0).edit().clear().apply()
        getSharedPreferences("newAd", 0).edit().clear().apply()
        getSharedPreferences("newAdOptions", 0).edit().clear().apply()
    }

    private fun handleAdapterResult(result: String) {
        when (result) {
            "show" -> {
                binding.lottie11.visibility = View.VISIBLE
                Tools().viewEnable(window.decorView.rootView, false)
            }
            "hide" -> {
                binding.lottie11.visibility = View.GONE
                Tools().viewEnable(window.decorView.rootView, true)
            }
        }
    }

    fun next(view: View) {
        val data = getSharedPreferences("newAd", 0)

        val title = data.getString("title", "").orEmpty()
        val price = data.getString("price", "").orEmpty()
        val currency = data.getString("currency", "").orEmpty()

        when {
            title.isBlank() -> {
                Toast.makeText(this, getString(R.string.enterTitle), Toast.LENGTH_SHORT).show()
            }
            price.isBlank() -> {
                Toast.makeText(this, getString(R.string.enterPrice), Toast.LENGTH_SHORT).show()
            }
            currency.isBlank() -> {
                Toast.makeText(this, getString(R.string.enterCurrency), Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding.lottie11.visibility = View.VISIBLE
                Tools().viewEnable(window.decorView.rootView, false)
                val repo = Repository(this)
                repo.addAdInfo(
                        title,
                        price,
                        data.getString("description", "").orEmpty(),
                        idArray,
                        currency
                )
                repo.getAddAdInfoData().observe(this) { response ->
                    binding.lottie11.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    if (response.status) {
                        val intent = Intent(this, NewAdAddress::class.java)
                        intent.putExtra("adId", response.data.ad_id.toString())
                        startActivity(intent)
                    }
                }
            }
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
                if (language.isNullOrEmpty() || language == "0" || language == "1")
                        View.LAYOUT_DIRECTION_LTR
                else View.LAYOUT_DIRECTION_RTL
    }
}
