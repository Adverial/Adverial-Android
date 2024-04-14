package com.application.adverial.remote

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.application.adverial.remote.model.AdDetails
import com.application.adverial.remote.model.AddAdInfo
import com.application.adverial.remote.model.AutoComplete
import com.application.adverial.remote.model.BackgroundResponseModel
import com.application.adverial.remote.model.Category
import com.application.adverial.remote.model.CategoryAds
import com.application.adverial.remote.model.CategoryOptions
import com.application.adverial.remote.model.City
import com.application.adverial.remote.model.Currency
import com.application.adverial.remote.model.Favorite
import com.application.adverial.remote.model.Filter
import com.application.adverial.remote.model.ForgotPasswordRequestModel
import com.application.adverial.remote.model.ImageUpload
import com.application.adverial.remote.model.LatestSearch
import com.application.adverial.remote.model.MainCategory
import com.application.adverial.remote.model.Notification
import com.application.adverial.remote.model.PublishAd
import com.application.adverial.remote.model.Search
import com.application.adverial.remote.model.ShowRoom
import com.application.adverial.remote.model.Signup
import com.application.adverial.remote.model.User
import com.application.adverial.remote.model.VerifyCode
import com.application.adverial.service.Tools
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File


class Repository(val context: Context) {

    private val token= context.getSharedPreferences("user", 0).getString("token", "")
    val mainCategory= MutableLiveData<MainCategory>()
    val categoryAds= MutableLiveData<CategoryAds>()
    val signup= MutableLiveData<Signup>()
    val login= MutableLiveData<Signup>()
    val category= MutableLiveData<Category>()
    val adDetails= MutableLiveData<AdDetails>()
    val showRoom= MutableLiveData<ShowRoom>()
    val search= MutableLiveData<Search>()
    val clearSearch= MutableLiveData<com.application.adverial.remote.model.Response>()
    val latestSearch= MutableLiveData<LatestSearch>()
    val favorite= MutableLiveData<Favorite>()
    val removeFavorite= MutableLiveData<com.application.adverial.remote.model.Response>()
    val addFavorite= MutableLiveData<com.application.adverial.remote.model.Response>()
    val activeAds= MutableLiveData<Search>()
    val deactiveAds= MutableLiveData<Search>()
    val removeAd= MutableLiveData<com.application.adverial.remote.model.Response>()
    val categoryOptions= MutableLiveData<CategoryOptions>()
    val addAdInfo= MutableLiveData<AddAdInfo>()
    val country= MutableLiveData<City>()
    val city= MutableLiveData<City>()
    val district= MutableLiveData<City>()
    val addAdLocation= MutableLiveData<com.application.adverial.remote.model.Response>()
    val image= MutableLiveData<ImageUpload>()
    val publishAd= MutableLiveData<PublishAd>()
    val user= MutableLiveData<User>()
    val userUpdate= MutableLiveData<com.application.adverial.remote.model.Response>()
    val deleteImage= MutableLiveData<com.application.adverial.remote.model.Response>()
    val notification= MutableLiveData<Notification>()
    val contactUs= MutableLiveData<com.application.adverial.remote.model.Response>()
    val currency= MutableLiveData<Currency>()
    val filter= MutableLiveData<Filter>()
    val adFeedback= MutableLiveData<com.application.adverial.remote.model.Response>()
    val deleteAccount= MutableLiveData<com.application.adverial.remote.model.Response>()
    val verifyCode= MutableLiveData<VerifyCode>()
    val sendCode= MutableLiveData<com.application.adverial.remote.model.Response>()
    val forgotPassword= MutableLiveData<com.application.adverial.remote.model.Response>()
    val autoComplete= MutableLiveData<AutoComplete>()

    var currentLang = Tools().getCurrentLanguage(context)

    fun mainCategory(){
        val service: APIService = RetroClass().apiService()
        val call = service.mainCategory(currentLang)
        call.enqueue(object : retrofit2.Callback<MainCategory> {
            override fun onResponse(call: Call<MainCategory>, response: Response<MainCategory>) {
                if(response.body() != null) mainCategory.value = response.body()
            }
            override fun onFailure(call: Call<MainCategory>, t: Throwable) {}
        })
    }

