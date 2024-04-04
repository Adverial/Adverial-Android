package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.CategoryOptionsData
import com.application.adverial.ui.dialog.DropList
import java.util.*
import kotlin.collections.ArrayList


class FilterItemsAdapter(var itemList: ArrayList<CategoryOptionsData>) : RecyclerView.Adapter<FilterItemsAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result= MutableLiveData<kotlin.String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterItemsAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_filter_item, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits", "RtlHardcoded")
    override fun onBindViewHolder(holder: FilterItemsAdapter.ViewHolder, position: Int) {
        holder.title.text= itemList[position].title
        holder.id.text= itemList[position].id.toString()
        if(itemList[position].form_type == "1"){
            holder.min.visibility= View.VISIBLE
            holder.max.visibility= View.VISIBLE
            holder.min.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    context.getSharedPreferences("filterOptions", 0).edit().putString(holder.id.text.toString(), holder.min.text.toString() + "-" + holder.max.text.toString()).apply()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            holder.max.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    context.getSharedPreferences("filterOptions", 0).edit().putString(holder.id.text.toString(), holder.min.text.toString() + "-" + holder.max.text.toString()).apply()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
        }
        else if(itemList[position].form_type == "2") holder.dropList.visibility= View.VISIBLE
        else{
            holder.enter.visibility= View.VISIBLE
            holder.enter.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    context.getSharedPreferences("filterOptions", 0).edit().putString(itemList[holder.adapterPosition].id.toString(), holder.enter.text.toString()).apply()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
        }

        holder.dropList.setOnClickListener {
            val data= ArrayList<com.application.adverial.ui.model.DropList>()
            for(i in itemList[holder.adapterPosition].values!!.indices){
                data.add(com.application.adverial.ui.model.DropList(itemList[holder.adapterPosition].values!![i].title, itemList[holder.adapterPosition].values!![i].id.toString()))
            }
            val dialog= DropList(data, itemList[holder.adapterPosition].title)
            dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
            dialog.getStatus().observe(context as LifecycleOwner, {
                holder.dropList.visibility= View.GONE
                holder.value.visibility= View.VISIBLE
                holder.value.text= it.name
                context.getSharedPreferences("filterOptions", 0).edit().putString(itemList[holder.adapterPosition].id.toString(), it.id).apply()
            })
        }
        holder.value.setOnClickListener {
            val data= ArrayList<com.application.adverial.ui.model.DropList>()
            for(i in itemList[holder.adapterPosition].values!!.indices){
                data.add(com.application.adverial.ui.model.DropList(itemList[holder.adapterPosition].values!![i].title, itemList[holder.adapterPosition].values!![i].id.toString()))
            }
            val dialog= DropList(data, itemList[holder.adapterPosition].title)
            dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
            dialog.getStatus().observe(context as LifecycleOwner, {
                holder.dropList.visibility= View.GONE
                holder.value.visibility= View.VISIBLE
                holder.value.text= it.name
                context.getSharedPreferences("filterOptions", 0).edit().putString(itemList[holder.adapterPosition].id.toString(), it.id).apply()
            })
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView= itemView.findViewById(R.id.itemFilterItem_title)
        val value: TextView= itemView.findViewById(R.id.itemFilterItem_value)
        val min: TextView= itemView.findViewById(R.id.itemFilterItem_min)
        val max: TextView= itemView.findViewById(R.id.itemFilterItem_max)
        val dropList: ImageView= itemView.findViewById(R.id.itemFilterItem_drop)
        val id: TextView= itemView.findViewById(R.id.itemFilterItem_id)
        val enter: TextView= itemView.findViewById(R.id.itemFilterItem_enter)
    }

    fun getResult(): MutableLiveData<kotlin.String>{ return result }
}