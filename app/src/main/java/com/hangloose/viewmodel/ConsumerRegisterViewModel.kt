package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerCreateRequest
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.utils.MESSAGE_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import retrofit2.Response

class ConsumerRegisterViewModel : ViewModel() {

    private val TAG = "ConsumerRegisterModel"
    private var mConsumerLoginRequest: ConsumerLoginRequest =
        ConsumerLoginRequest(AUTH_TYPE.MOBILE.name, null, null)
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var mConsumerRegisterRequest: ConsumerCreateRequest? =
        ConsumerCreateRequest(null, AUTH_TYPE.MOBILE.name, null)
    private var mConsumerAuthDetailResponse: MutableLiveData<Response<ConsumerAuthDetailResponse>> = MutableLiveData()
    private var mConfirmPassword: String? = null
    var isVisible = ObservableBoolean()
    var isPhoneValid = ObservableBoolean()
    var isPasswordValid = ObservableBoolean()
    var isConfirmPasswordValid = ObservableBoolean()

    private var mPhoneNumber: String? = null
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()

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
            mConsumerRegisterRequest!!.password = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            //passwordValidate()
        }
    }

    val confirmPasswordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mConfirmPassword = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            //confirmPasswordValidate()
        }
    }

    /**
     * method to perform click on signUp button after user enters mobile number and password
     * @view view clicked
     */
    fun onSignUpClick(view: View) {
        Log.i(TAG, "onSignUpClick")
        if (phoneValidate() && passwordValidate() && confirmPasswordValidate()) {
            mConsumerRegisterRequest!!.authId = "+91$mPhoneNumber"
            registerUser()
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
        var isValid = !mConsumerRegisterRequest!!.password.isNullOrEmpty()
        if (isValid) {
            isValid = mConsumerRegisterRequest!!.password!!.length >= 6
        }
        isPasswordValid.set(isValid)
        isPasswordValid.notifyChange()
        return isValid
    }

    private fun confirmPasswordValidate(): Boolean {
        var isValid = !mConfirmPassword.isNullOrEmpty()
        if (isValid) {
            isValid = mConsumerRegisterRequest!!.password == mConfirmPassword
        }
        isConfirmPasswordValid.set(isValid)
        isConfirmPasswordValid.notifyChange()
        return isValid
    }

    fun onFacebookSignUpClick(fbLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onFacebookSignInClick")
        mConsumerLoginRequest = fbLoginRequest
        verifyGoogleFbSignUp(AUTH_TYPE.FACEBOOK.name)
    }

    fun onGoogleSignUpClick(googleLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onGoogleSignInClick")
        mConsumerLoginRequest = googleLoginRequest
        verifyGoogleFbSignUp(AUTH_TYPE.GOOGLE.name)
    }

    /**
     * method to call API to verify signIn credentials
     */
    private fun verifyGoogleFbSignUp(name: String) {
        if (mConsumerLoginRequest.id != null && mConsumerLoginRequest.token != null) {
            val disposable = HanglooseApp.getApiService()!!.consumerLogin(mConsumerLoginRequest)
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it ->
                    it.body()!!.consumer!!.authType = name
                    return@map it
                }
                .doOnSubscribe {
                    isVisible.set(true)
                }
                .doFinally {
                    isVisible.set(false)
                }
                .subscribe({ response ->
                    Log.i(TAG, "success login")
                    if (response.isSuccessful) {
                        mConsumerAuthDetailResponse.value = response
                        Log.i(TAG, """success register${response.body()!!.consumer!!.authType}""")
                        val header = response.headers()
                        Log.i(TAG, "Header : ${header.get("X-AUTH-TOKEN")}")
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

    /**
     * method to call API for registering the user credentials
     */
    private fun registerUser() {
        Log.i(TAG, "Register request" + mConsumerRegisterRequest.toString())
        val disposable = HanglooseApp.getApiService()!!.consumerRegister(mConsumerRegisterRequest!!)
            .subscribeOn(HanglooseApp.subscribeScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it ->
                it.body()!!.consumer!!.authType = AUTH_TYPE.MOBILE.name
                return@map it
            }
            .doOnSubscribe {
                isVisible.set(true)
            }
            .doFinally {
                isVisible.set(false)
            }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    mConsumerAuthDetailResponse.value = response
                    Log.i(TAG, """success register${response.body()!!.consumer!!.authType}""")
                    val header = response.headers()
                    Log.i(TAG, "Header : ${header.get("X-AUTH-TOKEN")}")
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    mShowErrorSnackBar.value = jObjError.getString(MESSAGE_KEY)
                }
            }, {
                Log.i(TAG, "error register$it")
                mShowErrorSnackBar.value = it.localizedMessage
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