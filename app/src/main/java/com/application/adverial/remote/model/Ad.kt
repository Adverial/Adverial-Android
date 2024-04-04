package com.application.adverial.remote.model

data class Ad(var id: Int, var title: String?, var user_id: String?, var option_type: String?, var visibility: String?, var created_at: String?,
              var price: String?, var description: String?, var is_favorite: Int?
              , var city_detail: CityDetails?, var district_detail: CityDetails?, var country_detail: CityDetails?, var ad_options: List<AdOption>?
              , var user_detail: UserDetail?, var lat: String?, var lon: String?, var user_name: String?, var ad_images: List<AdImage>?, var phone: String?, var type: String?, var price_currency: String?)