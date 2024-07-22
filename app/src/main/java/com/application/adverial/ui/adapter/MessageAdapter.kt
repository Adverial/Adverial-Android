package com.application.adverial.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.remote.model.Message
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.FullImageActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_message_left.view.*
import kotlinx.android.synthetic.main.item_message_right.view.*

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()
    private val VIEW_TYPE_LEFT = 1
    private val VIEW_TYPE_RIGHT = 2
    private var currentUserId =8;
    //set current user id
    fun setCurrentUserId(id:Int){
        currentUserId=id
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_RIGHT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_right, parent, false)
            RightMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_left, parent, false)
            LeftMessageViewHolder(view)
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



    class RightMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            itemView.textViewMessageRight.text = message.message
            if (message.mediaUrl != null) {
                itemView.imageViewMediaRight.visibility = View.VISIBLE
                Glide.with(itemView.context).load(BuildConfig.API_BASE_URL+message.mediaUrl).into(itemView.imageViewMediaRight)
                itemView.imageViewMediaRight.setOnClickListener {
                    openFullImageView(itemView.context, message.mediaUrl)
                }
            } else {
                itemView.imageViewMediaRight.visibility = View.GONE
            }
        }
        private fun openFullImageView(context: Context, mediaUrl: String) {
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("mediaUrl", mediaUrl)
            context.startActivity(intent)
        }
    }

    class LeftMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            itemView.textViewMessageLeft.text = message.message
            if (message.mediaUrl != null) {
                itemView.imageViewMediaLeft.visibility = View.VISIBLE
                Glide.with(itemView.context).load(BuildConfig.API_BASE_URL+message.mediaUrl).into(itemView.imageViewMediaLeft)
                itemView.imageViewMediaLeft.setOnClickListener {
                    openFullImageView(itemView.context, message.mediaUrl)
                }
            } else {
                itemView.imageViewMediaLeft.visibility = View.GONE
            }
        }
        private fun openFullImageView(context: Context, mediaUrl: String) {
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("mediaUrl", mediaUrl)
            context.startActivity(intent)
        }
    }

}
