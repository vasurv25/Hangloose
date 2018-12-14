package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.model.Activities
import com.hangloose.model.Adventures
import com.hangloose.ui.model.ConsumerDetails
import com.hangloose.ui.model.SelectionList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import retrofit2.Response

class SelectionViewModel : ViewModel() {

    private val TAG = "SelectionViewModel"
    private var mSelectionList: MutableLiveData<SelectionList> = MutableLiveData()
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()

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
                .subscribe({
                    Log.i(TAG, "success login : $it")
                    mSelectionList.value = it
                }, {
                    Log.i(TAG, """error login : ${it.localizedMessage}""")
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

    fun reset() {
        unSubscribeFromObservable()
        mCompositeDisposable = null
    }
}