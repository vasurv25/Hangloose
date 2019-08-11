package com.hangloose.database

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.database.dbmodel.SavedRestaurant
import com.hangloose.listener.LikedInsertionListener
import com.hangloose.listener.SavedInsertionListener
import com.hangloose.ui.model.RestaurantData

class RoomDBHandler(context: Context) : DBInf {

    private var appRoomDatabase = AppRoomDatabase.getInstance(context)

    override fun insertSavedRestaurantData(restaurantData: RestaurantData, listener: SavedInsertionListener) {

        class InsertTask : AsyncTask<Void, Void, Long>() {
            override fun doInBackground(vararg params: Void): Long {
                val restaurant = SavedRestaurant(
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
                    restaurantData.number,
                    restaurantData.logo,
                    restaurantData.ambienceList,
                    restaurantData.menuList
                )
                return appRoomDatabase.savedRestaurantDao().insertSavedRestaurant(restaurant)
            }

            override fun onPostExecute(result: Long?) {
                super.onPostExecute(result)
                listener.onSavedRecordInserted(result!!)
            }
        }
        InsertTask().execute()

    }

    override fun getAllSavedRestaurant(): ModelCommunicator<LiveData<List<SavedRestaurant>>> {
        return object : ModelCommunicator<LiveData<List<SavedRestaurant>>> {
            override fun get(): LiveData<List<SavedRestaurant>> {
                return appRoomDatabase.savedRestaurantDao().getAllSavedRestaurant()
            }
        }
    }

    override fun deleteUnsavedRestaurant(restaurantData: RestaurantData) {
        class deleteTask : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void?): Int {
                val restaurant = SavedRestaurant(
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
                    restaurantData.number,
                    restaurantData.logo,
                    restaurantData.ambienceList,
                    restaurantData.menuList
                )
                return appRoomDatabase.savedRestaurantDao().deleteUnSavedRestaurant(restaurant)
            }
        }
        deleteTask().execute()
    }

    override fun getPersistedSavedRestaurant(id: String): ModelCommunicator<LiveData<SavedRestaurant>> {
        return object : ModelCommunicator<LiveData<SavedRestaurant>> {
            override fun get(): LiveData<SavedRestaurant> {
                return appRoomDatabase.savedRestaurantDao().getPersistedSavedRestaurant(id)
            }
        }
    }

    override fun insertLikedRestaurantData(restaurantData: RestaurantData, listener: LikedInsertionListener) {
        class InsertTask : AsyncTask<Void, Void, Long>() {
            override fun doInBackground(vararg params: Void): Long {
                val restaurant = LikedRestaurant(
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
                    restaurantData.number,
                    restaurantData.logo,
                    restaurantData.ambienceList,
                    restaurantData.menuList
                )
                return appRoomDatabase.likedRestaurantDao().insertLikedRestaurant(restaurant)
            }

            override fun onPostExecute(result: Long?) {
                super.onPostExecute(result)
                listener.onLikedRecordInserted(result!!)
            }
        }
        InsertTask().execute()
    }

    override fun getAllLikedRestaurant(): ModelCommunicator<LiveData<List<LikedRestaurant>>> {
        return object : ModelCommunicator<LiveData<List<LikedRestaurant>>> {
            override fun get(): LiveData<List<LikedRestaurant>> {
                return appRoomDatabase.likedRestaurantDao().getAllLikedRestaurant()
            }
        }
    }

    override fun deleteUnlikedRestaurant(restaurantData: RestaurantData) {
        class deleteTask : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void?): Int {
                val restaurant = LikedRestaurant(
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
                    restaurantData.number,
                    restaurantData.logo,
                    restaurantData.ambienceList,
                    restaurantData.menuList
                )
                return appRoomDatabase.likedRestaurantDao().deleteUnlikedRestaurant(restaurant)
            }
        }
        deleteTask().execute()
    }

    override fun getPersistedLikedRestaurant(id: String): ModelCommunicator<LiveData<LikedRestaurant>> {
        return object : ModelCommunicator<LiveData<LikedRestaurant>> {
            override fun get(): LiveData<LikedRestaurant> {
                return appRoomDatabase.likedRestaurantDao().getPersistedLikedRestaurant(id)
            }
        }
    }
}