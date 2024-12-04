package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.ShowRoomData
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.Post
import com.bumptech.glide.Glide

class HomePostsAdapter(var itemList: ArrayList<ShowRoomData>) : RecyclerView.Adapter<HomePostsAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostsAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_showroom, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: HomePostsAdapter.ViewHolder, position: Int) {
        holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.im_image))
//        Log.d("image", Tools().getPublicPath() + (itemList[position].ad_images?.get(0)?.image ?: ""))
        holder.price.text= itemList[position].price_currency
       //print log all images
        for (i in itemList[position].ad_images!!){
            i.image?.let { Log.d("image", it) }
        }

        if(itemList[position].ad_images!!.isNotEmpty()) Glide.with(context).load( (itemList[position].ad_images?.get(0)?.image ?: "")).into(holder.image)
        holder.name.text= itemList[position].title
        holder.item.setOnClickListener {
            val intent= Intent(context, Post::class.java)
            intent.putExtra("id", itemList[holder.adapterPosition].id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ImageView= itemView.findViewById(R.id.itemHomeProduct_image)
        val name: TextView= itemView.findViewById(R.id.itemHomeProduct_name)
        val price: TextView= itemView.findViewById(R.id.showroom_price)
        val item: ConstraintLayout= itemView.findViewById(R.id.itemHomeProduct_item)
    }
}