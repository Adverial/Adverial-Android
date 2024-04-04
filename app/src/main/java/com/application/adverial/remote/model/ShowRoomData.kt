package com.application.adverial.remote.model

data class ShowRoomData(
    var id: Int,
    var title: String?,
    var visibility: String,
    var price: String,
    var created_at: String,
    var ad_images: List<AdImage>?,
    var city_detail: CityDetails?,
    var district_detail: CityDetails?,
    var country_detail: CityDetails?,
    var price_currency: String?
)