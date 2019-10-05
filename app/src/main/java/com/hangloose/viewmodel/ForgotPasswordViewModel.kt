package com.hangloose.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableBoolean
import android.util.Log
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerResendOtpRequest
import com.hangloose.utils.MESSAGE_KEY
import com.hangloose.utils.STATUS_CREATED
import com.hangloose.utils.STATUS_NOCONTENT
import com.hangloose.utils.STATUS_OK
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
        Log.i(TAG, "Initiate Forgot Password request$consumerResendOtpRequest")
        val disposable = HanglooseApp.getApiService()!!.initiateForgotPassword(consumerResendOtpRequest)
            .subscribeOn(HanglooseApp.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                isVisible.set(true)
            }
            .doFinally {
                isVisible.set(false)
            }
            .subscribe({
                if (it.code() == STATUS_OK || it.code() == STATUS_CREATED || it.code() == STATUS_NOCONTENT) {
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