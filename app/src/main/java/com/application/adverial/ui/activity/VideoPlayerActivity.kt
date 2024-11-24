package com.application.adverial.ui.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import android.widget.ProgressBar
import android.media.MediaPlayer

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoUrl = intent.getStringExtra("video_url")

        if (videoUrl != null) {
            val videoView: VideoView = findViewById(R.id.video_view)
            val progressBar: ProgressBar = findViewById(R.id.progress_bar)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            val uri = Uri.parse(videoUrl)
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(uri)
            videoView.requestFocus()
            videoView.setOnPreparedListener { mediaPlayer ->
                // Hide progress bar when video is ready
                progressBar.visibility = View.GONE
                mediaPlayer.start()
            }
            videoView.setOnInfoListener { _, what, _ ->
                // Show or hide progress bar based on buffering status
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    progressBar.visibility = View.VISIBLE
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    progressBar.visibility = View.GONE
                }
                false
            }
            // Show progress bar while loading
            progressBar.visibility = View.VISIBLE
        }

        // Handle back button
        val backButton: View = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}