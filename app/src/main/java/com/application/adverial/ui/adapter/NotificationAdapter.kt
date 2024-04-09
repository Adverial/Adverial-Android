package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.NotificationData
import com.application.adverial.ui.activity.Post
import com.application.adverial.ui.dialog.AlertDialog

class NotificationAdapter(var itemList: ArrayList<NotificationData>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {

        holder.text.text= itemList[position].title
        holder.title.text= itemList[position].text
        holder.date.text= itemList[position].created_at
        holder.item.setOnClickListener {
            if(itemList[holder.adapterPosition].ad_visibility == 1){
                val intent= Intent(context, Post::class.java)
                intent.putExtra("id", itemList[holder.adapterPosition].ad_id.toString())
                context.startActivity(intent)
            }else{
                val dialog= AlertDialog(context.resources.getString(R.string.error), context.resources.getString(R.string.adNotAvailable))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "AlertDialog")
            }
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val text: TextView = itemView.findViewById(R.id.notification_text)
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val date: TextView = itemView.findViewById(R.id.notification_date)
        val item: ConstraintLayout = itemView.findViewById(R.id.notification_item)
    }
}