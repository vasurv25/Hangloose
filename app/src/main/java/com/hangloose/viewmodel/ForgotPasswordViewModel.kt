package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerResendOtpRequest
import com.hangloose.utils.MESSAGE_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import retrofit2.Response

class ForgotPasswordViewModel : ViewModel() {

    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()
    var isVisible = ObservableBoolean()
    private var mConsumerResendOTPResponse: MutableLiveData<Response<Int>> = MutableLiveData()
    private val TAG = "ForgotPasswordViewModel"

    /**
     * method to call API for registering the user credentials
     */
    fun initiateForgotPassword(consumerResendOtpRequest: ConsumerResendOtpRequest) {
        Log.i(TAG, "Initiate Forgot Password request" + consumerResendOtpRequest.toString())
        val disposable = HanglooseApp.getApiService()!!.initiateForgotPassword(consumerResendOtpRequest!!)
            .subscribeOn(HanglooseApp.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                isVisible.set(true)
            }
            .doFinally {
                isVisible.set(false)
            }
            .subscribe({
                if (it.code() == 200 || it.code() == 201 || it.code() == 204) {
                    Log.i(TAG, "success register${it.code()}")
                    mConsumerResendOTPResponse.value = it
                } else {
                    val jObjError = JSONObject(it.errorBody()!!.string())
                    mShowErrorSnackBar.value = jObjError.getString(MESSAGE_KEY)
                }
            }, {
                Log.i(TAG, "error register$it")
                mShowErrorSnackBar.value = it.localizedMessage
            })

        mCompositeDisposable!!.add(disposable)
    }

    fun initiateOTPResponse(): MutableLiveData<Response<Int>> {
        Log.i(TAG, "Live Data")
        return mConsumerResendOTPResponse
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