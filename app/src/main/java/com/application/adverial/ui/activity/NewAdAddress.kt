package com.application.adverial.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.application.adverial.R
import com.application.adverial.databinding.ActivityAddressBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.dialog.DropList
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class NewAdAddress : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private val itemList = ArrayList<com.application.adverial.ui.model.DropList>()
    private var country = ""
    private var city = ""
    private var district = ""
    private var adId = ""
    private var nextPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().setBasedLogo(this, R.id.imageView32)
        Tools().rotateLayout(this, binding.homeMenu3)
        Tools().changeViewFromTheme(this, binding.addressRoot)

        pageInit()
    }

    private fun pageInit() {
        Tools().rotateLayout(this, binding.arrow1)
        Tools().rotateLayout(this, binding.arrow2)
        Tools().rotateLayout(this, binding.arrow3)

        adId = intent.getStringExtra("adId") ?: ""

        binding.newAdAddressCountrySelect.setOnClickListener {
            fetchCountryData()
        }

        binding.newAdAddressCitySelect.setOnClickListener {
            if (country.isNotBlank()) {
                fetchCityData()
            }
        }

        binding.newAdAddressDistrictSelect.setOnClickListener {
            if (city.isNotBlank()) {
                fetchDistrictData()
            }
        }
    }

    private fun fetchCountryData() {
        binding.lottie9.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        val repo = Repository(this)
        repo.country()
        repo.getCountryData().observe(this) {
            binding.lottie9.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
            itemList.clear()
            it.data.forEach { country ->
                itemList.add(com.application.adverial.ui.model.DropList(country.name, country.id.toString()))
            }
            val dialog = DropList(itemList, getString(R.string.address_country))
            dialog.show(supportFragmentManager, "DropList")
            dialog.getStatus().observe(this) { result ->
                binding.newAdAddressCountry.text = result.name
                country = result.id
                city = ""
                district = ""
                resetFields()
            }
        }
    }

    private fun fetchCityData() {
        val repo = Repository(this)
        binding.lottie9.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        repo.city(country)
        repo.getCityData().observe(this) {
            binding.lottie9.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
            itemList.clear()
            it.data.forEach { city ->
                itemList.add(com.application.adverial.ui.model.DropList(city.name, city.id.toString()))
            }
            val dialog = DropList(itemList, getString(R.string.address_province))
            dialog.show(supportFragmentManager, "DropList")
            dialog.getStatus().observe(this) { result ->
                binding.newAdAddressCity.text = result.name
                city = result.id
                district = ""
                resetDistrictField()
            }
        }
    }

    private fun fetchDistrictData() {
        val repo = Repository(this)
        binding.lottie9.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)
        repo.district(city)
        repo.getDistrictData().observe(this) {
            binding.lottie9.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
            itemList.clear()
            it.data.forEach { district ->
                itemList.add(com.application.adverial.ui.model.DropList(district.name, district.id.toString()))
            }
            val dialog = DropList(itemList, getString(R.string.address_district))
            dialog.show(supportFragmentManager, "DropList")
            dialog.getStatus().observe(this) { result ->
                binding.newAdAddressDistrict.text = result.name
                district = result.id
                binding.newAdAddressDistrict.setTextColor(ContextCompat.getColor(this, R.color.gray))
            }
        }
    }

    private fun resetFields() {
        binding.newAdAddressDistrict.setTextColor(ContextCompat.getColor(this, R.color.gray))
        binding.newAdAddressCity.setTextColor(ContextCompat.getColor(this, R.color.gray))
        binding.newAdAddressDistrict.text = getString(R.string.new_ad_info_select)
        binding.newAdAddressCity.text = getString(R.string.new_ad_info_select)
        binding.newAdAddressCountry.setTextColor(ContextCompat.getColor(this, R.color.gray))
    }

    private fun resetDistrictField() {
        binding.newAdAddressDistrict.setTextColor(ContextCompat.getColor(this, R.color.gray))
        binding.newAdAddressDistrict.text = getString(R.string.new_ad_info_select)
        binding.newAdAddressCity.setTextColor(ContextCompat.getColor(this, R.color.gray))
    }

    fun next(view: View) {
        if (country.isNotBlank() && city.isNotBlank() && district.isNotBlank()) {
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    if (nextPage) {
                        nextPage = false
                        val intent = Intent(this@NewAdAddress, NewAdMap::class.java)
                        intent.putExtra("adId", adId)
                        intent.putExtra("country", country)
                        intent.putExtra("city", city)
                        intent.putExtra("district", district)
                        startActivity(intent)
                    }
                }

                override fun onPermissionDenied(deniedPermissions: List<String?>) {
                    Toast.makeText(this@NewAdAddress, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
        } else {
            Toast.makeText(this, getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        nextPage = true
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") {
                View.LAYOUT_DIRECTION_LTR
            } else {
                View.LAYOUT_DIRECTION_RTL
            }
    }
}
