package com.hangloose.ui.model

import com.hangloose.model.BaseModel

data class AdventuresState(
    val image: Int?
) : BaseModel() {
    var checked: Boolean = false
}