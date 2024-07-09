package com.application.adverial.remote.model

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    val data: VerifyOtpData? = null,
    val message: String? = null,
    @SerializedName("whatsapp_number") val whatsappNumber: List<String>? = null,
    @SerializedName("otp") val otp: List<String>? = null
)

data class VerifyOtpData(
    val token: String
)
