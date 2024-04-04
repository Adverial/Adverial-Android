package com.application.adverial.ui.dialog

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.application.adverial.R
import com.application.adverial.remote.model.Ad
import com.application.adverial.ui.adapter.PostSliderZoomAdapter


class PhotoViewer(var itemList: Ad, var pos: Int) : DialogFragment(){

    private val status= MutableLiveData<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_background2)
        val view= inflater.inflate(R.layout.dialog_photo_viewer, container, false)
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
        val width = (resources.displayMetrics.widthPixels)
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun uiSetup(view: View){
        val slider= view.findViewById<SliderView>(R.id.post_slider)
        slider.setSliderAdapter(PostSliderZoomAdapter(itemList))
        slider.setIndicatorAnimation(IndicatorAnimationType.NONE)
        slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        slider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        slider.isAutoCycle= false
        slider.setInfiniteAdapterEnabled(false)
        slider.stopAutoCycle()
        slider.currentPagePosition= pos
    }

    fun getStatus(): MutableLiveData<String>{ return status }
}