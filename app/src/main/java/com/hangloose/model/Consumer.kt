package com.hangloose.model

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.gson.annotations.SerializedName

data class Consumer(
    @SerializedName("address") val address: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("dob") val dob: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("existing") val existing: Boolean?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("updatedAt") val updatedAt: String?
) : BaseModel()

data class ConsumerAuth(
    @SerializedName("consumerAuthStatus") val consumerAuthStatus: String?,
    @SerializedName("consumerId") val consumerId: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("updatedAt") val updatedAt: String?
) : BaseModel()

data class ConsumerAuthDetailResponse(
    @SerializedName("consumer") val consumer: Consumer?,
    @SerializedName("consumerAuths") val consumerAuths: List<ConsumerAuth>?
) : BaseModel()

data class ConsumerCreateRequest(
    @SerializedName("authId") var authId: String?,
    @SerializedName("authType") var authType: String?,
    @SerializedName("password") var password: String?
) : BaseModel()

data class ConsumerLoginRequest(
    @SerializedName("type") var type: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("token") var token: String?
) : BaseModel()

data class ConsumerOTPRequest(
    @SerializedName("id") var mobileNo: String?,
    @SerializedName("token") var otp: String?
) : BaseModel()

data class ConsumerResendOtpRequest(
    @SerializedName("authType") var authType: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("otpRequestReason") var otpRequestReason: String?
) : BaseModel()

data class ConsumerOtpVerifyRequest(
    @SerializedName("id") var id: String?,
    @SerializedName("otp") var otp: String?,
    @SerializedName("otpRequestReason") var otpRequestReason: String?
) : BaseModel()

data class ConsumerOtpChangePasswordRequest(
    @SerializedName("authType") var authType: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("newPassword") var newPassword: String?
) : BaseModel()

data class Activities(
    @SerializedName("createdAt") var createdAt: String?,
    @SerializedName("updatedAt") var updatedAt: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("image") var image: String?
) : BaseModel()

data class Adventures(
    @SerializedName("createdAt") var createdAt: String?,
    @SerializedName("updatedAt") var updatedAt: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("image") var image: String?
) : BaseModel()

data class RestuarantListRequest(
    @SerializedName("address") var activityIds: List<String>?,
    @SerializedName("createdAt") var adventureIds: List<String>?
) : BaseModel()

data class RestaurantList(
    @SerializedName("address") var address: String?,
    @SerializedName("createdAt") var createdAt: String?,
    @SerializedName("discount") var discount: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("images") var images: List<String>?,
    @SerializedName("latitude") var latitude: String?,
    @SerializedName("longitude") var longitude: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("offer") var offer: String?,
    @SerializedName("priceFortwo") var priceFortwo: String?,
    @SerializedName("ratings") var ratings: String?,
    @SerializedName("restaurantType") var restaurantType: String?,
    @SerializedName("updatedAt") var updatedAt: String?,
    @SerializedName("distanceFromLocation") var distanceFromLocation: Double?,
    @SerializedName("about") var about: String?,
    @SerializedName("tags") var tags: List<String>?,
    @SerializedName("openCloseTime") var openCloseTime: String?,
    @SerializedName("number") var number: String?,
    @SerializedName("documents") var documents: List<Document>?
) : BaseModel()

data class Document(
    @SerializedName("createdAt") var createdAt: String?,
    @SerializedName("updatedAt") var updatedAt: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("location") var location: String?,
    @SerializedName("ownerId") var ownerId: String?,
    @SerializedName("documentType") var documentType: String?
) : BaseModel()

data class RestaurantConsumerRating(
    @SerializedName("consumerId") var consumerId: String?,
    @SerializedName("ratingAction") var ratingAction: String?,
    @SerializedName("restaurantId") var restaurantId: String?
) : BaseModel()