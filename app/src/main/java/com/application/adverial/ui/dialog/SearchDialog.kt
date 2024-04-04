package com.application.adverial.ui.dialog

import android.Manifest
import android.app.Activity
import android.content.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment

import com.application.adverial.R
import com.application.adverial.service.Tools
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.inputmethod.InputMethodManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.LatestSearchData
import com.application.adverial.service.AutoComplete
import com.application.adverial.ui.activity.QrCode
import com.application.adverial.ui.activity.SearchResult
import com.application.adverial.ui.adapter.SearchAdapter
import kotlinx.android.synthetic.main.dialog_search.view.recyclerView
import kotlinx.android.synthetic.main.dialog_search.view.search_text
import java.util.*
import kotlin.collections.ArrayList

class SearchDialog: DialogFragment(){

    private lateinit var mBlurEngine: BlurDialogEngine
    private lateinit var anim: LottieAnimationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBlurEngine = BlurDialogEngine(requireActivity())
        mBlurEngine.setBlurRadius(10)
        mBlurEngine.setDownScaleFactor(10f)
        mBlurEngine.setBlurActionBar(true)
        mBlurEngine.setUseRenderScript(true)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view= inflater.inflate(R.layout.dialog_search, container, false)
        val window: Window? = dialog!!.window
        val wlp: WindowManager.LayoutParams = window!!.attributes
        wlp.gravity = Gravity.TOP
        window.attributes = wlp

        uiSetup(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(Tools().displayWidth(), ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun uiSetup(view: View){
        AutoComplete(requireContext(), view.recyclerView, view.search_text, "ad")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("search"))
        anim= view.findViewById(R.id.lottie16)
        val clean= view.findViewById<TextView>(R.id.search_clean)
        val recyclerView= view.findViewById<RecyclerView>(R.id.search_recyclerView)
        val search= view.findViewById<ImageView>(R.id.search_search)
        val text= view.findViewById<TextView>(R.id.search_text)
        val back= view.findViewById<ImageView>(R.id.search_back)
        val qr= view.findViewById<ImageView>(R.id.search_qr)
        clean.paintFlags= clean.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        text.requestFocus()
        val imgr: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        text.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.action == KeyEvent.ACTION_UP) {
                    if (text.text.isNotBlank()){
                        search(view)
                    }
                }
                true
            } else false
        }

        if(Tools().authCheck(requireContext())){
            val repo= Repository(requireContext())
            repo.latestSearch()
            repo.getLatestSearchData().observe(viewLifecycleOwner, {
                if(it.status){
                    val keywords= it.data as ArrayList<LatestSearchData>
                    keywords.reverse()
                    val adapter= SearchAdapter(keywords)
                    recyclerView.adapter= adapter
                    adapter.getResult().observe(viewLifecycleOwner, {
                        if(it == "hideKeyboard"){
                            imgr.hideSoftInputFromWindow(requireView().windowToken, 0)
                        }
                    })
                }
            })
        }

        search.setOnClickListener {
            if (text.text.isNotBlank()){
                search(view)
            }
        }

        clean.setOnClickListener{
            if(Tools().authCheck(requireContext())){
                anim.visibility= View.VISIBLE
                Tools().viewEnable(view, false)
                val repo= Repository(requireContext())
                repo.clearSearch()
                repo.getClearSearchData().observe(viewLifecycleOwner, { itt->
                    if(itt.status){
                        val repo1= Repository(requireContext())
                        repo1.latestSearch()
                        repo1.getLatestSearchData().observe(viewLifecycleOwner, {
                            anim.visibility= View.GONE
                            Tools().viewEnable(view, true)
                            if(it.status){
                                val adapter= SearchAdapter(it.data as ArrayList<LatestSearchData>)
                                recyclerView.adapter= adapter
                                adapter.getResult().observe(viewLifecycleOwner, {
                                    if(it == "hideKeyboard"){
                                        imgr.hideSoftInputFromWindow(requireView().windowToken, 0)
                                    }
                                })
                            }
                        })
                    }
                })
            }
        }

        back.setOnClickListener {
            imgr.hideSoftInputFromWindow(requireView().windowToken, 0)
            dialog!!.dismiss()
        }
        qr.setOnClickListener{
            val permissionListener: PermissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    val imgr: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imgr.hideSoftInputFromWindow(requireView().windowToken, 0)
                    val intent= Intent(requireContext(), QrCode::class.java)
                    startActivity(intent)
                    dialog!!.dismiss()
                }
                override fun onPermissionDenied(deniedPermissions: List<String?>) {}
            }
            TedPermission.with(requireContext()).setPermissionListener(permissionListener).setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).check()
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data= intent.getStringExtra("data")
            val text= requireView().findViewById<TextView>(R.id.search_text)
            text.text= data
        }
    }

    private fun search(view: View){
        val text= view.findViewById<TextView>(R.id.search_text)
        val imgr: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        val intent= Intent(requireContext(), SearchResult::class.java)
        intent.putExtra("keyword", text.text.toString())
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mBlurEngine.onResume(retainInstance)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mBlurEngine.onDismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBlurEngine.onDetach()
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }
}