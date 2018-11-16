package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp.Companion.getApiService
import com.hangloose.HanglooseApp.Companion.subscribeScheduler
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerCreateRequest
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class ConsumerViewModel : ViewModel() {

    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var consumerLoginRequest: ConsumerLoginRequest =
        ConsumerLoginRequest(AUTH_TYPE.MOBILE.name, "2531256372", "sajgdasd")
    private var consumerRegisterRequest: ConsumerCreateRequest =
        ConsumerCreateRequest(AUTH_TYPE.MOBILE.name, "2531256372", "sajgdasd")
    private var consumerAuthDetailResponse: MutableLiveData<ConsumerAuthDetailResponse>? = null
    private val TAG = "ConsumerViewModel"

    fun onSignInClick(view: View) {
        Log.i(TAG, "onSignInClick")

        val disposable = getApiService()!!.consumerLogin(consumerLoginRequest)
            .subscribeOn(subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.i(TAG, "success login")
                consumerAuthDetailResponse!!.value = response
            }, {
                Log.i(TAG, "error login")
            })

        compositeDisposable!!.add(disposable)
    }

    fun loginResponse(): MutableLiveData<ConsumerAuthDetailResponse>? {
        return consumerAuthDetailResponse
    }

    private fun unSubscribeFromObservable() {
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed) {
            compositeDisposable!!.dispose()
        }
    }

    fun reset() {
        unSubscribeFromObservable()
        compositeDisposable = null
    }
}