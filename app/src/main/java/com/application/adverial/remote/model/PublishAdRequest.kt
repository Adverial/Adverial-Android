
package com.application.adverial.remote.model

data class PublishAdRequest(
    val ad_id: String,
    val phone: String,
    val name: String,
    val type: String,
    val filePaths: ArrayList<String>
)