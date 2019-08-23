package com.hangloose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.HanglooseApp.Companion.getDataHandler
import com.hangloose.database.dbmodel.ModelCommunicator
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.listener.LikedInsertionListener
import com.hangloose.model.RestaurantConsumerRating
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject

class LikedViewModel : ViewModel() {
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private val TAG = "LikedViewModel"

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

//    fun getPersistedLikedRestaurant(id: String): ModelCommunicator<LiveData<LikedRestaurant>> {
//        return PersistedLikedRestaurantAsyncTask(id).execute().get()
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    private class PersistedLikedRestaurantAsyncTask(var id: String) :
//        AsyncTask<Void, Void, ModelCommunicator<LiveData<LikedRestaurant>>>() {
//        override fun doInBackground(vararg params: Void?): ModelCommunicator<LiveData<LikedRestaurant>> {
//            return getDataHandler()!!.getPersistedLikedRestaurant(id) as ModelCommunicator<LiveData<LikedRestaurant>>
//        }
//    }

    fun syncLikeUnlikeRestaurants(
        data: RestaurantData, restaurantConsumerRating: RestaurantConsumerRating, likedListener: LikedInsertionListener
    ) {
        val disposable = HanglooseApp.getApiService()!!.restaurantsConsumersRatings(restaurantConsumerRating)
            .subscribeOn(HanglooseApp.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code() == STATUS_OK || it.code() == STATUS_CREATED || it.code() == STATUS_NOCONTENT) {
                    Log.i(TAG, "success register${it.code()}")
                    when (restaurantConsumerRating.ratingAction) {
                        LIKE_DISLIKE.LIKE.name -> {
                            getDataHandler()!!.insertLikeUnlikeRestaurantData(data, true, likedListener)
                        }
                        LIKE_DISLIKE.DISLIKE.name -> {
                            getDataHandler()!!.insertLikeUnlikeRestaurantData(data, false, likedListener)
                        }
                    }
                } else {
                    val jObjError = JSONObject(it.errorBody()!!.string())
                    likedListener.onLikedRecordError(jObjError.getString(MESSAGE_KEY), data)
                }
            }, {
                Log.i(TAG, "error register$it")
                likedListener.onLikedRecordError(it.localizedMessage, data)
            })

        mCompositeDisposable!!.add(disposable)
    }

    private fun unSubscribeFromObservable() {
        if (mCompositeDisposable != null && !mCompositeDisposable!!.isDisposed) {
            mCompositeDisposable!!.dispose()
        }
    }

    fun reset() {
        unSubscribeFromObservable()
        mCompositeDisposable = null
    }
}