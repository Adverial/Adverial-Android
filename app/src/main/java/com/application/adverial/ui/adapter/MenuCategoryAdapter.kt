package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.Category
import com.bumptech.glide.Glide

class MenuCategoryAdapter(var itemList: List<com.application.adverial.remote.model.SubCategory>) : RecyclerView.Adapter<MenuCategoryAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuCategoryAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_menu_category, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: MenuCategoryAdapter.ViewHolder, position: Int) {
        if (itemList[position].image != null) {
            Glide.with(context).load(Tools().getPublicPath() + itemList[position].image).into(holder.image)
        }
        else
        {
            holder.image.setImageResource(R.drawable.im_logo)
        }
        holder.title.text= itemList[position].name
        if(itemList[position].total_ad_count != null) holder.count.text= "(${itemList[position].total_ad_count})"
        holder.title.setOnClickListener {
            val intent= Intent(context, Category::class.java)
            intent.putExtra("position", holder.adapterPosition)
            intent.putExtra("name", itemList[holder.adapterPosition].name)
            intent.putExtra("id", itemList[holder.adapterPosition].id.toString())
            context.startActivity(intent)
        }
        holder.image.setOnClickListener {
            val intent= Intent(context, Category::class.java)
            intent.putExtra("position", holder.adapterPosition)
            intent.putExtra("name", itemList[holder.adapterPosition].name)
            intent.putExtra("id", itemList[holder.adapterPosition].id.toString())
            context.startActivity(intent)
        }
        holder.layout.setOnClickListener {
            val intent= Intent(context, Category::class.java)
            intent.putExtra("position", holder.adapterPosition)
            intent.putExtra("name", itemList[holder.adapterPosition].name)
            intent.putExtra("id", itemList[holder.adapterPosition].id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ImageView= itemView.findViewById(R.id.itemMenuCategory_image)
        val title: TextView= itemView.findViewById(R.id.itemMenuCategory_title)
        val count: TextView= itemView.findViewById(R.id.itemMenuCategory_count)
        val item: ConstraintLayout= itemView.findViewById(R.id.itemMenuCategory_item)
        val layout: LinearLayout= itemView.findViewById(R.id.itemMenuCategory_layout)
    }
}