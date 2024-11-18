package com.application.adverial.ui

import android.content.Context
import android.content.Intent
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

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()
    private val VIEW_TYPE_LEFT = 1
    private val VIEW_TYPE_RIGHT = 2
    private var currentUserId = 8

    fun setCurrentUserId(id: Int) {
        currentUserId = id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_RIGHT) {
            val binding = ItemMessageRightBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            RightMessageViewHolder(binding)
        } else {
            val binding = ItemMessageLeftBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
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

    class RightMessageViewHolder(private val binding: ItemMessageRightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                textViewMessageRight.visibility = if (message.message.isNullOrEmpty() || message.message == "null") {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                textViewMessageRight.text = message.message

                if (message.mediaUrl != null) {
                    imageViewMediaRight.visibility = View.VISIBLE
                    Glide.with(root.context)
                        .load(BuildConfig.API_BASE_URL + message.mediaUrl)
                        .into(imageViewMediaRight)

                    imageViewMediaRight.setOnClickListener {
                        openFullImageView(root.context, message.mediaUrl)
                    }
                } else {
                    imageViewMediaRight.visibility = View.GONE
                }
            }
        }

        private fun openFullImageView(context: Context, mediaUrl: String) {
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("mediaUrl", mediaUrl)
            context.startActivity(intent)
        }
    }

    class LeftMessageViewHolder(private val binding: ItemMessageLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                textViewMessageLeft.visibility = if (message.message.isNullOrEmpty() || message.message == "null") {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                textViewMessageLeft.text = message.message

                if (message.mediaUrl != null) {
                    imageViewMediaLeft.visibility = View.VISIBLE
                    Glide.with(root.context)
                        .load(BuildConfig.API_BASE_URL + message.mediaUrl)
                        .into(imageViewMediaLeft)

                    imageViewMediaLeft.setOnClickListener {
                        openFullImageView(root.context, message.mediaUrl)
                    }
                } else {
                    imageViewMediaLeft.visibility = View.GONE
                }
            }
        }

        private fun openFullImageView(context: Context, mediaUrl: String) {
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("mediaUrl", mediaUrl)
            context.startActivity(intent)
        }
    }
}
