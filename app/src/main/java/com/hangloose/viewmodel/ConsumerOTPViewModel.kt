package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerDetails
import com.hangloose.network.ConsumerAuthDetailResponse
import com.hangloose.network.ConsumerOTPRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class ConsumerOTPViewModel : ViewModel() {

    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mConsumerAuthDetailResponse: MutableLiveData<Response<ConsumerAuthDetailResponse>>? = MutableLiveData()
    private val TAG = "ConsumerOTPViewModel"

    /**
     * method to call API to verify OTP
     */
    fun verifyOTP(consumerOTPRequest: ConsumerOTPRequest) {
        if (consumerOTPRequest.otp != null && consumerOTPRequest.mobileNo != null) {
            Log.i(TAG, "Consumer Details : ${ConsumerDetails.consumerData}")
            val disposable = HanglooseApp.getApiService()!!.consumerRegisterOTP(
                ConsumerDetails.consumerData!!.headers!!,
                ConsumerDetails.consumerData!!.id!!,
                ConsumerDetails.consumerData!!.authType!!,
                consumerOTPRequest
            )
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.i(TAG, "success login")
                    mConsumerAuthDetailResponse!!.value = response
                }, {
                    Log.i(TAG, "error login")
                })

            mCompositeDisposable!!.add(disposable)
        } else {
        }
    }

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