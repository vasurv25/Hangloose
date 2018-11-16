package com.hangloose.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.Observable

class ConsumerViewModel(private var context: Context?) : Observable() {

    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var consumerLoginRequest: ConsumerLoginRequest =
        ConsumerLoginRequest(AUTH_TYPE.MOBILE.name, "2531256372", "sajgdasd")
    private var consumerAuthDetailResponse: ConsumerAuthDetailResponse? = null
    private val TAG = "ConsumerViewModel"

    fun onSignInClick(view: View) {
        Log.i(TAG, "onSignInClick")
        verifySignIn()
    }

    private fun verifySignIn() {
        val appController = HanglooseApp.create(context!!)
        val apiService = appController.getApiService()

        val disposable = apiService.consumerLogin(consumerLoginRequest)
            .subscribeOn(appController.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.i(TAG, "success login")
                consumerAuthDetailResponse = response as ConsumerAuthDetailResponse
                loginSuccess()
            }, {
                Log.i(TAG, "error login")
            })

        compositeDisposable!!.add(disposable)
    }

    private fun loginSuccess() {
        setChanged()
        notifyObservers()
    }

    private fun unSubscribeFromObservable() {
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed()) {
            compositeDisposable!!.dispose()
        }
    }

    fun reset() {
        unSubscribeFromObservable()
        compositeDisposable = null
        context = null
    }
}