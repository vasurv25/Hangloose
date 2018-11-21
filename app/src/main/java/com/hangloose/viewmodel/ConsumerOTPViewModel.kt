package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerOTPRequest
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class ConsumerOTPViewModel : ViewModel() {

    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mConsumerOTPRequest: ConsumerOTPRequest =
        ConsumerOTPRequest(null, null)
    private var mConsumerAuthDetailResponse: MutableLiveData<Response<ConsumerAuthDetailResponse>>? = null
    private val TAG = "ConsumerOTPViewModel"

    /**
     * method to call API to verify OTP
     *//*
    private fun verifySignIn() {
        if (mConsumerOTPRequest.id != null && mConsumerOTPRequest.token != null) {
            val disposable = HanglooseApp.getApiService()!!.consumerRegisterOTP(mConsumerOTPRequest)
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.i(TAG, "success login")
                    consumerAuthDetailResponse!!.value = response.body()
                }, {
                    Log.i(TAG, "error login")
                })

            compositeDisposable!!.add(disposable)
        } else {
        }
    }*/

    fun otpResponse(): MutableLiveData<Response<ConsumerAuthDetailResponse>>? {
        return mConsumerAuthDetailResponse
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