    fun categoryAds(id: String, type: String, page: Int){
        val service: APIService = RetroClass().apiService()
        val call = service.categoryAds(currentLang,"ad-list/$id?page=$page", type)
        call.enqueue(object : retrofit2.Callback<CategoryAds> {
            override fun onResponse(call: Call<CategoryAds>, response: Response<CategoryAds>) { categoryAds.value = response.body() }
            override fun onFailure(call: Call<CategoryAds>, t: Throwable) {}
        })
    }

    fun signup(firstName: String, lastName: String, email: String, password: String, phone: String){
        val service: APIService = RetroClass().apiService()
        val call = service.signup("application/json",currentLang, email, password, firstName, lastName, phone)
        call.enqueue(object : retrofit2.Callback<Signup> {
            override fun onResponse(call: Call<Signup>, response: Response<Signup>) { signup.value = response.body() }
            override fun onFailure(call: Call<Signup>, t: Throwable) {  }
        })
    }

    fun login(email: String, password: String){
        val service: APIService = RetroClass().apiService()
        val call = service.login("application/json",currentLang, email, password)
        call.enqueue(object : retrofit2.Callback<Signup> {
            override fun onResponse(call: Call<Signup>, response: Response<Signup>) { login.value = response.body() }
            override fun onFailure(call: Call<Signup>, t: Throwable) {  }
        })
    }

    fun verifyCode(email: String, code: String){
        val service: APIService = RetroClassV2().apiService()
        val call = service.verifyCode("application/json",currentLang, email, code)
        call.enqueue(object : retrofit2.Callback<VerifyCode> {
            override fun onResponse(call: Call<VerifyCode>, response: Response<VerifyCode>) { verifyCode.value = response.body() }
            override fun onFailure(call: Call<VerifyCode>, t: Throwable) {  }
        })
    }

