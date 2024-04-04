package com.application.adverial.service

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import com.application.adverial.utils.Constants
import com.application.adverial.utils.Constants.BACKGROUND_NAME
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson

class SharedPrefManager(private val context : Context ) {


    private val sharedPreferences : SharedPreferences by lazy {
       context.getSharedPreferences(Constants.BACKGROUND_PREF,Context.MODE_PRIVATE)
    }

    fun saveBackground(image : String){
        with(sharedPreferences.edit()){
            putString(BACKGROUND_NAME,image)
            apply()
        }
    }

    fun getBackground() = sharedPreferences.getString(BACKGROUND_NAME,"")


    fun deleteBackground() = sharedPreferences.edit().remove(BACKGROUND_NAME).apply()


}