package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.ui.activity.Message

class MessagesListAdapter() : RecyclerView.Adapter<MessagesListAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result= MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesListAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_messages_list, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: MessagesListAdapter.ViewHolder, position: Int) {
        holder.item.setOnClickListener {
            val intent= Intent(context, Message::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int { return 1 }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ImageView= itemView.findViewById(R.id.itemResult_image)
        val title: TextView= itemView.findViewById(R.id.itemResult_name)
        val date: TextView= itemView.findViewById(R.id.itemResult_date)
        val price: TextView= itemView.findViewById(R.id.itemResult_price)
        val item: ConstraintLayout= itemView.findViewById(R.id.itemResult_item)
    }

    fun getResult(): MutableLiveData<Int>{ return result }
}