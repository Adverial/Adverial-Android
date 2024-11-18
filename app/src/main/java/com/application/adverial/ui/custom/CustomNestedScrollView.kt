package com.application.adverial.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class CustomNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    private var isMapTouched = false
    private var isSingleTouchActive = false
    private var lastTouchY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isSingleTouchActive = true
                lastTouchY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val yDiff = Math.abs(ev.y - lastTouchY)
                if (isMapTouched || yDiff < 10) {
                    // If map is touched or minimal scrolling has occurred, don't intercept the event
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isSingleTouchActive = false
            }
        }
        return if (isMapTouched || isSingleTouchActive) {
            false
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isSingleTouchActive = true
            }
            MotionEvent.ACTION_MOVE -> {
                val yDiff = Math.abs(ev.y - lastTouchY)
                if (isMapTouched || yDiff < 10) {
                    // Allow map interaction if it's being touched or scrolling is minimal
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isSingleTouchActive = false
            }
        }

        return if (isMapTouched || isSingleTouchActive) {
            false
        } else {
            super.onTouchEvent(ev)
        }
    }

    fun setMapTouched(touched: Boolean) {
        isMapTouched = touched
    }
}
