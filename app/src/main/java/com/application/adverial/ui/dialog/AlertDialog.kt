package com.application.adverial.ui.dialog

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.application.adverial.R


class AlertDialog(var title: String, private var details: String) : DialogFragment(){

    private val status= MutableLiveData<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        val view= inflater.inflate(R.layout.alert_dialog, container, false)
        val window: Window? = dialog!!.window
        val wlp: WindowManager.LayoutParams = window!!.attributes
        //dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp

        uiSetup(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.7).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun uiSetup(view: View){
        val ok= view.findViewById<TextView>(R.id.alertDialog_ok)
        val titleText= view.findViewById<TextView>(R.id.alertDialog_title)
        val detailsText= view.findViewById<TextView>(R.id.alertDialog_details)
        titleText.text= title
        detailsText.text= details
        ok.setOnClickListener{ status.value= "ok"; dialog!!.dismiss()}
    }

    fun getStatus(): MutableLiveData<String>{ return status }
}