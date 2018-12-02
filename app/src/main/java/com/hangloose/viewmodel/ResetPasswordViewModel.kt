package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerOtpChangePasswordRequest
import com.hangloose.ui.model.ConsumerDetails
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.utils.validateConfirmPassword
import com.hangloose.utils.validatePassword
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class ResetPasswordViewModel : ViewModel() {

    private val TAG = "ResetPasswordViewModel"
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mConfirmPassword: String? = null
    var isPasswordValid = ObservableBoolean()
    var isConfirmPasswordValid = ObservableBoolean()
    private var mConsumerChangePassRequest: ConsumerOtpChangePasswordRequest? =
        ConsumerOtpChangePasswordRequest(AUTH_TYPE.MOBILE.name, null, null)
    var isVisible = ObservableBoolean()
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()
    private var mConsumerAuthDetailResponse: MutableLiveData<Response<Int>> = MutableLiveData()

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mConsumerChangePassRequest!!.newPassword = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            if (validatePassword(edit.toString())) {
                isPasswordValid.set(true)
                isPasswordValid.notifyChange()
            }
        }
    }

    val confirmPasswordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mConfirmPassword = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            if (validateConfirmPassword(mConsumerChangePassRequest!!.newPassword, edit.toString())) {
                isConfirmPasswordValid.set(true)
                isConfirmPasswordValid.notifyChange()
            }
        }
    }

    fun onResetClick(view: View) {
        Log.i(TAG, "onSignUpClick")
        val validPassword = validatePassword(mConsumerChangePassRequest!!.newPassword)
        isPasswordValid.set(validPassword)
        isPasswordValid.notifyChange()

        val validConfirmPassword = validateConfirmPassword(mConsumerChangePassRequest!!.newPassword, mConfirmPassword)
        isConfirmPasswordValid.set(validConfirmPassword)
        isConfirmPasswordValid.notifyChange()
        if (validPassword && validConfirmPassword) {
            mConsumerChangePassRequest!!.id = ConsumerDetails.consumerData!!.mobile
            mConsumerChangePassRequest!!.authType = ConsumerDetails.consumerData!!.authType
            verifyNewPassword()
        }
    }

    private fun verifyNewPassword() {
        if (mConsumerChangePassRequest!!.id != null && mConsumerChangePassRequest!!.newPassword != null) {
            Log.i(TAG, "Consumer Details : ${ConsumerDetails.consumerData}")
            val disposable = HanglooseApp.getApiService()!!.consumerChangePassword(
                ConsumerDetails.consumerData!!.headers!!,
                ConsumerDetails.consumerData!!.consumerId!!,
                mConsumerChangePassRequest!!
            )
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isVisible.set(true)
                }
                .doFinally {
                    isVisible.set(false)
                }
                .subscribe({ response ->
                    Log.i(TAG, "success login")
                    mConsumerAuthDetailResponse.value = response
                }, {
                    Log.i(TAG, "error login")
                    mShowErrorSnackBar.value = it.localizedMessage
                })
            mCompositeDisposable!!.add(disposable)
        } else {
        }
    }

    fun verifyNewPassResponse(): MutableLiveData<Response<Int>> {
        Log.i(TAG, "Live Data")
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