package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerCreateRequest
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_up.*

class ConsumerRegisterViewModel : ViewModel() {

    private val TAG = "ConsumerRegisterModel"
    private var consumerLoginRequest: ConsumerLoginRequest =
        ConsumerLoginRequest(AUTH_TYPE.MOBILE.name, null, null)
    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var consumerRegisterRequest: ConsumerCreateRequest? =
        ConsumerCreateRequest(null, AUTH_TYPE.MOBILE.name, null)
    private var consumerAuthDetailResponse: MutableLiveData<ConsumerAuthDetailResponse>? = null

    val phoneWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            consumerRegisterRequest!!.authId = edit.toString()
        }
    }

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            consumerRegisterRequest!!.password = edit.toString()
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
        consumerLoginRequest = fbLoginRequest
        verifyGoogleFbSignUp()
    }

    fun onGoogleSignUpClick(googleLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onGoogleSignInClick")
        consumerLoginRequest = googleLoginRequest
        verifyGoogleFbSignUp()
    }
    /**
     * method to call API to verify signIn credentials
     */
    private fun verifyGoogleFbSignUp() {
        if (consumerLoginRequest.id != null && consumerLoginRequest.token != null) {
            val disposable = HanglooseApp.getApiService()!!.consumerLogin(consumerLoginRequest)
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.i(TAG, "success login")
                    consumerAuthDetailResponse!!.value = response
                }, {
                    Log.i(TAG, "error login")
                })

            compositeDisposable!!.add(disposable)
        } else {
        }
    }

    /**
     * method to call API for registering the user credentials
     */
    private fun registerUser() {
        Log.i(TAG, "Register request" + consumerRegisterRequest.toString())
        val disposable = HanglooseApp.getApiService()!!.consumerRegister(consumerRegisterRequest!!)
            .subscribeOn(HanglooseApp.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.i(TAG, """success register$response""")
                consumerAuthDetailResponse!!.value = response
            }, {
                Log.i(TAG, "error register$it")
            })

        compositeDisposable!!.add(disposable)
    }

    fun loginResponse(): MutableLiveData<ConsumerAuthDetailResponse>? {
        return consumerAuthDetailResponse
    }

    private fun unSubscribeFromObservable() {
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed) {
            compositeDisposable!!.dispose()
        }
    }

    fun reset() {
        unSubscribeFromObservable()
        compositeDisposable = null
    }
}