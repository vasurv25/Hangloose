/*
 * Copyright (c) 2018. Vungle, All rights reserved.
 */

package com.hangloose.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.hangloose.database.dao.RestaurantDao
import com.hangloose.database.dbmodel.Restaurant
import com.hangloose.utils.DB_NAME

/**
 * room database class
 */
@Database(entities = [Restaurant::class], version = 1, exportSchema = false)
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

    internal abstract fun restaurantDao(): RestaurantDao
}