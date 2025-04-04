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
import com.application.adverial.R
import com.application.adverial.service.Tools
import com.application.adverial.ui.activity.NewAdCategory
import com.bumptech.glide.Glide

class NewAdAdapter(
        var itemList: List<com.application.adverial.remote.model.SubCategory>,
        var raw: List<com.application.adverial.remote.model.SubCategory>
) : RecyclerView.Adapter<NewAdAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAdAdapter.ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_new_ad_category, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: NewAdAdapter.ViewHolder, position: Int) {
        Glide.with(context)
                .load(Tools().getPublicPath() + (itemList[position].image ?: ""))
                .into(holder.image)
        holder.name.text = itemList[position].name
        Tools().rotateLayout(context, holder.arrow)

        // Debug logging for type
      //  val currentItem = itemList[holder.adapterPosition]
//        android.util.Log.d(
//                "TypeDebug",
//                """
//            Category: ${currentItem.name}
//            ID: ${currentItem.id}
//            Type: ${currentItem.type}
//        """.trimIndent()
//        )

        holder.item.setOnClickListener {
            val intent =
                    Intent(context, NewAdCategory::class.java).apply {
                        val selectedItem = itemList[holder.adapterPosition]
                        putExtra("id", selectedItem.id.toString())
                        putExtra("name", selectedItem.name)
                        putExtra("type", selectedItem.type)
//                        android.util.Log.d(
//                                "TypeDebug",
//                                "Setting category type: ${selectedItem.type}"
//                        )

                        for (i in raw.indices) {
                            if (raw[i].name == selectedItem.name) {
                                putExtra("position", i)
                                break
                            }
                        }
                    }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemNewAdCategory_image)
        val name: TextView = itemView.findViewById(R.id.itemNewAdCategory_name)
        val arrow: ImageView = itemView.findViewById(R.id.newAddArrow)
        val item: ConstraintLayout = itemView.findViewById(R.id.itemNewAdCategory_item)
    }
}
