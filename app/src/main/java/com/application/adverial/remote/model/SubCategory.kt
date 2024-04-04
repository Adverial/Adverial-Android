package com.application.adverial.remote.model

data class SubCategory(var id: Int, var name: String?, var type: String?, var image: String?, var total_ad_count: String?, var parent_categories: List<SubCategory>)