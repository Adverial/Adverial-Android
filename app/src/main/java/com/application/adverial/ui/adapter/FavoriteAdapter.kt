package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.FavoriteData
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.Post
import com.application.adverial.ui.dialog.AlertDialog
import com.bumptech.glide.Glide

class FavoriteAdapter(var itemList: ArrayList<FavoriteData>) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolder, position: Int) {

        //   Glide.with(context).load(Tools().getPath() + (itemList[position].ad_detail!!.ad_images?.get(0)?.image ?: "")).into(holder.image)

        val adImages = itemList[position].ad_detail!!.ad_images
        if (adImages != null && adImages.isNotEmpty()) {
            Glide.with(context).load(Tools().getPath() + adImages[0].image).into(holder.image)
        } else {
            holder.image.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.album_not_found
                )
            )
        }

        if (itemList[position].ad_detail!!.visibility == "1") {
            holder.status.text = context.resources.getString(R.string.post_active)
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green))
            holder.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_check
                )
            )
            holder.statusImage.imageTintList =
                ContextCompat.getColorStateList(context, R.color.green)
        } else {
            holder.status.text = context.resources.getString(R.string.post_expired)
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_denied
                )
            )
            holder.statusImage.imageTintList = ContextCompat.getColorStateList(context, R.color.red)
        }
        holder.price.text = itemList[position].ad_detail!!.price_currency
        holder.name.text = itemList[position].ad_detail!!.title
        val cityDetailName = itemList[position].ad_detail?.city_detail?.name ?: ""
        val countryDetailName = itemList[position].ad_detail?.country_detail?.name ?: ""
        val createdAt = itemList[position].ad_detail?.created_at?.split("T")?.get(0) ?: ""

        holder.date.text = "$cityDetailName, $countryDetailName\n$createdAt"

        // holder.date.text= itemList[position].ad_detail!!.city_detail!!.name + ", " + itemList[position].ad_detail!!.country_detail!!.name + "\n" + itemList[position].ad_detail!!.created_at!!.split("T")[0]
        holder.remove.setOnClickListener {
            val repo = Repository(context)
            repo.removeFavorite(itemList[holder.adapterPosition].ad_detail!!.id.toString())
            repo.getRemoveFavoriteData().observe(context as LifecycleOwner, {
                if (it.status) {
                    result.value = holder.adapterPosition
                }
            })
        }
        holder.item.setOnClickListener {
            if (itemList[holder.adapterPosition].ad_detail!!.visibility == "1") {
                val intent = Intent(context, Post::class.java)
                intent.putExtra("id", itemList[holder.adapterPosition].ad_detail!!.id.toString())
                context.startActivity(intent)
            } else {
                val dialog = AlertDialog(
                    context.resources.getString(R.string.error),
                    context.resources.getString(R.string.adNotAvailable)
                )
                dialog.show((context as AppCompatActivity).supportFragmentManager, "AlertDialog")
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemPost_image)
        val status: TextView = itemView.findViewById(R.id.itemPost_status)
        val name: TextView = itemView.findViewById(R.id.itemResult_name)
        val date: TextView = itemView.findViewById(R.id.itemResult_date)
        val statusImage: ImageView = itemView.findViewById(R.id.itemPost_statusImage)
        val notification: ImageView = itemView.findViewById(R.id.itemPost_notification)
        val price: TextView = itemView.findViewById(R.id.itemPost_price)
        val remove: ImageView = itemView.findViewById(R.id.itemPost_remove)
        val item: ConstraintLayout = itemView.findViewById(R.id.itemPost_item)
    }

    fun getResult(): MutableLiveData<Int> {
        return result
    }
}