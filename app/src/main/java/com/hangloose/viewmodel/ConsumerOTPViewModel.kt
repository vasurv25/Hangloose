package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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

    val otpWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            mConsumerOTPRequest.token = edit.toString()
        }
    }
    fun onNextClick(view: View) {
        Log.i(TAG, "onSignInClick")
        //verifySignIn()
    }
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

    fun loginResponse(): MutableLiveData<Response<ConsumerAuthDetailResponse>>? {
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