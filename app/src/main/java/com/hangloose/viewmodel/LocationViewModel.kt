package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.model.RestaurantList
import com.hangloose.utils.MESSAGE_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import retrofit2.Response

class LocationViewModel : ViewModel() {

    private val TAG = "LocationViewModel"

    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mRestaurantListResponse: MutableLiveData<Response<List<RestaurantList>>> = MutableLiveData()
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()
    var isVisible = ObservableBoolean()

    fun restaurantListApiRequest(
        activitiesSelectedList: ArrayList<String>,
        adventuresSelectedList: ArrayList<String>,
        minimumDiscount: String,
        maximumDiscount: String,
        latitude: Double,
        longitude: Double,
        mHeader: String?, tagsList: String
    ) {
        Log.i(TAG, "Actvities List : $activitiesSelectedList")
        Log.i(TAG, "Adeventures List : $adventuresSelectedList")
        Log.i(TAG, "Consumer Deatils$mHeader")
        val disposable = HanglooseApp.getApiService()!!.getRestaurants(
            mHeader!!, convertToCSV(activitiesSelectedList), convertToCSV(adventuresSelectedList), latitude, longitude
            , minimumDiscount, maximumDiscount, 50, tagsList)

            .subscribeOn(HanglooseApp.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                isVisible.set(true)
            }
            .doFinally {
                isVisible.set(false)
            }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    Log.i(TAG, "Response : ${response.isSuccessful}")
                    mRestaurantListResponse.value = response
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    mShowErrorSnackBar.value = jObjError.getString(MESSAGE_KEY)
                }
                mCompositeDisposable!!.dispose()
            }, {
                Log.i(TAG, "error login")
                mShowErrorSnackBar.value = it.localizedMessage
            })
        //mCompositeDisposable!!.add(disposable)
    }

    fun getRestaurantList(): MutableLiveData<Response<List<RestaurantList>>> {
        return mRestaurantListResponse
    }

    fun convertToCSV(list : ArrayList<String>) : String {
        var listInString = StringBuilder()

        for (name in list) {
            listInString = if (listInString.isNotEmpty()) listInString.append(",").append(name) else listInString.append(name)
        }
        return listInString.toString()
    }
}