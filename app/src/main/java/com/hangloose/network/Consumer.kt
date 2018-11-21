package com.hangloose.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hangloose.model.BaseModel

data class Consumer(
    @SerializedName("address")
    @Expose val address: String?,
    @SerializedName("createdAt")
    @Expose val createdAt: String?,
    @SerializedName("dob")
    @Expose val dob: String?,
    @SerializedName("email")
    @Expose val email: String?,
    @SerializedName("existing")
    @Expose val existing: Boolean?,
    @SerializedName("firstName")
    @Expose val firstName: String?,
    @SerializedName("gender")
    @Expose val gender: String?,
    @SerializedName("id")
    @Expose val id: String?,
    @SerializedName("lastName")
    @Expose val lastName: String?,
    @SerializedName("mobile")
    @Expose val mobile: String?,
    @SerializedName("photo")
    @Expose val photo: String?,
    @SerializedName("updatedAt")
    @Expose val updatedAt: String?,
    var authType: String?
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