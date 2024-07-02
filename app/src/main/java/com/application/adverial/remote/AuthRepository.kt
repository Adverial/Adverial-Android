package com.application.adverial.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.adverial.remote.model.GenericResponse
import com.application.adverial.remote.model.VerifyOtpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthRepository(val context: Context) {
    private val service: APIService = RetroClass().apiService()
    private val loginResponse = MutableLiveData<GenericResponse>()
    private val verifyResponse = MutableLiveData<VerifyOtpResponse>()
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
        val call = service.loginViaWa("application/json", whatsappNumber)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body() != null){
                    loginResponse.value = response.body()
                } else {
                    loginResponse.value = GenericResponse("please check your phone number if associated with whatsapp number")
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {

            }
        })
        return loginResponse
    }

    fun verifyOtpWa(whatsappNumber: String, otp: Int): LiveData<VerifyOtpResponse> {
        val call = service.verifyOtpWa("application/json", whatsappNumber, otp)
        call.enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                if (response.isSuccessful && response.body() != null){
                    verifyResponse.value = response.body()
                } else {
                    verifyResponse.value = VerifyOtpResponse(token = null, message = "Invalid OTP", response.body()?.data)
                }

            }
            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                // Handle failure
            }
        })
        return verifyResponse
    }

    fun resendOtpWa(whatsappNumber: String): LiveData<GenericResponse> {
        val call = service.resendOtpWa("application/json", whatsappNumber)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                loginResponse.value = response.body()
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                // Handle failure
            }
        })
        return loginResponse
    }

    fun getLoginResponse(): LiveData<GenericResponse> {
        return loginResponse
    }

    fun getVerifyResponse(): LiveData<VerifyOtpResponse> {
        return verifyResponse
    }
}