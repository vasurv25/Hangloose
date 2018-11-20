package com.hangloose.model

import com.google.gson.annotations.SerializedName

data class Consumer(
    @SerializedName("address") val address: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("dob") val dob: String,
    @SerializedName("email") val email: String,
    @SerializedName("existing") val existing: Boolean,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("id") val id: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("photo") val photo: String,
    @SerializedName("updatedAt") val updatedAt: String
) : BaseModel()

data class ConsumerAuth(
    @SerializedName("consumerAuthStatus") val consumerAuthStatus: String,
    @SerializedName("consumerId") val consumerId: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("updatedAt") val updatedAt: String
) : BaseModel()

data class ConsumerAuthDetailResponse(
    @SerializedName("consumer") val consumer: Consumer,
    @SerializedName("consumerAuths") val consumerAuths: ConsumerAuth
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