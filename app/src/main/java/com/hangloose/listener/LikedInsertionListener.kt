package com.hangloose.listener

import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.ui.model.RestaurantData

interface LikedInsertionListener {
    fun onLikedRecordInserted(id: Long)
    fun onLikedRecordError(msg : String, data: RestaurantData)
    fun getLikeData(data: LikedRestaurant, restaurantData: RestaurantData, type: String)
    fun getLikeDataIfNull(data: RestaurantData, type: String)
}

interface SavedInsertionListener {
    fun onSavedRecordInserted(id: Long)
}