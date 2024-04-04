package com.application.adverial.ui.dialog

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.application.adverial.R
import com.application.adverial.remote.Repository


class AdFeedback(val adId: String) : DialogFragment(){

    private val status= MutableLiveData<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_background1)
        val view= inflater.inflate(R.layout.dialog_ad_feedback, container, false)
        val window: Window? = dialog!!.window
        val wlp: WindowManager.LayoutParams = window!!.attributes
        //dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        wlp.gravity = Gravity.CENTER
        dialog!!.setCanceledOnTouchOutside(false)
        window.attributes = wlp

        uiSetup(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels*0.9).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun uiSetup(view: View){
        val message= view.findViewById<TextView>(R.id.adFeedback_message)
        val finish= view.findViewById<Button>(R.id.adFeedback_finish)
        finish.setOnClickListener{
            val repo= Repository(requireContext())
            repo.adFeedback(adId, message.text.toString())
            repo.getAdFeedbackData().observe(viewLifecycleOwner, {
                if(it.status){
                    status.value= "finish"
                }
            })
        }
    }

    fun getStatus(): MutableLiveData<String>{ return status }
}