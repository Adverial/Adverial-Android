package com.application.adverial.remote.model

data class CategoryDetail(
        val id: Int,
        val cat_key: String?,
        val name_en: String?,
        val name_tr: String?,
        val name_ar: String?,
        val name_ckb: String?,
        val name_kmr: String?,
        val parent_id: Int?,
        val type: Int?,
        val image: String?,
        val is_active: Boolean?,
        val created_at: String?,
        val updated_at: String?
)
