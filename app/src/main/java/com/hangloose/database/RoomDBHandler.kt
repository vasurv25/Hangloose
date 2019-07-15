package com.hangloose.database

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.Restaurant
import com.hangloose.listener.RecordInsertionListener
import com.hangloose.ui.model.RestaurantData

class RoomDBHandler(context: Context) : DBInf {

    private var appRoomDatabase = AppRoomDatabase.getInstance(context)

    override fun insertRestaurantData(restaurantData: RestaurantData, listener: RecordInsertionListener) {

        class InsertTask : AsyncTask<Void, Void, Long>() {
            override fun doInBackground(vararg params: Void): Long {
                val restaurant = Restaurant(
                    restaurantData.address,
                    restaurantData.createdAt,
                    restaurantData.discount,
                    restaurantData.id!!,
                    restaurantData.images,
                    restaurantData.latitude,
                    restaurantData.longitude,
                    restaurantData.name,
                    restaurantData.offer,
                    restaurantData.priceFortwo,
                    restaurantData.ratings,
                    restaurantData.restaurantType,
                    restaurantData.updatedAt,
                    restaurantData.distanceFromLocation,
                    restaurantData.about,
                    restaurantData.tags,
                    restaurantData.openCloseTime,
                    restaurantData.saved
                )
                return appRoomDatabase.restaurantDao().insertSavedRestaurant(restaurant)
            }

            override fun onPostExecute(result: Long?) {
                super.onPostExecute(result)
                listener.onRecordInserted(result!!)
            }
        }
        InsertTask().execute()

    }

    override fun getAllSavedRestaurant(): ModelCommunicator<LiveData<List<Restaurant>>> {
        return object : ModelCommunicator<LiveData<List<Restaurant>>> {
            override fun get(): LiveData<List<Restaurant>> {
                return appRoomDatabase.restaurantDao().getAllSavedRestaurant()
            }
        }
    }

    override fun deleteUnsavedRestaurant(restaurantData: RestaurantData) {
        class deleteTask : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void?): Int {
                val restaurant = Restaurant(
                    restaurantData.address,
                    restaurantData.createdAt,
                    restaurantData.discount,
                    restaurantData.id!!,
                    restaurantData.images,
                    restaurantData.latitude,
                    restaurantData.longitude,
                    restaurantData.name,
                    restaurantData.offer,
                    restaurantData.priceFortwo,
                    restaurantData.ratings,
                    restaurantData.restaurantType,
                    restaurantData.updatedAt,
                    restaurantData.distanceFromLocation,
                    restaurantData.about,
                    restaurantData.tags,
                    restaurantData.openCloseTime,
                    restaurantData.saved
                )
                return appRoomDatabase.restaurantDao().deleteUnsavedRestaurant(restaurant)
            }
        }
        deleteTask().execute()
    }

    override fun getPersistedSavedRestaurant(id: String): ModelCommunicator<LiveData<Int>> {
        return object : ModelCommunicator<LiveData<Int>> {
            override fun get(): LiveData<Int> {
                return appRoomDatabase.restaurantDao().getPersistedSavedRestaurant(id)
            }
        }
    }
}