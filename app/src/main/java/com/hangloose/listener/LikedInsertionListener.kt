package com.hangloose.listener

import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.ui.model.RestaurantData

interface LikedInsertionListener {
    fun onLikedRecordInserted(id: Long)
    fun onLikedRecordError(msg : String, data: RestaurantData)
    fun getLikeDislikeData(data: LikedRestaurant, restaurantData: RestaurantData, type: String)
    fun getLikeDislikeDataIfNull(data: RestaurantData, type: String)
}

interface SavedInsertionListener {
    fun onSavedRecordInserted(id: Long)
}