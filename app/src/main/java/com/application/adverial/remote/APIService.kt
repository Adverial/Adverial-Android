package com.application.adverial.remote

import com.application.adverial.remote.model.AdDetails
import com.application.adverial.remote.model.AddAdInfo
import com.application.adverial.remote.model.AutoComplete
import com.application.adverial.remote.model.BackgroundResponseModel
import com.application.adverial.remote.model.Category
import com.application.adverial.remote.model.CategoryAds
import com.application.adverial.remote.model.CategoryOptions
import com.application.adverial.remote.model.City
import com.application.adverial.remote.model.Conversation
import com.application.adverial.remote.model.ConversationResponse
import com.application.adverial.remote.model.Currency
import com.application.adverial.remote.model.Favorite
import com.application.adverial.remote.model.Filter
import com.application.adverial.remote.model.ForgotPasswordRequestModel
import com.application.adverial.remote.model.GenericResponse
import com.application.adverial.remote.model.ImageUpload
import com.application.adverial.remote.model.LatestSearch
import com.application.adverial.remote.model.MainCategory
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.remote.model.Notification
import com.application.adverial.remote.model.PublishAd
import com.application.adverial.remote.model.Response
import com.application.adverial.remote.model.Search
import com.application.adverial.remote.model.ShowRoom
import com.application.adverial.remote.model.Signup
import com.application.adverial.remote.model.User
import com.application.adverial.remote.model.VerifyCode
import com.application.adverial.remote.model.VerifyOtpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface APIService {

    @FormUrlEncoded
    @POST("register-via-wa")
    fun registerViaWa(
        @Header("lang") lang: String,
        @Header("Accept") accept: String,
        @Field("name") name: String,
        @Field("whatsapp_number") whatsappNumber: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("login-via-wa")
    fun loginViaWa(
        @Header("lang") lang: String,
        @Header("Accept") accept: String,
        @Field("whatsapp_number") whatsappNumber: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("resend-otp-wa")
    fun resendOtpWa(
        @Header("Accept") accept: String,
        @Field("whatsapp_number") whatsappNumber: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("verify-otp-wa")
    fun verifyOtpWa(
        @Header("lang") lang: String,
        @Header("Accept") accept: String,
        @Field("whatsapp_number") whatsappNumber: String,
        @Field("otp") otp: Int
    ): Call<VerifyOtpResponse>



    @GET("main-categories")
    fun mainCategory(
        @Header("lang") lang: String
    ): Call<MainCategory>

    @FormUrlEncoded
    @POST
    fun categoryAds(
        @Header("lang") lang: String,
        @Url url: String,
        @Field("type") type: String
    ): Call<CategoryAds>

    @FormUrlEncoded
    @POST("register")
    fun signup(
        @Header("Accept") accept: String,
        @Header("lang") lang: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("last_name") last_name: String,
        @Field("phone") phone: String
    ): Call<Signup>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Header("Accept") accept: String,
        @Header("lang") lang: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Signup>

    @FormUrlEncoded
    @POST("verify-code")
    fun verifyCode(
        @Header("Accept") accept: String,
        @Header("lang") lang: String,
        @Field("email") email: String,
        @Field("code") code: String
    ): Call<VerifyCode>

    @FormUrlEncoded
    @POST("resend-code")
    fun sendCode(
        @Header("Accept") accept: String,
        @Header("lang") lang: String,
        @Field("email") email: String,
    ): Call<Response>

    @GET
    fun category(
        @Header("lang") lang: String,
        @Url url: String
    ): Call<Category>

    @GET
    fun adDetails(
        @Header("lang") lang: String,
        @Url url: String,
        @Header("Authorization") token: String
    ): Call<AdDetails>

    @FormUrlEncoded
    @POST
    fun showRoom(
        @Header("lang") lang: String,
        @Field("type") type: String,
        @Url url: String
    ): Call<ShowRoom>

    @FormUrlEncoded
    @POST("ad-search")
    fun search(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("keyword") keyword: String,
        @Field("type") type: String
    ): Call<Search>

    @DELETE("all-latest-search-delete")
    fun clearSearch(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
    ): Call<Response>

    @GET("latest-search-keyword")
    fun latestSearch(
        @Header("Authorization") token: String,
        @Header("lang") lang: String
        ): Call<LatestSearch>

    @GET("favorite-ads")
    fun favorite(
        @Header("Authorization") token: String,
        @Header("lang") lang: String
    ): Call<Favorite>

    @FormUrlEncoded
    @POST("remove-favorite")
    fun removeFavorite(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("ad_id") ad_id: String
    ): Call<Response>

    @FormUrlEncoded
    @POST("add-favorite")
    fun addFavorite(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("ad_id") ad_id: String
    ): Call<Response>

    @GET("active-ads")
    fun activeAds(
        @Header("Authorization") token: String,
        @Header("lang") lang: String
        ): Call<Search>

    @GET("deactive-ads")
    fun deactiveAds(
        @Header("Authorization") token: String,
        @Header("lang") lang: String
    ): Call<Search>

    @DELETE
    fun removeAd(
        @Url url: String,
        @Header("Authorization") token: String,
        @Header("lang") lang: String
    ): Call<Response>

    @FormUrlEncoded
    @POST("ad-create")
    fun addAdInfo(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("title") title: String,
        @Field("price") price: String,
        @Field("description") description: String,
        @Field("categories") categories: String,
        @Field("currency") currency: String
    ): Call<AddAdInfo>

    @FormUrlEncoded
    @POST("ad-create")
    fun addAdInfoOptions(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("title") title: String,
        @Field("price") price: String,
        @Field("description") description: String,
        @Field("categories") categories: String,
        @Field("options") options: String,
        @Field("currency") currency: String
    ): Call<AddAdInfo>

    @GET
    fun categoryOptions(
        @Header("lang") lang: String,
        @Url url: String
    ): Call<CategoryOptions>

    @GET
    fun country(
        @Header("lang") lang: String,
        @Url url: String
    ): Call<City>

    @GET
    fun city(
        @Header("lang") lang: String,
        @Url url: String
    ): Call<City>

    @GET
    fun district(
        @Header("lang") lang: String,
        @Url url: String
    ): Call<City>

    @FormUrlEncoded
    @POST("ad-create-2")
    fun addAdLocation(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("ad_id") ad_id: String,
        @Field("country_id") country_id: String,
        @Field("city_id") city_id: String,
        @Field("district_id") district_id: String,
        @Field("lat") lat: String,
        @Field("lon") lon: String
    ): Call<Response>

    @Multipart
    @POST("ad-create-3")
    fun image(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Part("ad_id") ad_id: RequestBody,
        @Part() image: MultipartBody.Part
    ): Call<ImageUpload>

    @FormUrlEncoded
    @POST("image-delete")
    fun deleteImage(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("image_id") image_id: String
    ): Call<Response>

    @FormUrlEncoded
    @POST("publish-ad")
    fun publishAd(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("ad_id") ad_id: String,
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("type") type: String
    ): Call<PublishAd>

    @GET("user-detail")
    fun user(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
    ): Call<User>

    @FormUrlEncoded
    @POST("user-detail")
    fun userUpdate(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("name") name: String,
        @Field("last_name") last_name: String,
        @Field("email") email: String,
        @Field("phone") phone: String
    ): Call<Response>

    @GET("notifications")
    fun notification(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
    ): Call<Notification>

    @FormUrlEncoded
    @POST("send-contact-message")
    fun contactUs(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("message") name: String
    ): Call<Response>

    @GET("get-currencies")
    fun currency(
        @Header("lang") lang: String
        ): Call<Currency>

    @FormUrlEncoded
    @POST
    fun filterCategory(
        @Header("lang") lang: String,
        @Url url: String,
        @Field("filter") filter: String
    ): Call<Filter>

    @FormUrlEncoded
    @POST("ad-feedback")
    fun adFeedback(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("ad_id") ad_id: String,
        @Field("message") message: String
    ): Call<Response>

    @DELETE("delete-account")
    fun deleteAccount(
        @Header("Authorization") token: String,
    ): Call<Response>


    @POST("forgot-password")
    fun forgotPassword(
        @Body requestModel: ForgotPasswordRequestModel
    ) : Call<Response>

    @GET("backgrounds")
    fun background() : Call<BackgroundResponseModel>

    @FormUrlEncoded
    @POST("ad-search-autocomplete")
    fun adAutoComplete(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("keyword") keyword: String,
    ) : Call<AutoComplete>

    @FormUrlEncoded
    @POST("category-search-autocomplete")
    fun categoryAutoComplete(
        @Header("Authorization") token: String,
        @Header("lang") lang: String,
        @Field("keyword") keyword: String,
    ) : Call<AutoComplete>

    @POST("initial-conversation/{partner_user_id}")
    fun initialConversation(
        @Path("partner_user_id") partnerUserId: Int,
        @Query("ad_id") adId: Int?,
        @Header("Authorization") authorization: String,
        @Header("lang") lang: String
    ): Call<ConversationResponse>

    @GET("user-conversations")
    fun getUserConversations(
        @Header("Authorization") authorization: String,
        @Header("content-type") contentType: String,
        @Header("lang") lang: String
    ): Call<List<Conversation>>

    @POST("send-message/{conversionId}")
    @Multipart
    fun sendMessage(
        @Path("conversionId") conversionId: Int,
        @Header("Authorization") authorization: String,
        @Header("content-type") contentType: String,
        @Header("lang") lang: String,
        @Part("message") message: RequestBody,
        @Part("media") media: RequestBody?
    ): Call<MessageResponse>


    @GET("conversations/{conversionId}/messages")
    fun getMessagesByConversationId(
        @Path("conversionId") conversionId: Int,
        @Header("Authorization") authorization: String,
        @Header("content-type") contentType: String,
        @Header("lang") lang: String
    ): Call<List<Message>>
}