package com.hangloose.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.hangloose.database.dbmodel.SavedRestaurant

@Dao
abstract class SavedRestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSavedRestaurant(savedRestaurantData: SavedRestaurant): Long

    @Transaction
    @Query("Select * from SAVED_TABLE_RESTAURANT")
    abstract fun getAllSavedRestaurant(): LiveData<List<SavedRestaurant>>

    @Delete
    abstract fun deleteUnSavedRestaurant(savedRestaurantData: SavedRestaurant): Int

    @Transaction
    @Query("Select * from SAVED_TABLE_RESTAURANT where _id = :id")
    abstract fun getPersistedSavedRestaurant(id: String): LiveData<SavedRestaurant>
}