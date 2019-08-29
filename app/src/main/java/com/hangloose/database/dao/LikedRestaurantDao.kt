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
}