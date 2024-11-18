package com.application.adverial.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.databinding.ItemAutoCompleteBinding
import com.application.adverial.remote.model.AutoCompleteData

class AutoCompleteAdapter(private var itemList: List<AutoCompleteData>) : RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder>() {

    val result = MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutoCompleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = if (itemList.size <= 3) itemList.size else 3

    inner class ViewHolder(private val binding: ItemAutoCompleteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AutoCompleteData) {
            binding.text.text = item.title

            // Set click listener
            binding.root.setOnClickListener {
                result.value = item.title ?: ""
            }
        }
    }
}
