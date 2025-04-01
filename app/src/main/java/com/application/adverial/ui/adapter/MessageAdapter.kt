package com.application.adverial.ui

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.BuildConfig
import com.application.adverial.databinding.ItemMessageLeftBinding
import com.application.adverial.databinding.ItemMessageRightBinding
import com.application.adverial.remote.model.Message
import com.application.adverial.ui.activity.FullImageActivity
import com.bumptech.glide.Glide
import java.io.IOException

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()
    private val VIEW_TYPE_LEFT = 1
    private val VIEW_TYPE_RIGHT = 2
    private var currentUserId = 8
    private var mediaPlayer: MediaPlayer? = null

    fun setCurrentUserId(id: Int) {
        currentUserId = id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_RIGHT) {
            val binding =
                    ItemMessageRightBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            RightMessageViewHolder(binding)
        } else {
            val binding =
                    ItemMessageLeftBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            LeftMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RightMessageViewHolder) {
            holder.bind(messages[position])
        } else if (holder is LeftMessageViewHolder) {
            holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_RIGHT else VIEW_TYPE_LEFT
    }

    fun setMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    inner class RightMessageViewHolder(private val binding: ItemMessageRightBinding) :
            RecyclerView.ViewHolder(binding.root) {

        private var mediaPlayer: MediaPlayer? = null
        private var isPlaying = false

        fun bind(message: Message) {
            // Handle text message
            binding.textViewMessageRight.visibility =
                    if (message.message.isNullOrEmpty() || message.message == "null") {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            binding.textViewMessageRight.text = message.message

            // Handle image message
            binding.imageViewMediaRight.visibility =
                    if (message.mediaUrl != null) {
                        // Check if the URL is already a complete URL or if it needs the base URL
                        val fullMediaUrl =
                                if (message.mediaUrl.startsWith("http")) {
                                    message.mediaUrl
                                } else {
                                    BuildConfig.API_BASE_URL + message.mediaUrl
                                }

                        android.util.Log.d("MediaDebug", "Loading media URL: $fullMediaUrl")

                        Glide.with(binding.root.context)
                                .load(fullMediaUrl)
                                .into(binding.imageViewMediaRight)

                        binding.imageViewMediaRight.setOnClickListener {
                            // Pass the already processed full URL to the activity
                            openFullImageView(binding.root.context, fullMediaUrl)
                        }
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

            // Handle voice message
            binding.voiceMessageLayoutRight.visibility =
                    if (message.voiceUrl != null) {
                        binding.playButtonRight.setOnClickListener {
                            if (isPlaying) {
                                stopPlayback()
                            } else {
                                startPlayback(message.voiceUrl)
                            }
                        }
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
        }

        private fun openFullImageView(context: Context, mediaUrl: String) {
            // The URL should already be fully formatted by the caller
            android.util.Log.d("MediaDebug", "Opening full image view: $mediaUrl")
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("mediaUrl", mediaUrl)
            context.startActivity(intent)
        }

        private fun startPlayback(audioUrl: String) {
            stopPlayback()

            try {
                val player = MediaPlayer()
                // Check if the URL is already a complete URL or if it needs the base URL
                val fullUrl =
                        if (audioUrl.startsWith("http")) {
                            audioUrl
                        } else {
                            BuildConfig.API_BASE_URL + audioUrl
                        }
                android.util.Log.d("VoiceDebug", "Playing voice URL: $fullUrl")
                player.setDataSource(fullUrl)
                player.setOnPreparedListener {
                    player.start()
                    binding.playButtonRight.setImageResource(android.R.drawable.ic_media_pause)
                    binding.progressBarRight.visibility = View.VISIBLE
                    isPlaying = true
                }
                player.setOnCompletionListener {
                    binding.playButtonRight.setImageResource(android.R.drawable.ic_media_play)
                    binding.progressBarRight.visibility = View.GONE
                    isPlaying = false
                }
                player.prepareAsync()
                mediaPlayer = player
            } catch (e: IOException) {
                android.util.Log.e("VoiceDebug", "Error playing voice: ${e.message}")
                e.printStackTrace()
            }
        }

        private fun stopPlayback() {
            mediaPlayer?.apply {
                if (this@RightMessageViewHolder.isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            isPlaying = false
            binding.playButtonRight.setImageResource(android.R.drawable.ic_media_play)
            binding.progressBarRight.visibility = View.GONE
        }
    }

    inner class LeftMessageViewHolder(private val binding: ItemMessageLeftBinding) :
            RecyclerView.ViewHolder(binding.root) {

        private var mediaPlayer: MediaPlayer? = null
        private var isPlaying = false

        fun bind(message: Message) {
            // Handle text message
            binding.textViewMessageLeft.visibility =
                    if (message.message.isNullOrEmpty() || message.message == "null") {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            binding.textViewMessageLeft.text = message.message

            // Handle image message
            binding.imageViewMediaLeft.visibility =
                    if (message.mediaUrl != null) {
                        // Check if the URL is already a complete URL or if it needs the base URL
                        val fullMediaUrl =
                                if (message.mediaUrl.startsWith("http")) {
                                    message.mediaUrl
                                } else {
                                    BuildConfig.API_BASE_URL + message.mediaUrl
                                }

                        android.util.Log.d("MediaDebug", "Loading media URL: $fullMediaUrl")

                        Glide.with(binding.root.context)
                                .load(fullMediaUrl)
                                .into(binding.imageViewMediaLeft)

                        binding.imageViewMediaLeft.setOnClickListener {
                            // Pass the already processed full URL to the activity
                            openFullImageView(binding.root.context, fullMediaUrl)
                        }
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

            // Handle voice message
            binding.voiceMessageLayoutLeft.visibility =
                    if (message.voiceUrl != null) {
                        binding.playButtonLeft.setOnClickListener {
                            if (isPlaying) {
                                stopPlayback()
                            } else {
                                startPlayback(message.voiceUrl)
                            }
                        }
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
        }

        private fun openFullImageView(context: Context, mediaUrl: String) {
            // The URL should already be fully formatted by the caller
            android.util.Log.d("MediaDebug", "Opening full image view: $mediaUrl")
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("mediaUrl", mediaUrl)
            context.startActivity(intent)
        }

        private fun startPlayback(audioUrl: String) {
            stopPlayback()

            try {
                val player = MediaPlayer()
                // Check if the URL is already a complete URL or if it needs the base URL
                val fullUrl =
                        if (audioUrl.startsWith("http")) {
                            audioUrl
                        } else {
                            BuildConfig.API_BASE_URL + audioUrl
                        }
                android.util.Log.d("VoiceDebug", "Playing voice URL: $fullUrl")
                player.setDataSource(fullUrl)
                player.setOnPreparedListener {
                    player.start()
                    binding.playButtonLeft.setImageResource(android.R.drawable.ic_media_pause)
                    binding.progressBarLeft.visibility = View.VISIBLE
                    isPlaying = true
                }
                player.setOnCompletionListener {
                    binding.playButtonLeft.setImageResource(android.R.drawable.ic_media_play)
                    binding.progressBarLeft.visibility = View.GONE
                    isPlaying = false
                }
                player.prepareAsync()
                mediaPlayer = player
            } catch (e: IOException) {
                android.util.Log.e("VoiceDebug", "Error playing voice: ${e.message}")
                e.printStackTrace()
            }
        }

        private fun stopPlayback() {
            mediaPlayer?.apply {
                if (this@LeftMessageViewHolder.isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            isPlaying = false
            binding.playButtonLeft.setImageResource(android.R.drawable.ic_media_play)
            binding.progressBarLeft.visibility = View.GONE
        }
    }
}
