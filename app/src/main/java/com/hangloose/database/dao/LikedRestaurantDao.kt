package com.hangloose.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.utils.LIKED_TABLE_RESTAURANT

@Dao
abstract class LikedRestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLikedRestaurant(likedRestaurantData: LikedRestaurant): Long

    @Transaction
    @Query("Select * from LIKED_TABLE_RESTAURANT")
    abstract fun getAllLikedRestaurant(): LiveData<List<LikedRestaurant>>

    @Delete
    abstract fun deleteUnlikedRestaurant(likedRestaurantData: LikedRestaurant): Int

    @Transaction
    @Query("Select * from LIKED_TABLE_RESTAURANT where _id = :id and isLike = :isLike")
    abstract fun getPersistedLikedRestaurant(id: String, isLike: Boolean): LiveData<LikedRestaurant>

    @Query("DELETE FROM LIKED_TABLE_RESTAURANT")
    abstract fun emptyLikeDislikeRestaurants()
}