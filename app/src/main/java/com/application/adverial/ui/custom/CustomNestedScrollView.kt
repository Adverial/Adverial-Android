package com.application.adverial.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class CustomNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    private var isMapTouched = false
    private var isMultiTouch = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // Allow the scroll view to intercept unless the map is being touched or there are multiple touch points
        return if (isMapTouched || isMultiTouch) {
            false
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        // Handle multi-touch events separately (like zooming)
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> isMultiTouch = false
            MotionEvent.ACTION_POINTER_DOWN -> isMultiTouch = true
            MotionEvent.ACTION_POINTER_UP -> isMultiTouch = false
            MotionEvent.ACTION_UP -> isMapTouched = false
        }

        return if (isMapTouched || isMultiTouch) {
            // If map is being interacted with, don't handle the touch event here
            false
        } else {
            super.onTouchEvent(ev)
        }
    }

    fun setMapTouched(touched: Boolean) {
        isMapTouched = touched
    }
}
