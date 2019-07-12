package com.hangloose.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.hangloose.database.dbmodel.Restaurant

@Dao
abstract class RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSavedRestaurant(restaurantData: Restaurant): Long

    @Transaction
    @Query("Select * from TABLE_RESTAURANT")
    abstract fun getAllSavedRestaurant(): LiveData<List<Restaurant>>
}