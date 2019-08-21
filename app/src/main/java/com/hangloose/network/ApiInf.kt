package com.hangloose.network

import com.hangloose.model.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInf {
    @POST("consumers/login")
    fun consumerLogin(@Body consumerLoginRequestBody: ConsumerLoginRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @POST("consumers")
    fun consumerRegister(@Body consumerRegisterRequestBody: ConsumerCreateRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @PATCH("consumers/{id}/{authType}/verified")
    fun consumerRegisterOTP(@Header("X_AUTH_TOKEN") header: String, @Path("id") consumerId: String, @Path("authType") authType: String, @Body consumerOTPRequest: ConsumerOTPRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @POST("otp")
    fun initiateForgotPassword(@Body resendOtp: ConsumerResendOtpRequest): Observable<Response<Int>>

    @POST("otp/verify")
    fun otpVerifyForgotPassword(@Body verifyOtp: ConsumerOtpVerifyRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @PATCH("consumers/{consumerId}/setPassword")
    fun consumerChangePassword(@Header("X_AUTH_TOKEN") header: String, @Path("consumerId") consumerId: String, @Body consumerOTPChangePassRequest: ConsumerOtpChangePasswordRequest): Observable<Response<Int>>

    @GET("/activities")
    fun getActivities(@Header("X_AUTH_TOKEN") header: String): Observable<Response<List<Activities>>>

    @GET("/adventures")
    fun getAdventures(@Header("X_AUTH_TOKEN") header: String): Observable<Response<List<Adventures>>>

    @GET("/restaurants")
    fun getRestaurants(
        @Header("X_AUTH_TOKEN") header: String, @Query(
            "activityIds",
            encoded = true
        ) activityIds: String, @Query(
            "adventureIds",
            encoded = true
        ) adventureIds: String, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double,@Query("distance") distance: Int, @Query(
            "tags",
            encoded = true
        ) tags: String): Observable<Response<List<RestaurantList>>>

    @GET("restaurants/{restaurantId}")
    fun getRestaurantById(@Path("restaurantId") restaurantId: String): Observable<Response<RestaurantList>>

    @POST("/restaurantsConsumersRatings")
    fun restaurantsConsumersRatings(@Body restaurantConsumerRating: RestaurantConsumerRating): Observable<Response<Int>>
}