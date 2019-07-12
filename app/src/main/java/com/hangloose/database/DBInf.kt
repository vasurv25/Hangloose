package com.hangloose.database

import android.arch.lifecycle.LiveData
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.Restaurant
import com.hangloose.listener.RecordInsertionListener
import com.hangloose.ui.model.RestaurantData

interface DBInf {
    fun insertRestaurantData(restaurantData: RestaurantData, listener: RecordInsertionListener)
    fun getAllSavedRestaurant(): ModelCommunicator<LiveData<List<Restaurant>>>
}