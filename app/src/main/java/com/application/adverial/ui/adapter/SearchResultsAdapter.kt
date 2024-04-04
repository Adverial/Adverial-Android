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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.application.adverial.R
import com.application.adverial.remote.model.Ad
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.Post

class SearchResultsAdapter(var itemList: ArrayList<Ad>) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: SearchResultsAdapter.ViewHolder, position: Int) {
        if(!itemList[position].ad_images.isNullOrEmpty()) Glide.with(context).load(Tools().getPath() + (itemList[position].ad_images?.get(0)?.image ?: "")).into(holder.image)
        holder.name.text= itemList[position].title
        holder.date.text= itemList[position].city_detail?.name + ", " + itemList[position].country_detail?.name + "\n" + itemList[position].created_at!!.split("T")[0]
        holder.price.text= "${itemList[position].price_currency}"
        holder.item.setOnClickListener {
            val intent= Intent(context, Post::class.java)
            intent.putExtra("id", itemList[holder.adapterPosition].id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView= itemView.findViewById(R.id.itemResult_name)
        val date: TextView= itemView.findViewById(R.id.itemResult_date)
        val price: TextView= itemView.findViewById(R.id.itemResult_price)
        val image: ImageView= itemView.findViewById(R.id.itemResult_image)
        val item: ConstraintLayout= itemView.findViewById(R.id.itemResult_item)
    }
}