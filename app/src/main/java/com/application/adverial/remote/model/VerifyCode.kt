package com.application.adverial.remote.model

data class VerifyCode(
    var status: Boolean,
    var message: String?,
    val data: VerifyCodeData?
)

data class VerifyCodeData(
    val token: String?
)