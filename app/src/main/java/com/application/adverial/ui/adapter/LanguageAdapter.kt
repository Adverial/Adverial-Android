package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.ui.model.Language1

class LanguageAdapter(var itemList: ArrayList<Language1>) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result= MutableLiveData<Language1>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: LanguageAdapter.ViewHolder, position: Int) {
        holder.name.text= itemList[position].name
        holder.flag.setImageDrawable(ContextCompat.getDrawable(context, itemList[position].flag))
        if(itemList[position].position == position){
            holder.radio.isChecked= true
            holder.radio.buttonTintList= ContextCompat.getColorStateList(context, R.color.red)
        }else{
            holder.radio.isChecked= false
            holder.radio.buttonTintList= ContextCompat.getColorStateList(context, R.color.light_gray)
        }
        holder.item.setOnClickListener {
            result.value= Language1(itemList[holder.adapterPosition].name, itemList[holder.adapterPosition].id, itemList[holder.adapterPosition].flag, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView= itemView.findViewById(R.id.itemDropList_name)
        val radio: RadioButton= itemView.findViewById(R.id.itemDropList_radio)
        val item: ConstraintLayout= itemView.findViewById(R.id.itemDropList_item)
        val flag: ImageView= itemView.findViewById(R.id.itemDropList_flag)

        init {
            radio.isClickable= false
            radio.isFocusable= false
        }
    }

    fun getResult(): MutableLiveData<Language1>{ return result }
}