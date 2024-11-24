package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.application.adverial.R
import com.application.adverial.remote.model.Ad
import com.application.adverial.remote.model.AdOption
import com.application.adverial.ui.activity.VideoPlayerActivity

class PostPageAdapter(var itemList: Ad, var id: String) : RecyclerView.Adapter<PostPageAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostPageAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_post_page, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: PostPageAdapter.ViewHolder, position: Int) {
        holder.title.text= itemList.price_currency
        holder.name.text= itemList.user_name?:itemList.user_detail!!.name?:""
        holder.city.text= itemList.title
        holder.date.text= itemList.created_at!!.split("T")[0]
        holder.address.text= "${itemList.district_detail?.name}, ${itemList.city_detail?.name}"
        holder.recyclerView.adapter= PostDetailsAdapter(itemList.ad_options as ArrayList<AdOption>)
        holder.description.text= itemList.description

        holder.slider.setSliderAdapter(PostSliderAdapter(itemList))
        holder.slider.setIndicatorAnimation(IndicatorAnimationType.NONE)
        holder.slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        holder.slider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        holder.slider.isAutoCycle= false
        holder.slider.setInfiniteAdapterEnabled(false)
        holder.slider.stopAutoCycle()

        // Check if ad_images contains a video
        val videoUrl = itemList.ad_images?.firstOrNull { it.image?.endsWith(".mp4") == true }?.image

        if (videoUrl != null) {
            holder.viewVideo.visibility = View.VISIBLE
            holder.viewVideo.setOnClickListener {
                // Start VideoPlayerActivity
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra("video_url", videoUrl)
                context.startActivity(intent)
            }
        } else {
            holder.viewVideo.visibility = View.GONE
        }

        holder.info.setOnClickListener{
            holder.info.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
            holder.desc.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            holder.description.visibility= View.GONE
            holder.recyclerView.visibility= View.VISIBLE
        }
        holder.desc.setOnClickListener{
            holder.info.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            holder.desc.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
            holder.description.visibility= View.VISIBLE
            holder.recyclerView.visibility= View.GONE
        }
    }

    override fun getItemCount(): Int { return 1 }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val city: TextView= itemView.findViewById(R.id.post_city)
        val title: TextView= itemView.findViewById(R.id.post_title)
        val slider: SliderView= itemView.findViewById(R.id.post_slider)
        val name: TextView= itemView.findViewById(R.id.post_name)
        val date: TextView= itemView.findViewById(R.id.post_date)
        val address: TextView= itemView.findViewById(R.id.post_address)
        val info: TextView= itemView.findViewById(R.id.post_info)
        val desc: TextView= itemView.findViewById(R.id.post_desc)
        val description: TextView= itemView.findViewById(R.id.post_description)
        val recyclerView: RecyclerView= itemView.findViewById(R.id.post_recyclerView)
        val viewVideo: Button = itemView.findViewById(R.id.viewVideo)

        init {
            recyclerView.layoutManager= LinearLayoutManager(context)
        }
    }
}