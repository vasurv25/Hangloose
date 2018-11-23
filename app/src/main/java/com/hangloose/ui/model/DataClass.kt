package com.hangloose.ui.model

import com.hangloose.model.BaseModel

data class ConsumerData (
    val headers: String?,
    val existing: Boolean?,
    val id: String?,
    val mobile: String?,
    val authType: String?
) : BaseModel()