    fun sendCode(email: String){
        val service: APIService = RetroClassV2().apiService()
        val call = service.sendCode("application/json",currentLang, email)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { sendCode.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {  }
        })
    }

    fun category(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.category(currentLang,"get-category/$id")
        call.enqueue(object : retrofit2.Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) { category.value = response.body() }
            override fun onFailure(call: Call<Category>, t: Throwable) {}
        })
    }

    fun adDetails(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.adDetails(currentLang,"ad-detail/$id", "Bearer $token")
        call.enqueue(object : retrofit2.Callback<AdDetails> {
            override fun onResponse(call: Call<AdDetails>, response: Response<AdDetails>) { adDetails.value = response.body() }
            override fun onFailure(call: Call<AdDetails>, t: Throwable) {}
        })
    }

  fun showRoom(type: String, page: Int){

    val service: APIService = RetroClassV2().apiService()
    val call = service.showRoom(currentLang,type, "look-showroom?page=$page&type=0")
    call.enqueue(object : retrofit2.Callback<ShowRoom> {
        override fun onResponse(call: Call<ShowRoom>, response: Response<ShowRoom>) {
            showRoom.value = response.body()

        }
        override fun onFailure(call: Call<ShowRoom>, t: Throwable) {
//            Log.e("KEY",t.localizedMessage!!)
        }
    })
}

    fun search(keyword: String, type: String){
        val service: APIService = RetroClass().apiService()
        val call = service.search("Bearer $token",currentLang, keyword, type)
        call.enqueue(object : retrofit2.Callback<Search> {
            override fun onResponse(call: Call<Search>, response: Response<Search>) { search.value = response.body() }
            override fun onFailure(call: Call<Search>, t: Throwable) {}
        })
    }

    fun clearSearch(){
        val service: APIService = RetroClass().apiService()
        val call = service.clearSearch("Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { clearSearch.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun latestSearch(){
        val service: APIService = RetroClass().apiService()
        val call = service.latestSearch("Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<LatestSearch> {
            override fun onResponse(call: Call<LatestSearch>, response: Response<LatestSearch>) { latestSearch.value = response.body() }
            override fun onFailure(call: Call<LatestSearch>, t: Throwable) {}
        })
    }

    fun favorite(){
        val service: APIService = RetroClassV2().apiService()
        val call = service.favorite("Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<Favorite> {
            override fun onResponse(call: Call<Favorite>, response: Response<Favorite>) { favorite.value = response.body() }
            override fun onFailure(call: Call<Favorite>, t: Throwable) {}
        })
    }

    fun removeFavorite(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.removeFavorite("Bearer $token",currentLang, id)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { removeFavorite.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun addFavorite(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.addFavorite("Bearer $token",currentLang, id)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { addFavorite.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun activeAds(){
        val service: APIService = RetroClass().apiService()
        val call = service.activeAds("Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<Search> {
            override fun onResponse(call: Call<Search>, response: Response<Search>) { activeAds.value = response.body() }
            override fun onFailure(call: Call<Search>, t: Throwable) {}
        })
    }

    fun deactiveAds(){
        val service: APIService = RetroClass().apiService()
        val call = service.deactiveAds("Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<Search> {
            override fun onResponse(call: Call<Search>, response: Response<Search>) { deactiveAds.value = response.body() }
            override fun onFailure(call: Call<Search>, t: Throwable) {}
        })
    }

    fun removeAd(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.removeAd("ad-delete/$id", "Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { removeAd.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun categoryOptions(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.categoryOptions(currentLang,"get-option/$id")
        call.enqueue(object : retrofit2.Callback<CategoryOptions> {
            override fun onResponse(call: Call<CategoryOptions>, response: Response<CategoryOptions>) { categoryOptions.value = response.body() }
            override fun onFailure(call: Call<CategoryOptions>, t: Throwable) {}
        })
    }

    fun addAdInfo(title: String, price: String, desc: String, idArray: String, currency: String){
        val data= context.getSharedPreferences("newAdOptions", 0)
        if(data.all.isNotEmpty()){
            val jsonArray = JSONArray()
            val allEntries: Map<String, *> = data.all
            for ((key, value) in allEntries) {
                val jsonObj = JSONObject().put("option_id", key).put("value", value.toString())
                jsonArray.put(jsonObj)
            }
            val jsonObj1 = JSONObject().put("options", jsonArray)
            val service: APIService = RetroClass().apiService()
            val call = service.addAdInfoOptions("Bearer $token",currentLang, title, price, desc, idArray, jsonObj1.toString(), currency)
            call.enqueue(object : retrofit2.Callback<AddAdInfo> {
                override fun onResponse(call: Call<AddAdInfo>, response: Response<AddAdInfo>) { addAdInfo.value = response.body() }
                override fun onFailure(call: Call<AddAdInfo>, t: Throwable) {}
            })
        }else{
            val service: APIService = RetroClass().apiService()
            val call = service.addAdInfo("Bearer $token",currentLang, title, price, desc, idArray, currency)
            call.enqueue(object : retrofit2.Callback<AddAdInfo> {
                override fun onResponse(call: Call<AddAdInfo>, response: Response<AddAdInfo>) { addAdInfo.value = response.body() }
                override fun onFailure(call: Call<AddAdInfo>, t: Throwable) {}
            })
        }
    }

    fun country(){
        val service: APIService = RetroClass().apiService()
        val call = service.country(currentLang,"get-countries")
        call.enqueue(object : retrofit2.Callback<City> {
            override fun onResponse(call: Call<City>, response: Response<City>) { country.value = response.body() }
            override fun onFailure(call: Call<City>, t: Throwable) {}
        })
    }

    fun city(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.city(currentLang,"get-cities/$id")
        call.enqueue(object : retrofit2.Callback<City> {
            override fun onResponse(call: Call<City>, response: Response<City>) { city.value = response.body() }
            override fun onFailure(call: Call<City>, t: Throwable) {}
        })
    }

    fun district(id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.district(currentLang,"get-districts/$id")
        call.enqueue(object : retrofit2.Callback<City> {
            override fun onResponse(call: Call<City>, response: Response<City>) { district.value = response.body() }
            override fun onFailure(call: Call<City>, t: Throwable) {}
        })
    }

    fun addAdLocation(ad_id: String, country_id: String, city_id: String, district_id: String, lat: String, lon: String){
        val service: APIService = RetroClass().apiService()
        val call = service.addAdLocation("Bearer $token",currentLang, ad_id, country_id, city_id, district_id, lat, lon)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { addAdLocation.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun image(picture: String, adId: String){
        val service: APIService = RetroClass().apiService()
        val file = File(picture)
        val requestBody= RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file)
        val part= MultipartBody.Part.createFormData("image", file.name, requestBody)
        val ad_id= RequestBody.create(okhttp3.MediaType.parse("text/plane"), adId)
        val call = service.image( "Bearer $token",currentLang, ad_id, part)
        call.enqueue(object : retrofit2.Callback<ImageUpload> {
            override fun onResponse(call: Call<ImageUpload>, response: Response<ImageUpload>) {
                image.value= response.body()
            }
            override fun onFailure(call: Call<ImageUpload>, t: Throwable) {}
        })
    }

    fun deleteImage(image_id: String){
        val service: APIService = RetroClass().apiService()
        val call = service.deleteImage( "Bearer $token",currentLang, image_id)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) {
                deleteImage.value= response.body()
            }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun publishAd(ad_id: String, phone: String, name: String, type: String){
        val service: APIService = RetroClass().apiService()
        val call = service.publishAd("Bearer $token",currentLang, ad_id, phone, name, type)
        call.enqueue(object : retrofit2.Callback<PublishAd> {
            override fun onResponse(call: Call<PublishAd>, response: Response<PublishAd>) { publishAd.value = response.body() }
            override fun onFailure(call: Call<PublishAd>, t: Throwable) {}
        })
    }

    fun user(){
        val service: APIService = RetroClass().apiService()
        val call = service.user("Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) { user.value = response.body() }
            override fun onFailure(call: Call<User>, t: Throwable) {}
        })
    }

    fun userUpdate(name: String, last_name: String, email: String, phone: String){
        val service: APIService = RetroClass().apiService()
        val call = service.userUpdate("Bearer $token",currentLang, name, last_name, email, phone)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { userUpdate.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun notification(){
        val service: APIService = RetroClass().apiService()
        val call = service.notification("Bearer $token",currentLang)
        call.enqueue(object : retrofit2.Callback<Notification> {
            override fun onResponse(call: Call<Notification>, response: Response<Notification>) { notification.value = response.body() }
            override fun onFailure(call: Call<Notification>, t: Throwable) {}
        })
    }

    fun contactUs(message: String){
        val service: APIService = RetroClass().apiService()
        val call = service.contactUs("Bearer $token", currentLang,message)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { contactUs.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun currency(){
        val service: APIService = RetroClass().apiService()
        val call = service.currency(currentLang)
        call.enqueue(object : retrofit2.Callback<Currency> {
            override fun onResponse(call: Call<Currency>, response: Response<Currency>) { currency.value = response.body() }
            override fun onFailure(call: Call<Currency>, t: Throwable) {}
        })
    }

    fun filterCategory(category_id: String, json: String){
        val service: APIService = RetroClass().apiService()
        val call = service.filterCategory(currentLang,"filter/$category_id", json)
        call.enqueue(object : retrofit2.Callback<Filter> {
            override fun onResponse(call: Call<Filter>, response: Response<Filter>) { filter.value = response.body() }
            override fun onFailure(call: Call<Filter>, t: Throwable) {}
        })
    }

    fun adFeedback(ad_id: String, message: String){
        val service: APIService = RetroClass().apiService()
        val call = service.adFeedback("Bearer $token",currentLang, ad_id, message)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { adFeedback.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun deleteAccount(){
        val service: APIService = RetroClass().apiService()
        val call = service.deleteAccount("Bearer $token")
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response> {
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) { deleteAccount.value = response.body() }
            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {}
        })
    }

    fun forgotPassword(requestModel: ForgotPasswordRequestModel){
        val service: APIService = RetroClass().apiService()
        val call = service.forgotPassword(requestModel)
        call.enqueue(object : retrofit2.Callback<com.application.adverial.remote.model.Response>{
            override fun onResponse(call: Call<com.application.adverial.remote.model.Response>, response: Response<com.application.adverial.remote.model.Response>) {
               if (response.isSuccessful){
                   if (response.body() != null) forgotPassword.value = response.body()
               }
            }

            override fun onFailure(call: Call<com.application.adverial.remote.model.Response>, t: Throwable) {
//               Log.e("KEY",t.localizedMessage!!)
            }
        })
    }

    val backgroundLV= MutableLiveData<BackgroundResponseModel>()
    fun getBackground(){
        val service: APIService = RetroClass().apiService()
        val call = service.background()
        call.enqueue(object : retrofit2.Callback<BackgroundResponseModel>{
            override fun onResponse(
                call: Call<BackgroundResponseModel>,
                response: Response<BackgroundResponseModel>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null){
                    backgroundLV.value = response.body()
                }
            }
            override fun onFailure(call: Call<BackgroundResponseModel>, t: Throwable) {
//                Log.e("KEY",t.localizedMessage!!)
            }
        })
    }

    fun adAutoComplete(keyword: String){
        val service: APIService = RetroClassV2().apiService()
        val call = service.adAutoComplete("Bearer $token", currentLang, keyword)
        call.enqueue(object : retrofit2.Callback<AutoComplete>{
            override fun onResponse(call: Call<AutoComplete>, response: Response<AutoComplete>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null){
                    autoComplete.value = response.body()
                }
            }
            override fun onFailure(call: Call<AutoComplete>, t: Throwable) {
//                Log.e("KEY",t.localizedMessage!!)
            }
        })
    }

    fun categoryAutoComplete(keyword: String){
        val service: APIService = RetroClassV2().apiService()
        val call = service.categoryAutoComplete("Bearer $token", currentLang, keyword)
        call.enqueue(object : retrofit2.Callback<AutoComplete>{
            override fun onResponse(call: Call<AutoComplete>, response: Response<AutoComplete>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null){
                    autoComplete.value = response.body()
                }
            }
            override fun onFailure(call: Call<AutoComplete>, t: Throwable) {
//                Log.e("KEY",t.localizedMessage!!)
            }
        })
    }

    fun getMainCategoryData():MutableLiveData<MainCategory>{ return mainCategory }
    fun getCategoryAdsData():MutableLiveData<CategoryAds>{ return categoryAds }
    fun getSignupData():MutableLiveData<Signup>{ return signup }
    fun getLoginData():MutableLiveData<Signup>{ return login }
    fun getCategoryData():MutableLiveData<Category>{ return category }
    fun getAdDetailsData():MutableLiveData<AdDetails>{ return adDetails }
    fun getShowRoomData():MutableLiveData<ShowRoom>{ return showRoom }
    fun getSearchData():MutableLiveData<Search>{ return search }
    fun getClearSearchData():MutableLiveData<com.application.adverial.remote.model.Response>{ return clearSearch }
    fun getLatestSearchData():MutableLiveData<LatestSearch>{ return latestSearch }
    fun getFavoriteData():MutableLiveData<Favorite>{ return favorite }
    fun getRemoveFavoriteData():MutableLiveData<com.application.adverial.remote.model.Response>{ return removeFavorite }
    fun getAddFavoriteData():MutableLiveData<com.application.adverial.remote.model.Response>{ return addFavorite }
    fun getActiveAdsData():MutableLiveData<Search>{ return activeAds }
    fun getDeactiveAdsData():MutableLiveData<Search>{ return deactiveAds }
    fun getRemoveAdData():MutableLiveData<com.application.adverial.remote.model.Response>{ return removeAd }
    fun getCategoryOptionsData():MutableLiveData<CategoryOptions>{ return categoryOptions }
    fun getAddAdInfoData():MutableLiveData<AddAdInfo>{ return addAdInfo }
    fun getCountryData():MutableLiveData<City>{ return country }
    fun getCityData():MutableLiveData<City>{ return city }
    fun getDistrictData():MutableLiveData<City>{ return district }
    fun getAddAdLocationData():MutableLiveData<com.application.adverial.remote.model.Response>{ return addAdLocation }
    fun getImageData():MutableLiveData<ImageUpload>{ return image }
    fun getPublishAdData():MutableLiveData<PublishAd>{ return publishAd }
    fun getUserData():MutableLiveData<User>{ return user }
    fun getUserUpdateData():MutableLiveData<com.application.adverial.remote.model.Response>{ return userUpdate }
    fun getDeleteImageData():MutableLiveData<com.application.adverial.remote.model.Response>{ return deleteImage }
    fun getNotificationData():MutableLiveData<Notification>{ return notification }
    fun getContactUsData():MutableLiveData<com.application.adverial.remote.model.Response>{ return contactUs }
    fun getCurrencyData():MutableLiveData<Currency>{ return currency }
    fun getFilterCategoryData():MutableLiveData<Filter>{ return filter }
    fun getAdFeedbackData():MutableLiveData<com.application.adverial.remote.model.Response>{ return adFeedback }
}