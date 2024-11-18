package com.application.adverial.ui.dialog

import android.Manifest
import android.app.Activity
import android.content.*
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
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
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine

class SearchDialog : DialogFragment() {

    private lateinit var mBlurEngine: BlurDialogEngine
    private lateinit var binding: DialogSearchBinding
    private lateinit var anim: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchBinding.inflate(inflater, container, false)
        setupBlurEngine()
        setupDialogWindow()
        uiSetup()
        return binding.root
    }

    private fun setupBlurEngine() {
        mBlurEngine = BlurDialogEngine(requireActivity())
        mBlurEngine.setBlurRadius(10)
        mBlurEngine.setDownScaleFactor(10f)
        mBlurEngine.setBlurActionBar(true)
        mBlurEngine.setUseRenderScript(true)
    }

    private fun setupDialogWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val wlp: WindowManager.LayoutParams = attributes
            wlp.gravity = Gravity.TOP
            attributes = wlp
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(Tools().displayWidth(), ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun uiSetup() {
        anim = binding.lottie16
        val imgr = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        AutoComplete(requireContext(), binding.recyclerView, binding.searchText, "ad")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("search"))

        binding.searchText.requestFocus()

        // Handle search button click
        binding.searchSearch.setOnClickListener {
            if (binding.searchText.text.isNotBlank()) {
                search()
            }
        }

        // Handle clear search history
        binding.searchClean.paintFlags = binding.searchClean.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.searchClean.setOnClickListener { clearSearchHistory() }

        // Handle QR code scan
        binding.searchQr.setOnClickListener { openQrScanner() }

        // Handle back button
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
        if (Tools().authCheck(requireContext())) {
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
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
// Correct way to set the text to EditText
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

    override fun onResume() {
        super.onResume()
        mBlurEngine.onResume(retainInstance)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBlurEngine.onDetach()
    }
}
