package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.SubCategory
import com.application.adverial.service.Tools

class NewAdCategoryAdapter(var itemList: ArrayList<SubCategory>) : RecyclerView.Adapter<NewAdCategoryAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var count= 0
    private val result= MutableLiveData<com.application.adverial.ui.model.SubCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAdCategoryAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: NewAdCategoryAdapter.ViewHolder, position: Int) {
        when (count) {
            0 -> {holder.color.backgroundTintList= ContextCompat.getColorStateList(context, R.color.yellow); count++}
            1 -> {holder.color.backgroundTintList= ContextCompat.getColorStateList(context, R.color.light_gray); count++}
            2 -> {holder.color.backgroundTintList= ContextCompat.getColorStateList(context, R.color.red); count++}
            3 -> {holder.color.backgroundTintList= ContextCompat.getColorStateList(context, R.color.green); count= 0}
        }
        Tools().rotateLayout(context,holder.arrow)
        holder.name.text= itemList[position].name
        holder.item.setOnClickListener {
            result.value= com.application.adverial.ui.model.SubCategory(itemList[holder.adapterPosition].id.toString(), holder.adapterPosition, itemList[holder.adapterPosition].name.toString(), itemList[holder.adapterPosition].type!!)
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val color: ImageView= itemView.findViewById(R.id.itemCategory_color)
        val arrow: ImageView= itemView.findViewById(R.id.newAddArrow)
        val name: TextView= itemView.findViewById(R.id.itemCategory_name)
        val item: ConstraintLayout = itemView.findViewById(R.id.itemCategory_item)
    }

    fun getResult(): MutableLiveData<com.application.adverial.ui.model.SubCategory>{ return result }
}