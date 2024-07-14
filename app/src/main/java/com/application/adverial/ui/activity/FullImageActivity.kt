package com.application.adverial.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.bumptech.glide.Glide
class FullImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)
        var mediaUrl = intent.getStringExtra("mediaUrl")
        if (mediaUrl == null) {
            finish() // Close the activity if media URL is null
            return
        }
        mediaUrl= BuildConfig.API_BASE_URL + mediaUrl
        val imageViewFullImage = findViewById<ImageView>(R.id.imageViewFullImage)
        Glide.with(this).load(mediaUrl).into(imageViewFullImage)
        imageViewFullImage.setOnClickListener {
            finish() // Close the activity on image click
        }
    }
}
