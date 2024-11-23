package com.application.adverial.ui.dialog

import android.Manifest
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.DialogSearchBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.LatestSearchData
import com.application.adverial.service.AutoComplete
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.QrCode
import com.application.adverial.ui.activity.SearchResult
import com.application.adverial.ui.adapter.SearchAdapter
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.BlurTransformation

class SearchDialog : DialogFragment() {

    private lateinit var binding: DialogSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchBinding.inflate(inflater, container, false)
        setupDialogWindow()
        applyBlurEffectToBackground()
        uiSetup()
        return binding.root
    }

    private fun setupDialogWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val wlp: WindowManager.LayoutParams = attributes
            wlp.gravity = Gravity.TOP
            attributes = wlp
        }
    }

    private fun applyBlurEffectToBackground() {
        dialog?.window?.decorView?.let { decorView ->
            val rootView = decorView.rootView
            rootView.isDrawingCacheEnabled = true
            val bitmap = rootView.drawingCache

            if (bitmap != null) {
                val blurredBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    applyRenderEffectBlur(bitmap, 10f) // RenderEffect for API 31+
                } else {
                    applyFallbackBlur(bitmap) // Glide fallback for older devices
                }
                dialog?.window?.setBackgroundDrawable(BitmapDrawable(resources, blurredBitmap))
            }
            rootView.isDrawingCacheEnabled = false
        }
    }

    private fun applyRenderEffectBlur(bitmap: Bitmap, radius: Float): Bitmap {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            throw UnsupportedOperationException("RenderEffect is only available on API 31+")
        }

        val renderEffect = android.graphics.RenderEffect.createBlurEffect(radius, radius, android.graphics.Shader.TileMode.CLAMP)
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = android.graphics.Canvas(outputBitmap)
        val paint = android.graphics.Paint().apply {
            setRenderEffect(renderEffect)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return outputBitmap
    }

    private fun setRenderEffect(renderEffect: RenderEffect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.root.setRenderEffect(renderEffect)
        }


    }

    private fun applyFallbackBlur(bitmap: Bitmap): Bitmap {
        return Glide.with(requireContext())
            .asBitmap()
            .load(bitmap)
            .transform(BlurTransformation(10))
            .submit(bitmap.width, bitmap.height)
            .get()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(Tools().displayWidth(), ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun uiSetup() {
        val imgr = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        AutoComplete(requireContext(), binding.recyclerView, binding.searchText, "ad")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("search"))

        binding.searchText.requestFocus()

        binding.searchSearch.setOnClickListener {
            if (binding.searchText.text.isNotBlank()) {
                search()
            }
        }

        binding.searchClean.paintFlags = binding.searchClean.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.searchClean.setOnClickListener { clearSearchHistory() }

        binding.searchQr.setOnClickListener { openQrScanner() }

        binding.searchBack.setOnClickListener {
            imgr.hideSoftInputFromWindow(binding.root.windowToken, 0)
            dismiss()
        }

        setupLatestSearch()
        handleEnterKey()
    }

    private fun handleEnterKey() {
        binding.searchText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (binding.searchText.text.isNotBlank()) {
                    search()
                }
                true
            } else false
        }
    }

    private fun setupLatestSearch() {
        if (Tools().authCheck(requireContext())) {
            val repo = Repository(requireContext())
            repo.latestSearch()
            repo.getLatestSearchData().observe(viewLifecycleOwner) {
                if (it.status) {
                    val keywords = it.data as ArrayList<LatestSearchData>
                    keywords.reverse()
                    val adapter = SearchAdapter(keywords)
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerView.adapter = adapter
                    adapter.getResult().observe(viewLifecycleOwner) { result ->
                        if (result == "hideKeyboard") {
                            val imgr = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                            imgr.hideSoftInputFromWindow(binding.root.windowToken, 0)
                        }
                    }
                }
            }
        }
    }

    private fun clearSearchHistory() {
        val anim = binding.lottie16
        anim.visibility = View.VISIBLE
        Tools().viewEnable(binding.root, false)
        val repo = Repository(requireContext())
        repo.clearSearch()
        repo.getClearSearchData().observe(viewLifecycleOwner) { response ->
            if (response.status) {
                setupLatestSearch()
            }
            anim.visibility = View.GONE
            Tools().viewEnable(binding.root, true)
        }
    }

    private fun openQrScanner() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val imgr = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imgr.hideSoftInputFromWindow(binding.root.windowToken, 0)
                val intent = Intent(requireContext(), QrCode::class.java)
                startActivity(intent)
                dismiss()
            }

            override fun onPermissionDenied(deniedPermissions: List<String?>) {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .check()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            binding.searchText.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra("data"))
        }
    }

    private fun search() {
        val keyword = binding.searchText.text.toString()
        val imgr = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(binding.root.windowToken, 0)
        val intent = Intent(requireContext(), SearchResult::class.java)
        intent.putExtra("keyword", keyword)
        startActivity(intent)
    }
}