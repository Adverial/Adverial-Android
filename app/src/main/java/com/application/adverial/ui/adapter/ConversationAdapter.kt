package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.Conversation
import com.application.adverial.ui.activity.MessageActivity
import kotlinx.android.synthetic.main.item_conversation.view.imageViewAvatar
import kotlinx.android.synthetic.main.item_conversation.view.last_message
import kotlinx.android.synthetic.main.item_conversation.view.last_message_time
import kotlinx.android.synthetic.main.item_conversation.view.textViewChatPartnerName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {
    private var conversations: List<Conversation> = listOf()
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size
    fun setConversations(conversations: List<Conversation>) {
        this.conversations = conversations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SuspiciousIndentation")
        fun bind(conversation: Conversation) {
            itemView.textViewChatPartnerName.text = conversation.chatPartnerName ?: "Unknown"
            itemView.last_message.text = conversation.lastMessage?.let {
                if (it.length > 20) it.substring(0, 25) + "..." else it
            } ?: "No message"
            itemView.last_message_time.text = formatLastMessageAt(conversation.lastMessageAt)
            // Load the avatar as image of first char chatPartnerName
            itemView.imageViewAvatar.setImageDrawable(
                getAvatar(
                    itemView.context,
                    conversation.chatPartnerName?.get(0)?.toString() ?: "?"
                )
            )
            itemView.textViewChatPartnerName.text = conversation.chatPartnerName
            itemView.setOnClickListener {
//                Toast.makeText(itemView.context, "Clicked on ${conversation.conversionId}", Toast.LENGTH_SHORT).show()
                val intent = Intent(itemView.context, MessageActivity::class.java)
                intent.putExtra("conversation_id", conversation.conversionId)
                intent.putExtra("chat_partner_name", conversation.chatPartnerName)
                itemView.context.startActivity(intent)
            }
        }


        private fun getAvatar(
            context: android.content.Context,
            firstChar: String
        ): android.graphics.drawable.Drawable {
            val size = 128 // Size of the avatar image
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Create a circular path to clip
            val path = android.graphics.Path().apply {
                addCircle(size / 2f, size / 2f, size / 2f, android.graphics.Path.Direction.CCW)
            }
            canvas.clipPath(path)

            // Background Paint
            val backgroundPaint = Paint().apply {
                color = Color.parseColor("#3F51B5") // Example color
                style = Paint.Style.FILL
            }
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)

            // Text Paint
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 64f // Example text size
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

            // Draw the character on the canvas
            val xPos = size / 2f
            val yPos = (size / 2f - (textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText(firstChar, xPos, yPos, textPaint)

            // Convert the bitmap to drawable
            return BitmapDrawable(context.resources, bitmap)
        }

        private fun formatLastMessageAt(timestamp: String?): String {
            // Check if the timestamp is null or empty
            if (timestamp.isNullOrEmpty()) {
                return ""
            }
            try {
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val messageDate = inputDateFormat.parse(timestamp) ?: return "Invalid date format"

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentDate = Date()

                // Check if the message was sent today
                if (dateFormat.format(currentDate) == dateFormat.format(messageDate)) {
                    return timeFormat.format(messageDate)
                }

                // Check if the message was sent yesterday
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                if (dateFormat.format(calendar.time) == dateFormat.format(messageDate)) {
                    return "Yesterday"
                }

                // If the message was sent before yesterday
                return dateFormat.format(messageDate)
            } catch (e: ParseException) {
                // Handle the error appropriately
                Log.e("ConversationAdapter", "Error parsing date: $e")
                return "Invalid date format"
            }
        }

    }

}
