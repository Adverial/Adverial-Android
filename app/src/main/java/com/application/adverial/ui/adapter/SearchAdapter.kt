package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.LatestSearchData
import com.application.adverial.ui.activity.SearchResult

class SearchAdapter(var itemList: ArrayList<LatestSearchData>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result= MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        holder.name.text= itemList[position].keyword
        if(position == itemList.size - 1) holder.separator.visibility= View.GONE

        holder.item.setOnClickListener {
            result.value= "hideKeyboard"
            val intent= Intent(context, SearchResult::class.java)
            intent.putExtra("keyword", itemList[holder.adapterPosition].keyword)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView= itemView.findViewById(R.id.search_name)
        val separator: TextView= itemView.findViewById(R.id.search_separator)
        val item: ConstraintLayout= itemView.findViewById(R.id.search_item)
    }

    fun getResult(): MutableLiveData<String>{
        return result
    }
}