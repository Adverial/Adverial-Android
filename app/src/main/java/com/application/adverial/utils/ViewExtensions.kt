package com.application.adverial.utils

import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat


fun View.changeBackgroundRes(@DrawableRes res: Int){
    background = ContextCompat.getDrawable(this.context,res)
}