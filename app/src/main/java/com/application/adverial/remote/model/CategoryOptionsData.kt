package com.application.adverial.remote.model

import com.google.gson.annotations.SerializedName

data class CategoryOptionsData(
        @SerializedName("id") val id: Int,
        @SerializedName("ad_type") val ad_type: Int,
        @SerializedName("type") val type: String,
        @SerializedName("unit") val unit: String?,
        @SerializedName("title") val title: String,
        @SerializedName("form_type") val form_type: Int,
        @SerializedName("values") val values: String?
)
