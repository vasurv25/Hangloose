package com.hangloose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import com.hangloose.HanglooseApp.Companion.getDataHandler
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.SavedRestaurant

class SavedViewModel : ViewModel() {
    fun getSavedRestaurants(): ModelCommunicator<LiveData<List<SavedRestaurant>>> {
        return SavedRestaurantAsyncTask().execute().get()
    }

    @Suppress("UNCHECKED_CAST")
    private class SavedRestaurantAsyncTask :
        AsyncTask<Void, Void, ModelCommunicator<LiveData<List<SavedRestaurant>>>>() {
        override fun doInBackground(vararg params: Void?): ModelCommunicator<LiveData<List<SavedRestaurant>>> {
            return getDataHandler()!!.getAllSavedRestaurant()
        }
    }

    fun getPersistedSavedRestaurant(id: String): ModelCommunicator<LiveData<SavedRestaurant>> {
        return PersistedSavedRestaurantAsyncTask(id).execute().get()
    }

    @Suppress("UNCHECKED_CAST")
    private class PersistedSavedRestaurantAsyncTask(var id: String) :
        AsyncTask<Void, Void, ModelCommunicator<LiveData<SavedRestaurant>>>() {
        override fun doInBackground(vararg params: Void?): ModelCommunicator<LiveData<SavedRestaurant>> {
            return getDataHandler()!!.getPersistedSavedRestaurant(id)
        }
    }
}