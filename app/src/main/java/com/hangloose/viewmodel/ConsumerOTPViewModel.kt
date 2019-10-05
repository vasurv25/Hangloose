package com.hangloose.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerOTPRequest
import com.hangloose.model.ConsumerOtpVerifyRequest
import com.hangloose.ui.model.ConsumerDetails
import com.hangloose.utils.MESSAGE_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import retrofit2.Response

class ConsumerOTPViewModel : ViewModel() {

    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mConsumerAuthDetailResponse: MutableLiveData<Response<ConsumerAuthDetailResponse>>? = MutableLiveData()
    private val TAG = "ConsumerOTPViewModel"
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()

    /**
     * method to call API to verify OTP
     */
    fun verifyOtpForRegistration(consumerOTPRequest: ConsumerOTPRequest) {
        if (consumerOTPRequest.otp != null && consumerOTPRequest.mobileNo != null) {
            Log.i(TAG, "Consumer Details : ${ConsumerDetails.consumerData}")
            val disposable = HanglooseApp.getApiService()!!.consumerRegisterOTP(
                ConsumerDetails.consumerData!!.headers!!,
                ConsumerDetails.consumerData!!.consumerId!!,
                ConsumerDetails.consumerData!!.authType!!,
                consumerOTPRequest
            )
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        mConsumerAuthDetailResponse!!.value = response
                        Log.i(TAG, """success register${response.body()!!.consumer!!}""")
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        mShowErrorSnackBar.value = jObjError.getString(MESSAGE_KEY)
                    }
                }, {
                    Log.i(TAG, "error login")
                    mShowErrorSnackBar.value = it.localizedMessage
                })

            mCompositeDisposable!!.add(disposable)
        } else {
        }
    }

    fun verfiyOtpForResetPass(consumerVerifyOTPRequest: ConsumerOtpVerifyRequest) {
        if (consumerVerifyOTPRequest.otp != null && consumerVerifyOTPRequest.id != null) {
            val disposable = HanglooseApp.getApiService()!!.otpVerifyForgotPassword(consumerVerifyOTPRequest)
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        mConsumerAuthDetailResponse!!.value = response
                        Log.i(TAG, """success register${response.body()!!.consumer!!}""")
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        mShowErrorSnackBar.value = jObjError.getString(MESSAGE_KEY)
                    }
                }, {
                    Log.i(TAG, "error login")
                    mShowErrorSnackBar.value = it.localizedMessage
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