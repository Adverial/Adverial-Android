package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.AdOption

class PostDetailsAdapter(var itemList: ArrayList<AdOption>) : RecyclerView.Adapter<PostDetailsAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailsAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_post_details, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: PostDetailsAdapter.ViewHolder, position: Int) {
        holder.title.text = itemList[position].title
        holder.value.text = itemList[position].result
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView= itemView.findViewById(R.id.itemPostDetails_title)
        val value: TextView= itemView.findViewById(R.id.itemPostDetails_value)
    }
}