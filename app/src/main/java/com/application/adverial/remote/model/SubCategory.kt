package com.application.adverial.remote.model

data class SubCategory(
        var id: String,
        var position: Int,
        var name: String,
        var type: String,
        var parent_id: Int? = null,
        var image: String? = null,
        var total_ad_count: String?,
        var parent_categories: List<SubCategory> = listOf()
)
