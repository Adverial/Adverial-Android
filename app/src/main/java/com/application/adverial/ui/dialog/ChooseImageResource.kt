package com.application.adverial.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.github.dhaval2404.imagepicker.ImagePicker
import com.application.adverial.R
import com.application.adverial.service.Tools
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class ChooseImageResource() : DialogFragment(){

    private var liveData= MutableLiveData<List<String>>()
    private lateinit var imageUri: Uri
    private var mode= 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_background1)
        val view= inflater.inflate(R.layout.dialog_choose_image_source, container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) dialog!!.dismiss()
            true
        }

        uiSetup(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun uiSetup(view: View){
        val camera= view.findViewById<Button>(R.id.source_camera)
        val gallery= view.findViewById<Button>(R.id.source_gallery)
        val cancel= view.findViewById<Button>(R.id.source_cancel)

        gallery.setOnClickListener{
            mode= 1
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        cancel.setOnClickListener{
            dialog!!.dismiss()
        }

        camera.setOnClickListener{
            mode= 2
            ImagePicker.with(requireActivity()).cameraOnly().createIntent { intent ->
                startActivityForResult(intent, 1)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data!!
            val filePath: String? = PathUtil.getPath(requireContext(), imageUri)
            CoroutineScope(Dispatchers.IO).launch {
                val compressedImageFile = Compressor.compress(requireContext(), File(filePath))
                requireActivity().runOnUiThread {
                    Glide.with(requireContext()).asBitmap().load(compressedImageFile).into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            val bitmap= Tools().rotateImageIfRequired(requireContext(), resource, imageUri)
                            val uri= Tools().getImageUri(requireContext(), bitmap!!)
                            val filePath1: String? = PathUtil.getPath(requireContext(), uri!!)
                            liveData.value= listOf(filePath1!!, mode.toString())
                            dialog!!.dismiss()
                        }
                    })
                }
            }
        }else {
            dialog!!.dismiss()
        }
    }

    fun getData(): MutableLiveData<List<String>>{
        return liveData
    }
}