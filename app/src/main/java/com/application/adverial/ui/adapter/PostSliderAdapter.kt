package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context

import com.smarteist.autoimageslider.SliderViewAdapter
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.application.adverial.R
import com.application.adverial.remote.model.Ad
import com.application.adverial.service.Tools
import com.application.adverial.ui.dialog.PhotoViewer


class PostSliderAdapter(var itemList: Ad) : SliderViewAdapter<PostSliderAdapter.SliderAdapterVH>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post_slider, null)
        context= parent.context
        return SliderAdapterVH(inflate)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        viewHolder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.im_image))
        Glide.with(context).load(Tools().getPath() + itemList.ad_images?.get(position)?.image).into(viewHolder.image)
        viewHolder.itemView.setOnClickListener{
            val dialog= PhotoViewer(itemList, position)
            dialog.show((context as AppCompatActivity).supportFragmentManager, "PhotoViewer")
        }
    }

    override fun getCount(): Int {
        return itemList.ad_images!!.size
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemPostSlider_image)
    }
}