package com.application.adverial.ui.dialog

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.application.adverial.R
import java.io.File


class SinglePhotoViewer(var path: String) : DialogFragment(){

    private val status= MutableLiveData<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        val view= inflater.inflate(R.layout.dialog_single_photo_viewer, container, false)
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
        val image= view.findViewById<ImageView>(R.id.singlePhotoViewer_image)
        val imgFile = File(path)
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
    }

    fun getStatus(): MutableLiveData<String>{ return status }
}