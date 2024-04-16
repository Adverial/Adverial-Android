package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.Category
import com.bumptech.glide.Glide

class HomeCategoryAdapter(var itemList: List<com.application.adverial.remote.model.SubCategory>) : RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_home_category, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: HomeCategoryAdapter.ViewHolder, position: Int) {

        if(itemList[position].image != null)
        {
            Glide.with(context).load(Tools().getPublicPath() + itemList[position].image).into(holder.image)
        }
        else
        {
            // set image as name
            holder.image.setImageResource(R.drawable.im_logo)

        }



        holder.image.setOnClickListener {
            val intent= Intent(context, Category::class.java)
            intent.putExtra("position", holder.adapterPosition)
            intent.putExtra("name", itemList[holder.adapterPosition].name)
            intent.putExtra("id", itemList[holder.adapterPosition].id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ImageView= itemView.findViewById(R.id.itemHomeCategory_image)
    }
}