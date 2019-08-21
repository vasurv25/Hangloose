package com.hangloose.listener

import com.hangloose.ui.model.RestaurantData

interface LikedInsertionListener {
    fun onLikedRecordInserted(id: Long)
    fun onLikedRecordError(msg : String, data: RestaurantData)
}

interface SavedInsertionListener {
    fun onSavedRecordInserted(id: Long)
}