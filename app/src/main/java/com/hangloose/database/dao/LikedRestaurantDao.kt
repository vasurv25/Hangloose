package com.hangloose.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.hangloose.database.dbmodel.LikedRestaurant

@Dao
abstract class LikedRestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLikedRestaurant(likedRestaurantData: LikedRestaurant): Long

    @Transaction
    @Query("Select * from LIKED_TABLE_RESTAURANT where isLike = :isLike")
    abstract fun getAllLikedRestaurant(isLike: Boolean): LiveData<List<LikedRestaurant>>

    @Delete
    abstract fun deleteUnlikedRestaurant(likedRestaurantData: LikedRestaurant): Int

    @Transaction
    @Query("Select * from LIKED_TABLE_RESTAURANT where _id = :id")
    abstract fun getPersistedLikedRestaurant(id: String): LikedRestaurant

    @Query("DELETE FROM LIKED_TABLE_RESTAURANT")
    abstract fun emptyLikeDislikeRestaurants()

    @Query("DELETE FROM LIKED_TABLE_RESTAURANT where currentDate != :date")
    abstract fun emptyLikedRestroWithOldDate(date: String)

    @Query("UPDATE LIKED_TABLE_RESTAURANT SET address= :address, createdAt= :createdAt, discount= :discount, images= :images, latitude= :latitude, longitude= :longitude, name= :name, offer= :offer, priceFortwo= :priceForTwo, ratings= :ratings, restaurantType= :restaurantType, updatedAt= :updatedAt, distanceFromLocation= :distanceFromLocation, about= :about, tags= :tags, openCloseTime= :openCloseTime, number= :number, logo= :logo, ambience= :ambience, menu= :menu WHERE _id= :id")
    abstract fun updateLikedRestaurantData(
        id: String, address: String, createdAt: String, discount: String, images: List<String>
        , latitude: String, longitude: String, name: String, offer: String, priceForTwo: String
        , ratings: String, restaurantType: String, updatedAt: String, distanceFromLocation: Double
        , about: String, tags: List<String>, openCloseTime: String, number: String, logo: String
        , ambience: List<String>, menu: List<String>
    ): Long
}