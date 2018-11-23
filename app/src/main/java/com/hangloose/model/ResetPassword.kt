package com.hangloose.model

import com.google.gson.annotations.SerializedName

data class ForgotPassword(
    @SerializedName("authType") var authType: String?,
    @SerializedName("id") var id: String?
) : BaseModel()

data class ResetPassword(
    @SerializedName("authType") var authType: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("newPassword") var newPassword: String?,
    @SerializedName("otp") var otp: String?
) : BaseModel()