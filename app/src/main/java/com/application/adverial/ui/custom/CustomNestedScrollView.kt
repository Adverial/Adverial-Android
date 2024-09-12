package com.application.adverial.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class CustomNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    private var isMapTouched = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // When the map is being touched, prevent the NestedScrollView from intercepting the touch events
        return if (isMapTouched) {
            false
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        // Allow touch events to be passed to the map
        return if (isMapTouched) {
            false
        } else {
            super.onTouchEvent(ev)
        }
    }

    fun setMapTouched(touched: Boolean) {
        isMapTouched = touched
    }
}
