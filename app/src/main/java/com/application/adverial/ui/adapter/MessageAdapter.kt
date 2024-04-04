package com.application.adverial.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel

import com.application.adverial.R


class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
        context= parent.context
        return when (viewType) {
            R.layout.item_message_left -> Left(view.inflate(viewType, parent, false))
            R.layout.item_message_right -> Right(view.inflate(viewType, parent, false))
            R.layout.item_message_date -> Date(view.inflate(viewType, parent, false))
            else -> throw IllegalArgumentException("Unsupported layout")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /*when(position){
            0 -> {
                (holder as Left).
            }
        }*/
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_message_date
            1 -> R.layout.item_message_right
            2 -> R.layout.item_message_left
            else -> R.layout.item_message_date
        }
    }

    inner class Left(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val text: TextView = itemView.findViewById(R.id.itemLeft_text)
        val profileImage: ShapeableImageView = itemView.findViewById(R.id.itemLeft_profileImage)
        init {
            profileImage.shapeAppearanceModel= ShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, context.resources.getDimension(R.dimen._15sdp)).build()
        }
    }

    inner class Right(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val text: TextView = itemView.findViewById(R.id.itemRight_text)
        val profileImage: ShapeableImageView = itemView.findViewById(R.id.itemRight_profileImage)
        init {
            profileImage.shapeAppearanceModel= ShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, context.resources.getDimension(R.dimen._15sdp)).build()
        }
    }

    inner class Date(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.item_date)
    }
}