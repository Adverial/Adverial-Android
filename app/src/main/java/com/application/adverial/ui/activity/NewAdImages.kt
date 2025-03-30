// NewAdImages.kt
package com.application.adverial.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityNewAdImagesBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.ImageItem
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NewAdImagesAdapter
import com.application.adverial.ui.dialog.NewAdCompletedDialog
import com.wookweb.lezzetapp.service.ItemDecorations

class NewAdImages : AppCompatActivity() {

    private lateinit var binding: ActivityNewAdImagesBinding
    private val images = ArrayList<ImageItem>()
    private var adId = ""
    private lateinit var adapter: NewAdImagesAdapter
    private val PICK_MEDIA_REQUEST_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101

    // Flag to prevent TextWatcher from processing during initial setup
    private var isSettingInitialPhoneNumber = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().rotateLayout(this, binding.homeMenu3)
        Tools().changeViewFromTheme(this, binding.newAdImageRoot)
        Tools().setBasedLogo(this, R.id.imageView32)

        checkPermissions()
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
                    // Format and set the number directly without triggering TextWatcher
                    val phoneNumber = it.data.whatsappNumber
                    if (phoneNumber != null) {
                        if (phoneNumber.isNotEmpty()) {
                            isSettingInitialPhoneNumber = true
                            binding.publishPhone.setText(phoneNumber)
                            isSettingInitialPhoneNumber = false
                        }
                    }
                    binding.publishName.setText(it.data.name)
                }
            }
        }
    }

    private fun setupPhoneFormatting() {
        binding.publishPhone.addTextChangedListener(
                object : TextWatcher {
                    private var isFormatting: Boolean = false
                    private var previousText: String = ""

                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                        if (isFormatting || isSettingInitialPhoneNumber) return
                        previousText = s.toString()
                    }

                    override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                    ) {
                        if (isFormatting || isSettingInitialPhoneNumber) return
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (isFormatting || isSettingInitialPhoneNumber) return

                        val currentText = s.toString()
                        if (currentText == previousText) return

                        isFormatting = true

                        val formattedText = formatPhoneNumber(currentText)
                        binding.publishPhone.setText(formattedText)

                        // Add safety check to prevent IndexOutOfBoundsException
                        val selection =
                                minOf(formattedText.length, binding.publishPhone.text.length)
                        if (selection > 0) {
                            binding.publishPhone.setSelection(selection)
                        }

                        isFormatting = false
                    }
                }
        )
    }

    private fun formatPhoneNumber(phone: String): String {
        // Keep the plus sign if it exists
        val hasPlus = phone.startsWith("+")

        // Remove non-digit characters except the leading plus sign
        val digitsOnly =
                if (hasPlus) {
                    "+" + phone.substring(1).replace("\\D".toRegex(), "")
                } else {
                    phone.replace("\\D".toRegex(), "")
                }

        // Don't format if too short
        if (digitsOnly.length < 4) return digitsOnly

        val builder = StringBuilder()

        if (hasPlus) {
            // Handle international format with country code
            builder.append("+")

            // Add country code (assuming first 2-3 digits after + are country code)
            val countryCodeEnd = minOf(4, digitsOnly.length - 1)
            builder.append(digitsOnly.substring(1, countryCodeEnd))

            // Add space after country code
            if (digitsOnly.length > countryCodeEnd) {
                builder.append(" ")
            }

            // Add the rest of the number with spaces
            for (i in countryCodeEnd until digitsOnly.length) {
                if (i > countryCodeEnd && (i - countryCodeEnd) % 4 == 0) {
                    builder.append(" ")
                }
                builder.append(digitsOnly[i])
            }
        } else {
            // Regular formatting for numbers without country code
            for (i in digitsOnly.indices) {
                builder.append(digitsOnly[i])
                if (i == 3 || i == 6) builder.append(" ")
            }
        }

        return builder.toString()
    }

    private fun pageInit() {
        adId = intent.getStringExtra("adId") ?: ""
        binding.newAdImagesRecyclerView.layoutManager =
                GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        binding.newAdImagesRecyclerView.addItemDecoration(ItemDecorations(this).images(images.size))

        adapter = NewAdImagesAdapter(images, adId)
        binding.newAdImagesRecyclerView.adapter = adapter
        binding.newAdImagesRecyclerView.setHasFixedSize(true)

        adapter.getResult().observe(this) { result ->
            when (result) {
                "loading" -> {
                    binding.lottie10.visibility = View.VISIBLE
                    Tools().viewEnable(window.decorView.rootView, false)
                }
                "refresh" -> {
                    binding.lottie10.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    // Handle refresh if needed
                }
                "error" -> {
                    binding.lottie10.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun openGalleryForMedia() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(
                Intent.createChooser(intent, "getString(R.string.select_media)"),
                PICK_MEDIA_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_MEDIA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uriList = ArrayList<Uri>()
            data?.let {
                val clipData = it.clipData
                if (clipData != null) {
                    // Multiple items selected
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        uriList.add(item.uri)
                    }
                } else {
                    // Single item selected
                    it.data?.let { uri -> uriList.add(uri) }
                }
                adapter.addImages(uriList)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun next(view: View) {
        val name = binding.publishName.text.toString()
        val phone = binding.publishPhone.text.toString().replace(" ", "")

        // Check if phone is valid (either 11 digits local format or international format with + and
        // 10+ digits)
        val isPhoneValid =
                if (phone.startsWith("+")) {
                    // For international format, check if there are at least 10 digits after the +
                    // sign
                    phone.substring(1).replace("\\D".toRegex(), "").length >= 10
                } else {
                    // For local format, still check for 11 digits
                    phone.length == 11
                }

        if (name.isNotBlank() && isPhoneValid) {
            if (images.isNotEmpty()) {
                binding.lottie10.visibility = View.VISIBLE
                Tools().viewEnable(window.decorView.rootView, false)

                adapter.uploadToServer()
                adapter.getResult().observe(this) { result ->
                    when (result) {
                        "refresh" -> {
                            val filePaths = adapter.getFilePaths()
                            publishAd(filePaths)
                        }
                        "error" -> {
                            binding.lottie10.visibility = View.GONE
                            Tools().viewEnable(window.decorView.rootView, true)
                            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.uploadImage), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun publishAd(filePaths: ArrayList<String>) {
        val type = if (binding.publishRadio1.isChecked) "1" else "3"
        val repo = Repository(this)
        repo.publishAd(
                adId,
                binding.publishPhone.text.toString().replace(" ", ""),
                binding.publishName.text.toString(),
                type,
                filePaths
        )
        repo.getPublishAdData().observe(this) { response ->
            binding.lottie10.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
            if (response.status) {
                val dialog = NewAdCompletedDialog.newInstance(response.data.ad_no.toString())
                dialog.show(supportFragmentManager, "NewAdCompletedDialog")
            } else {
                Toast.makeText(
                                this,
                                " getString(R.string.failed_to_publish_ad)",
                                Toast.LENGTH_SHORT
                        )
                        .show()
            }
        }
    }

    private fun checkPermissions() {
        val permissions =
                arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        if (!permissions.all {
                    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                }
        ) {
            ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_CODE)
        } else {
            // Permissions already granted
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted
            } else {
                Toast.makeText(this, "getString(R.string.permissions_required)", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
