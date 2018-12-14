package com.hangloose.ui.model

import com.hangloose.model.Activities
import com.hangloose.model.Adventures

data class SelectionList(
    var activities: List<Activities>,
    var adventures: List<Adventures>
)