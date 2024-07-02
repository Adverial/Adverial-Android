package com.application.adverial.remote.model

data class VerifyOtpResponse(
    val token: String?,
    val message: String,
    val data:String?
)
