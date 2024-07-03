package com.application.adverial.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.adverial.remote.model.ErrorResponse
import com.application.adverial.remote.model.GenericResponse
import com.application.adverial.remote.model.VerifyOtpResponse
import com.application.adverial.service.Tools
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthRepository(val context: Context) {
    private val service: APIService = RetroClass().apiService()
    private val loginResponse = MutableLiveData<GenericResponse>()
    private val verifyResponse = MutableLiveData<VerifyOtpResponse>()
    private val signupResponse = MutableLiveData<GenericResponse>()
    private var currentLang = Tools().getCurrentLanguage(context)

    fun registerViaWa(name: String, whatsappNumber: String): LiveData<GenericResponse> {
        val call = service.registerViaWa(currentLang,"application/json", name, whatsappNumber)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    signupResponse.value = response.body()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val genericResponse = if (errorResponse != null) {
                        try {
                            Gson().fromJson(errorResponse, GenericResponse::class.java).copy(
                                error = Gson().fromJson(errorResponse, ErrorResponse::class.java).error
                            )
                        } catch (e: Exception) {
                            GenericResponse(error = "An unknown error occurred. Please try again later.")
                        }
                    } else {
                        GenericResponse(error = "An unknown error occurred. Please try again later.")
                    }
                    signupResponse.value = genericResponse
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                signupResponse.value = GenericResponse(error = "Failed to process your request. Please try again later.")
            }
        })
        return signupResponse
    }

    fun loginViaWa(whatsappNumber: String): LiveData<GenericResponse> {
        val call = service.loginViaWa(currentLang,"application/json", whatsappNumber)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    loginResponse.value = response.body()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = if (errorResponse != null) {
                        try {
                            val error = Gson().fromJson(errorResponse, ErrorResponse::class.java)
                            error.error
                        } catch (e: Exception) {
                            "An unknown error occurred. Please try again later."
                        }
                    } else {
                        "An unknown error occurred. Please try again later."
                    }
                    loginResponse.value = GenericResponse(message = "", error = errorMessage)
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                loginResponse.value = GenericResponse(message = "", error = "Failed to process your request. Please try again later.")
            }
        })
        return loginResponse
    }




    fun verifyOtpWa(whatsappNumber: String, otp: Int): LiveData<VerifyOtpResponse> {
        val call = service.verifyOtpWa(currentLang, "application/json", whatsappNumber, otp)
        call.enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    verifyResponse.value = response.body()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val verifyOtpResponse = if (errorResponse != null) {
                        try {
                            Gson().fromJson(errorResponse, VerifyOtpResponse::class.java)
                        } catch (e: Exception) {
                            VerifyOtpResponse(message = "An unknown error occurred. Please try again later.")
                        }
                    } else {
                        VerifyOtpResponse(message = "An unknown error occurred. Please try again later.")
                    }
                    verifyResponse.value = verifyOtpResponse
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                verifyResponse.value = VerifyOtpResponse(message = "Failed to process your request. Please try again later.")
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
    fun getSignupResponse(): LiveData<GenericResponse> {
        return signupResponse
    }
}