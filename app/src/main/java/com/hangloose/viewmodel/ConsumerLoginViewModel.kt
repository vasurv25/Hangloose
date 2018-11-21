package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp.Companion.getApiService
import com.hangloose.HanglooseApp.Companion.subscribeScheduler
import com.hangloose.network.ConsumerAuthDetailResponse
import com.hangloose.network.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.utils.MESSAGE_KEY
import com.hangloose.utils.PHONE_PASSWORD_CANNOT_BE_EMPTY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import retrofit2.Response

class ConsumerLoginViewModel : ViewModel() {

    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mConsumerLoginRequest: ConsumerLoginRequest =
        ConsumerLoginRequest(AUTH_TYPE.MOBILE.name, null, null)
    private var mConsumerAuthDetailResponse: MutableLiveData<Response<ConsumerAuthDetailResponse>> = MutableLiveData()
    private val TAG = "ConsumerLoginViewModel"
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()

    val phoneWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            mConsumerLoginRequest.id = edit.toString()
        }
    }

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            mConsumerLoginRequest.token = edit.toString()
        }
    }

    fun onSignInClick(view: View) {
        Log.i(TAG, "onSignInClick")
        if (mConsumerLoginRequest.id.isNullOrEmpty() || mConsumerLoginRequest.token.isNullOrEmpty()) {
            mShowErrorSnackBar.value = PHONE_PASSWORD_CANNOT_BE_EMPTY
        } else {
            verifySignIn()
        }
    }

    fun onFacebookSignInClick(fbLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onFacebookSignInClick")
        mConsumerLoginRequest = fbLoginRequest
        verifySignIn()
    }

    fun onGoogleSignInClick(googleLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onGoogleSignInClick")
        mConsumerLoginRequest = googleLoginRequest
        verifySignIn()
    }

    /**
     * method to call API to verify signIn credentials
     */
    private fun verifySignIn() {
        val disposable = getApiService()!!.consumerLogin(mConsumerLoginRequest)
            .subscribeOn(subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                if (response.isSuccessful) {
                    mConsumerAuthDetailResponse.value = response
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    mShowErrorSnackBar.value = jObjError.getString(MESSAGE_KEY)
                }
            }
        mCompositeDisposable!!.add(disposable)
    }

    fun loginResponse(): MutableLiveData<Response<ConsumerAuthDetailResponse>> {
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