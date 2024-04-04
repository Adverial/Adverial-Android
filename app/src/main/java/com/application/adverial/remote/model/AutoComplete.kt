package com.application.adverial.remote.model

data class AutoComplete(
    val status: Boolean,
    val message: String?,
    val data: List<AutoCompleteData>?
)

data class AutoCompleteData(
    val title: String?
)