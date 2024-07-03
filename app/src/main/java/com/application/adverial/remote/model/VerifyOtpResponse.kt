package com.application.adverial.remote.model

data class VerifyOtpResponseData(
    val token: String?
)

data class VerifyOtpResponse(
    val data: VerifyOtpResponseData?,
    val message: String
)
