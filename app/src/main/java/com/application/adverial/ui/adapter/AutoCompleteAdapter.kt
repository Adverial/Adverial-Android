package com.application.adverial.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.AutoCompleteData
import kotlinx.android.synthetic.main.item_auto_complete.view.text

class AutoCompleteAdapter(var itemList: List<AutoCompleteData>) : RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder>() {

    private lateinit var context: Context
    val result= MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoCompleteAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_auto_complete, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AutoCompleteAdapter.ViewHolder, position: Int) {
        with(holder.itemView){
            text.text= itemList[position].title

            setOnClickListener {
                result.value= itemList[holder.adapterPosition].title?:""
            }
        }
    }

    override fun getItemCount(): Int { return if(itemList.size <= 3) itemList.size else 3 }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}