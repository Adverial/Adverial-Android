package com.application.adverial.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityNewAdImagesBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NewAdImagesAdapter
import com.application.adverial.ui.dialog.NewAdCompletedDialog
import com.application.adverial.ui.model.Image
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.wookweb.lezzetapp.service.ItemDecorations

class NewAdImages : AppCompatActivity() {

    private lateinit var binding: ActivityNewAdImagesBinding
    private val images = ArrayList<Image>()
    private var adId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.homeMenu3)
        Tools().changeViewFromTheme(this, binding.newAdImageRoot)
        Tools().setBasedLogo(this, R.id.imageView32)

        permissions()
        setDefaultUserDetails()
        setupPhoneFormatting()
        pageInit()
    }

    private fun setDefaultUserDetails() {
        if (Tools().authCheck(this)) {
            val repo = Repository(this)
            repo.user()
            repo.getUserData().observe(this) { userData ->
                userData?.let {
                    binding.publishPhone.setText(it.data.phone)
                    binding.publishName.setText(it.data.name)
                }
            }
        }
    }

    private fun setupPhoneFormatting() {
        binding.publishPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.publishPhone.text.toString().isNotEmpty()) {
                    binding.publishPhone.removeTextChangedListener(this)
                    val phoneText = formatPhoneNumber(binding.publishPhone.text.toString())
                    binding.publishPhone.setText(phoneText)
                    binding.publishPhone.addTextChangedListener(this)
                    binding.publishPhone.setSelection(phoneText.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun formatPhoneNumber(phone: String): String {
        var formattedPhone = phone.replace(" ", "")
        if (formattedPhone.length > 1) formattedPhone = "0" + formattedPhone.substring(1)
        var phoneText = ""
        for (i in formattedPhone.indices) {
            phoneText += formattedPhone[i]
            if (i == 3 || i == 6) phoneText += " "
        }
        return phoneText
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun pageInit() {
        adId = intent.getStringExtra("adId") ?: ""
        binding.newAdImagesRecyclerView.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        binding.newAdImagesRecyclerView.addItemDecoration(ItemDecorations(this).images(images.size))
        updateData()
    }

    private fun updateData() {
        images.clear()
        val imagesData = getSharedPreferences("newAdImages", 0)
        val allEntries: Map<String, *> = imagesData.all
        for ((key, value) in allEntries) {
            images.add(Image(value.toString(), key))
        }
        images.sortWith { lhs, rhs -> lhs.id.compareTo(rhs.id) }

        val adapter = NewAdImagesAdapter(images, adId)
        binding.newAdImagesRecyclerView.adapter = adapter
        binding.newAdImagesRecyclerView.setHasFixedSize(true)
        binding.newAdImagesRecyclerView.setItemViewCacheSize(images.size + 1)

        adapter.getResult().observe(this) { result ->
            when (result) {
                "loading" -> {
                    binding.lottie10.visibility = View.VISIBLE
                    Tools().viewEnable(window.decorView.rootView, false)
                }
                "refresh" -> {
                    binding.lottie10.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    updateData()
                }
            }
        }
    }

    fun next(view: View) {
        if (binding.publishName.text.isNotBlank() && binding.publishPhone.text.toString().replace(" ", "").length == 11) {
            if (images.isNotEmpty()) {
                binding.lottie10.visibility = View.VISIBLE
                Tools().viewEnable(window.decorView.rootView, false)
                val type = if (binding.publishRadio1.isChecked) "1" else "3"
                val repo = Repository(this)
                val adapter = binding.newAdImagesRecyclerView.adapter as NewAdImagesAdapter
                val filePaths = adapter.getFilePaths()
                repo.publishAd(adId, binding.publishPhone.text.toString().replace(" ", ""), binding.publishName.text.toString(), type, filePaths)
                repo.getPublishAdData().observe(this) { response ->
                    binding.lottie10.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    if (response.status) {
                        val dialog = NewAdCompletedDialog(response.data.ad_no.toString())
                        dialog.show(supportFragmentManager, "NewAdCompletedDialog")
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.uploadImage), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun permissions() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: List<String?>) {}
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).check()
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") View.LAYOUT_DIRECTION_LTR
            else View.LAYOUT_DIRECTION_RTL
    }
}
