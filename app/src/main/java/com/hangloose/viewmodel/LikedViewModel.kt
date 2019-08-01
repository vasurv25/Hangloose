package com.hangloose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import com.hangloose.HanglooseApp.Companion.getDataHandler
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.LikedRestaurant

class LikedViewModel : ViewModel() {
    fun getLikedRestaurants(): ModelCommunicator<LiveData<List<LikedRestaurant>>> {
        return LikedRestaurantAsyncTask().execute().get()
    }

    @Suppress("UNCHECKED_CAST")
    private class LikedRestaurantAsyncTask :
        AsyncTask<Void, Void, ModelCommunicator<LiveData<List<LikedRestaurant>>>>() {
        override fun doInBackground(vararg params: Void?): ModelCommunicator<LiveData<List<LikedRestaurant>>> {
            return getDataHandler()!!.getAllLikedRestaurant() as ModelCommunicator<LiveData<List<LikedRestaurant>>>
        }
    }

    fun getPersistedLikedRestaurant(id: String): ModelCommunicator<LiveData<LikedRestaurant>> {
        return PersistedLikedRestaurantAsyncTask(id).execute().get()
    }

    @Suppress("UNCHECKED_CAST")
    private class PersistedLikedRestaurantAsyncTask(var id: String) :
        AsyncTask<Void, Void, ModelCommunicator<LiveData<LikedRestaurant>>>() {
        override fun doInBackground(vararg params: Void?): ModelCommunicator<LiveData<LikedRestaurant>> {
            return getDataHandler()!!.getPersistedLikedRestaurant(id) as ModelCommunicator<LiveData<LikedRestaurant>>
        }
    }
}