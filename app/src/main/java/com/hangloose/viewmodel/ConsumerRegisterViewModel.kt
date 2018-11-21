package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp
import com.hangloose.network.ConsumerAuthDetailResponse
import com.hangloose.network.ConsumerCreateRequest
import com.hangloose.network.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class ConsumerRegisterViewModel : ViewModel() {

    private val TAG = "ConsumerRegisterModel"
    private var mConsumerLoginRequest: ConsumerLoginRequest =
        ConsumerLoginRequest(AUTH_TYPE.MOBILE.name, null, null)
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mConsumerRegisterRequest: ConsumerCreateRequest? =
        ConsumerCreateRequest(null, AUTH_TYPE.MOBILE.name, null)
    private var mConsumerAuthDetailResponse: MutableLiveData<Response<ConsumerAuthDetailResponse>> = MutableLiveData()

    val phoneWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            mConsumerRegisterRequest!!.authId = edit.toString()
        }
    }

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            mConsumerRegisterRequest!!.password = edit.toString()
        }
    }

    /**
     * method to perform click on signUp button after user enters mobile number and password
     * @view view clicked
     */
    fun onSignUpClick(view: View) {
        Log.i(TAG, "onSignUpClick")
        registerUser()
    }

    fun onFacebookSignUpClick(fbLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onFacebookSignInClick")
        mConsumerLoginRequest = fbLoginRequest
        verifyGoogleFbSignUp()
    }

    fun onGoogleSignUpClick(googleLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onGoogleSignInClick")
        mConsumerLoginRequest = googleLoginRequest
        verifyGoogleFbSignUp()
    }

    /**
     * method to call API to verify signIn credentials
     */
    private fun verifyGoogleFbSignUp() {
        if (mConsumerLoginRequest.id != null && mConsumerLoginRequest.token != null) {
            val disposable = HanglooseApp.getApiService()!!.consumerLogin(mConsumerLoginRequest)
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.i(TAG, "success login")
                    mConsumerAuthDetailResponse.value = response
                }, {
                    Log.i(TAG, "error login")
                })
            mCompositeDisposable!!.add(disposable)
        } else {
        }
    }

    /**
     * method to call API for registering the user credentials
     */
    private fun registerUser() {
        Log.i(TAG, "Register request" + mConsumerRegisterRequest.toString())
        val disposable = HanglooseApp.getApiService()!!.consumerRegister(mConsumerRegisterRequest!!)
            .subscribeOn(HanglooseApp.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                mConsumerAuthDetailResponse.value = response
                Log.i(TAG, """success register$mConsumerAuthDetailResponse""")
                val header = response.headers()
                Log.i(TAG, "Header : ${header.get("X-AUTH-TOKEN")}")
            }, {
                Log.i(TAG, "error register$it")
            })

        mCompositeDisposable!!.add(disposable)
    }

    fun registerResponse(): MutableLiveData<Response<ConsumerAuthDetailResponse>> {
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