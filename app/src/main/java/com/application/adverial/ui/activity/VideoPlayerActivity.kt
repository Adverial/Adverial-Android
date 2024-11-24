
package com.application.adverial.ui.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoUrl = intent.getStringExtra("video_url")

        if (videoUrl != null) {
            val videoView: VideoView = findViewById(R.id.video_view)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            val uri = Uri.parse(videoUrl)
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(uri)
            videoView.requestFocus()
            videoView.start()
        }

        // Handle back button
        val backButton: View = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}