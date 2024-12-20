package com.application.adverial.remote.model

import com.google.gson.annotations.SerializedName

data class GenericResponse(
    val message: String? = null,
    @SerializedName("whatsapp_number") val whatsappNumber: List<String>? = null,
    val error: String? = null,
    val status : Int? = null
)
data class ErrorResponse(
    val error: String,
    @SerializedName("whatsapp_number") val whatsappNumber: List<String>? = null

)