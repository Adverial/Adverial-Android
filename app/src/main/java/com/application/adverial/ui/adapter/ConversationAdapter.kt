package com.application.adverial.ui.adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.Conversation
import com.application.adverial.service.Tools
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_conversation.view.*

class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private var conversations: List<Conversation> = listOf()

    fun setConversations(conversations: List<Conversation>) {
        this.conversations = conversations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(conversation: Conversation) {
            itemView.textViewChatPartnerName.text = conversation.chatPartnerName
            itemView.textViewChatPartnerEmail.text = conversation.lastMessage
            Glide.with(itemView.context)
                .load (Tools().getPath().plus(conversation.avatar))
                .placeholder(R.drawable.baseline_arrow_back_24)
                .into(itemView.imageViewAvatar)
        }
    }
}
