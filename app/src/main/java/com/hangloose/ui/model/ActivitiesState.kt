package com.hangloose.ui.model

import com.hangloose.model.BaseModel

data class ActivitiesState(
    val image: Int?
) : BaseModel() {
    var checked: Boolean = false
}