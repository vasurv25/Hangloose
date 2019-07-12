package com.hangloose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import com.hangloose.HanglooseApp.Companion.getDataHandler
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.Restaurant
import com.hangloose.ui.model.RestaurantData

class SavedViewModel : ViewModel() {
    fun getSavedRestaurants(): ModelCommunicator<LiveData<List<Restaurant>>> {
        return SavedRestaurantAsyncTask().execute().get()
    }

    @Suppress("UNCHECKED_CAST")
    private class SavedRestaurantAsyncTask :
        AsyncTask<Void, Void, ModelCommunicator<LiveData<List<Restaurant>>>>() {
        override fun doInBackground(vararg params: Void?): ModelCommunicator<LiveData<List<Restaurant>>> {
            return getDataHandler()!!.getAllSavedRestaurant() as ModelCommunicator<LiveData<List<Restaurant>>>
        }
    }
}