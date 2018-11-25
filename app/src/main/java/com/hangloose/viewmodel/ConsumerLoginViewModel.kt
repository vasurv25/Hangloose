package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp.Companion.getApiService
import com.hangloose.HanglooseApp.Companion.subscribeScheduler
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.utils.MESSAGE_KEY
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
    var isPhoneValid = ObservableBoolean()
    var isPasswordValid = ObservableBoolean()
    var setVisibility: Int = View.GONE
    private var mPhoneNumber: String? = null

    val phoneWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mPhoneNumber = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            //phoneValidate()
        }
    }

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mConsumerLoginRequest.token = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            //passwordValidate()
        }
    }

    fun onSignInClick(view: View) {
        Log.i(TAG, "onSignInClick")
        if (phoneValidate() && passwordValidate()) {
            mConsumerLoginRequest!!.id = "+91$mPhoneNumber"
            setVisibility = View.VISIBLE
            verifySignIn()
        }
    }

    private fun phoneValidate(): Boolean {
        var isValid = !mPhoneNumber.isNullOrEmpty()
        if (isValid) {
            isValid = mPhoneNumber!!.length == 10
        }
        isPhoneValid.set(isValid)
        isPhoneValid.notifyChange()
        return isValid
    }

    private fun passwordValidate(): Boolean {
        val isValid = !mConsumerLoginRequest.token.isNullOrEmpty()
        isPasswordValid.set(isValid)
        isPasswordValid.notifyChange()
        return isValid
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
            .subscribe({ response ->
                setVisibility = View.GONE
                if (response.isSuccessful) {
                    mConsumerAuthDetailResponse.value = response
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    mShowErrorSnackBar.value = jObjError.getString(MESSAGE_KEY)
                }
            }, {
                Log.i(TAG, "error login")
                mShowErrorSnackBar.value = it.localizedMessage
            })
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