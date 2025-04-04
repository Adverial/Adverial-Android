package com.application.adverial.remote

import com.application.adverial.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetroClassV2 {

    private val basePath= BuildConfig.API_BASE_URL+"api/v2/"
    private fun retrofitInstance():Retrofit{
        val okhttp=OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build()
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        val gson = gsonBuilder.create()
        return Retrofit.Builder().baseUrl(basePath).client(okhttp).addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    fun apiService(): APIService {
        return retrofitInstance().create(APIService::class.java)
    }
}