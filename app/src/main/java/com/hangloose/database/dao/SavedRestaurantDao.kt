package com.hangloose.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hangloose.database.dbmodel.SavedRestaurant
import com.hangloose.ui.model.RestaurantData

@Dao
abstract class SavedRestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSavedRestaurant(savedRestaurantData: SavedRestaurant): Long

    @Transaction
    @Query("Select * from SAVED_TABLE_RESTAURANT")
    abstract fun getAllSavedRestaurant(): LiveData<List<SavedRestaurant>>

    @Delete
    abstract fun deleteUnSavedRestaurant(savedRestaurantData: SavedRestaurant): Int

    @Query("DELETE FROM SAVED_TABLE_RESTAURANT")
    abstract fun emptySavedRestaurants()

    @Transaction
    @Query("Select * from SAVED_TABLE_RESTAURANT where _id = :id")
    abstract fun  getPersistedSavedRestaurant(id: String): LiveData<SavedRestaurant>

    @Query("UPDATE SAVED_TABLE_RESTAURANT SET address= :address, createdAt= :createdAt, discount= :discount, images= :images, latitude= :latitude, longitude= :longitude, name= :name, offer= :offer, priceFortwo= :priceForTwo, ratings= :ratings, restaurantType= :restaurantType, updatedAt= :updatedAt, distanceFromLocation= :distanceFromLocation, about= :about, tags= :tags, openCloseTime= :openCloseTime, number= :number, logo= :logo, ambience= :ambience, menu= :menu WHERE _id= :id")
    abstract fun updateSavedRestaurantData(id: String, address: String, createdAt: String, discount: String, images: List<String>
                                           , latitude: String, longitude: String, name: String, offer: String, priceForTwo: String
                                           , ratings: String, restaurantType: String, updatedAt: String, distanceFromLocation: Double
                                           , about: String, tags: List<String>, openCloseTime: String, number: String, logo: String
                                           , ambience: List<String>, menu: List<String>): Long
}