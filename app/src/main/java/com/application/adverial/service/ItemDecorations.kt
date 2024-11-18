package com.wookweb.lezzetapp.service

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R

class ItemDecorations(var context: Context) {


    inner class Category(private var space: Int): RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top= space
            outRect.left= space
        }
    }

    inner class images(private var count: Int): RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            val space= context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp).toInt()
            when {
                parent.getChildLayoutPosition(view)==0 -> outRect.left= space
                parent.getChildLayoutPosition(view)== count - 1 -> {
                    outRect.right= space
                    outRect.left= space
                }
                else -> outRect.left= space
            }
        }
    }

}