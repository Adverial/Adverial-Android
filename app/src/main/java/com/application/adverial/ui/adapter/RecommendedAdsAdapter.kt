package com.application.adverial.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.Ad
import com.application.adverial.ui.activity.Post
import com.bumptech.glide.Glide

class RecommendedAdsAdapter(private val context: Context, private val ads: List<Ad>) :
        RecyclerView.Adapter<RecommendedAdsAdapter.ViewHolder>() {

    init {
        Log.d("RecommendedAdsDebug", "Adapter initialized with ${ads.size} ads")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.recommended_ad_image)
        val title: TextView = view.findViewById(R.id.recommended_ad_title)
        val price: TextView = view.findViewById(R.id.recommended_ad_price)
        val priceTag: TextView = view.findViewById(R.id.recommended_ad_price_tag)
        val location: TextView = view.findViewById(R.id.recommended_ad_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("RecommendedAdsDebug", "Creating view holder")
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.recommended_ad_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ad = ads[position]
        Log.d("RecommendedAdsDebug", "Binding ad at position $position: ${ad.title}")

        holder.title.text = ad.title

        // Set price in the price tag overlay
        val priceText = ad.price_currency ?: "${ad.price} ${ad.price_currency}"
        holder.priceTag.text = priceText

        if (ad.city_detail != null) {
            holder.location.text = ad.city_detail?.name
        }

        if (ad.ad_images != null && ad.ad_images!!.isNotEmpty()) {
            Log.d("RecommendedAdsDebug", "Loading image: ${ad.ad_images!![0].image}")
            Glide.with(context)
                    .load(ad.ad_images!![0].image)
                    .placeholder(R.drawable.im_image)
                    .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.im_image)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Post::class.java)
            intent.putExtra("id", ad.id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        Log.d("RecommendedAdsDebug", "getItemCount: ${ads.size}")
        return ads.size
    }
}
