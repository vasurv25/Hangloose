/*
 * Copyright (c) 2018. Vungle, All rights reserved.
 */

package com.hangloose.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.hangloose.database.dao.LikedRestaurantDao
import com.hangloose.database.dao.SavedRestaurantDao
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.database.dbmodel.SavedRestaurant
import com.hangloose.utils.DB_NAME

/**
 * room database class
 */
@Database(entities = [LikedRestaurant::class, SavedRestaurant::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppRoomDatabase::class.java, DB_NAME)
                    .build()
            }
            return INSTANCE as AppRoomDatabase
        }
    }

    internal abstract fun likedRestaurantDao(): LikedRestaurantDao
    internal abstract fun savedRestaurantDao(): SavedRestaurantDao
}