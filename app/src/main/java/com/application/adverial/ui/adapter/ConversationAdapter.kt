package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.databinding.ItemConversationBinding
import com.application.adverial.remote.model.Conversation
import com.application.adverial.ui.activity.MessageActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private var conversations: List<Conversation> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size

    fun setConversations(conversations: List<Conversation>) {
        this.conversations = conversations
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(conversation: Conversation) {
            binding.apply {
                textViewChatPartnerName.text = conversation.chatPartnerName ?: "Unknown"
                lastMessage.text = conversation.lastMessage?.let {
                    if (it.length > 25) it.substring(0, 25) + "..." else it
                } ?: "No message"
                lastMessageTime.text = formatLastMessageAt(conversation.lastMessageAt)

                // Set avatar image
                imageViewAvatar.setImageDrawable(
                    getAvatar(root.context, conversation.chatPartnerName?.get(0)?.toString() ?: "?")
                )

                // Set click listener to open MessageActivity
                root.setOnClickListener {
                    val intent = Intent(root.context, MessageActivity::class.java).apply {
                        putExtra("conversation", conversation)
                    }
                    root.context.startActivity(intent)
                }
            }
        }

        private fun getAvatar(context: Context, firstChar: String): BitmapDrawable {
            val size = 128
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Create circular path
            val path = Path().apply {
                addCircle(size / 2f, size / 2f, size / 2f, Path.Direction.CCW)
            }
            canvas.clipPath(path)

            // Background paint
            val backgroundPaint = Paint().apply {
                color = Color.parseColor("#3F51B5")
                style = Paint.Style.FILL
            }
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)

            // Text paint
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 64f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }
            val xPos = size / 2f
            val yPos = (size / 2f - (textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText(firstChar, xPos, yPos, textPaint)

            return BitmapDrawable(context.resources, bitmap)
        }

        private fun formatLastMessageAt(timestamp: String?): String {
            if (timestamp.isNullOrEmpty()) return ""

            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val messageDate = inputFormat.parse(timestamp) ?: return "Invalid date"
                val currentDate = Date()

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                when {
                    dateFormat.format(currentDate) == dateFormat.format(messageDate) -> timeFormat.format(messageDate)
                    isYesterday(messageDate) -> "Yesterday"
                    else -> dateFormat.format(messageDate)
                }
            } catch (e: ParseException) {
                Log.e("ConversationAdapter", "Error parsing date: $e")
                "Invalid date"
            }
        }

        private fun isYesterday(date: Date): Boolean {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time) ==
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        }
    }
}
