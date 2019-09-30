package com.hangloose.database

import android.arch.lifecycle.LiveData
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.database.dbmodel.SavedRestaurant
import com.hangloose.listener.LikedInsertionListener
import com.hangloose.listener.SavedInsertionListener
import com.hangloose.ui.model.RestaurantData


interface DBInf : SavedDBInf, LikedDBInf

interface SavedDBInf {
    fun insertSavedRestaurantData(restaurantData: RestaurantData, listener: SavedInsertionListener)
    fun getAllSavedRestaurant(): ModelCommunicator<LiveData<List<SavedRestaurant>>>
    fun deleteUnsavedRestaurant(restaurantData: RestaurantData)
    fun getPersistedSavedRestaurant(id: String): ModelCommunicator<LiveData<SavedRestaurant>>
    fun emptyRestroForOldDate(date: String)
    fun emptySavedRestaurants()
    fun updateSavedRestaurantData(restaurantData: RestaurantData, listener: SavedInsertionListener)
}

interface LikedDBInf {
    fun insertLikeUnlikeRestaurantData(restaurantData: RestaurantData, isLike: Boolean, currentDate:String, listener: LikedInsertionListener)
    fun getAllLikedRestaurant(): ModelCommunicator<LiveData<List<LikedRestaurant>>>
    fun deleteLikeUnlikeRestaurant(restaurantData: RestaurantData, isLike: Boolean)
    fun getPersistedLikedRestaurant(data: RestaurantData, type: String, listener: LikedInsertionListener)
    fun emptyLikeDislikeRestaurants()
    fun updateLikedRestaurantData(restaurantData: RestaurantData, listener: LikedInsertionListener)
}