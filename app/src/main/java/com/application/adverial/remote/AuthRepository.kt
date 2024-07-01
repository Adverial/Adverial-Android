package com.application.adverial.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.adverial.remote.model.GenericResponse
import com.application.adverial.remote.model.VerifyOtpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthRepository(val context: Context) {
    private val service: APIService = RetroClass().apiService()

    fun registerViaWa(name: String, lastName: String?, whatsappNumber: String): LiveData<GenericResponse> {
        val responseLiveData = MutableLiveData<GenericResponse>()
        val call = service.registerViaWa("application/json", name, lastName, whatsappNumber)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                responseLiveData.value = response.body()
            }
            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                // Handle failure
            }
        })
        return responseLiveData
    }

    fun loginViaWa(whatsappNumber: String): LiveData<GenericResponse> {
        val responseLiveData = MutableLiveData<GenericResponse>()
        val call = service.loginViaWa("application/json", whatsappNumber)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                responseLiveData.value = response.body()
            }
            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                // Handle failure
            }
        })
        return responseLiveData
    }

    fun resendOtpWa(whatsappNumber: String): LiveData<GenericResponse> {
        val responseLiveData = MutableLiveData<GenericResponse>()
        val call = service.resendOtpWa("application/json", whatsappNumber)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                responseLiveData.value = response.body()
            }
            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                // Handle failure
            }
        })
        return responseLiveData
    }

    fun verifyOtpWa(whatsappNumber: String, otp: Int): LiveData<VerifyOtpResponse> {
        val responseLiveData = MutableLiveData<VerifyOtpResponse>()
        val call = service.verifyOtpWa("application/json", whatsappNumber, otp)
        call.enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                responseLiveData.value = response.body()
            }
            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                // Handle failure
            }
        })
        return responseLiveData
    }
}