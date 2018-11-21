package com.hangloose.model

data class ConsumerData (
    val headers: String?,
    val existing: Boolean?,
    val id: String?,
    val mobile: String?,
    val authType: String?
) : BaseModel()