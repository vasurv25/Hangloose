package com.hangloose.model

import com.google.gson.annotations.SerializedName

data class UserRegister(
    @SerializedName("name")
    private var name : String? = null
)