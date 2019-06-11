package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.model.Activities
import com.hangloose.model.Adventures
import com.hangloose.model.RestaurantList
import com.hangloose.ui.model.ConsumerDetails
import com.hangloose.ui.model.SelectionList
import com.hangloose.utils.MESSAGE_KEY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import org.json.JSONObject
import retrofit2.Response

class SelectionViewModel : ViewModel() {

    private val TAG = "SelectionViewModel"
    private var mSelectionList: MutableLiveData<SelectionList> = MutableLiveData()
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mRestaurantListResponse: MutableLiveData<Response<List<RestaurantList>>> = MutableLiveData()
    var isVisible = ObservableBoolean()

    fun selectionListApiRequest() {
        Log.i(TAG, """X_AUTH_TOKEN : ${ConsumerDetails.consumerData!!.headers!!}""")
        var callActivities = HanglooseApp.getApiService()!!.getActivities(ConsumerDetails.consumerData!!.headers!!)
        var callAdventures = HanglooseApp.getApiService()!!.getAdventures(ConsumerDetails.consumerData!!.headers!!)

        val disposable =
            Observable.zip(callActivities, callAdventures,
                BiFunction<Response<List<Activities>>, Response<List<Adventures>>, SelectionList> { t1, t2 ->
                    SelectionList(
                        t1.body()!!,
                        t2.body()!!
                    )
                })
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isVisible.set(true)
                }
                .doFinally {
                    isVisible.set(false)
                }
                .subscribe({
                    Log.i(TAG, "success login : $it")
                    mSelectionList.value = it
                }, {
                    Log.i(TAG, """error login : ${it.localizedMessage}""")
                    mShowErrorSnackBar.value = it.localizedMessage
                    // TODO if error related to token mis-match navigate user to SignIn Activity
                })

        mCompositeDisposable!!.add(disposable)
    }

    fun restaurantListApiRequest(
        activitiesSelectedList: ArrayList<String>,
        adventuresSelectedList: ArrayList<String>
    ) {
        val disposable = HanglooseApp.getApiService()!!.getRestaurants(ConsumerDetails.consumerData!!.headers!!, convertToCSV(activitiesSelectedList), convertToCSV(adventuresSelectedList))
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
        mCompositeDisposable!!.add(disposable)
    }

    private fun unSubscribeFromObservable() {
        if (mCompositeDisposable != null && !mCompositeDisposable!!.isDisposed) {
            mCompositeDisposable!!.dispose()
        }
    }

    fun getSelectionList(): MutableLiveData<SelectionList> {
        return mSelectionList
    }

    fun getRestaurantList(): MutableLiveData<Response<List<RestaurantList>>> {
        return mRestaurantListResponse
    }

    fun reset() {
        unSubscribeFromObservable()
        mCompositeDisposable = null
    }

    private fun convertToCSV(list : ArrayList<String>) : String {
        var listInString = StringBuilder()

        for (name in list) {
            listInString = if (listInString.isNotEmpty()) listInString.append(",").append(name) else listInString.append(name)
        }
        return listInString.toString();
    }
}