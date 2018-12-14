package com.hangloose.model

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
