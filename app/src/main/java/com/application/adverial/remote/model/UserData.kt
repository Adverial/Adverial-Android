package com.application.adverial.remote.model

import com.google.gson.annotations.SerializedName

data class UserData(
    var name: String?,
    var last_name: String?,
    var email: String?,
    var phone: String?,
    @SerializedName("whatsapp_number") val whatsappNumber: String? ,
)