package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp
import com.hangloose.model.*
import com.hangloose.ui.model.SelectionList
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.utils.MESSAGE_KEY
import com.hangloose.utils.validateConfirmPassword
import com.hangloose.utils.validatePassword
import com.hangloose.utils.validatePhoneNumber
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
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
    private var mSelectionList: MutableLiveData<SelectionList> = MutableLiveData()

    private var mPhoneNumber: String? = null
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()

    val phoneWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mPhoneNumber = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            if (validatePhoneNumber(edit.toString())) {
                isPhoneValid.set(true)
                isPhoneValid.notifyChange()
            }
        }
    }

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mConsumerRegisterRequest!!.password = edit.toString()
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
            if (validateConfirmPassword(mConsumerRegisterRequest!!.password, edit.toString())) {
                isConfirmPasswordValid.set(true)
                isConfirmPasswordValid.notifyChange()
            }
        }
    }

    /**
     * method to perform click on signUp button after user enters mobile number and password
     * @view view clicked
     */
    fun onSignUpClick(view: View) {
        Log.i(TAG, "onSignUpClick")

        val validPhone = validatePhoneNumber(mPhoneNumber)
        isPhoneValid.set(validPhone)
        isPhoneValid.notifyChange()

        val validPassword = validatePassword(mConsumerRegisterRequest!!.password)
        isPasswordValid.set(validPassword)
        isPasswordValid.notifyChange()

        val validConfirmPassword = validateConfirmPassword(mConsumerRegisterRequest!!.password, mConfirmPassword)
        isConfirmPasswordValid.set(validConfirmPassword)
        isConfirmPasswordValid.notifyChange()

        if (validPhone && validPassword && validConfirmPassword) {
            mConsumerRegisterRequest!!.authId = "+91$mPhoneNumber"
            registerUser()
        }
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
                        Log.i(TAG, """success register${response.body()!!.consumer!!}""")
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
            .doOnSubscribe {
                isVisible.set(true)
            }
            .doFinally {
                isVisible.set(false)
            }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    mConsumerAuthDetailResponse.value = response
                    Log.i(TAG, """success register${response.body()!!.consumer!!}""")
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

    fun selectionListApiRequest(mHeader: String?) {

        var callActivities = HanglooseApp.getApiService()!!.getActivities(mHeader!!)
        var callAdventures = HanglooseApp.getApiService()!!.getAdventures(mHeader!!)

        val disposable =
            Observable.zip(callActivities, callAdventures,
                BiFunction<Response<List<Activities>>, Response<List<Adventures>>, SelectionList> { t1, t2 ->
                    SelectionList(
                        t1.body()!!,
                        t2.body()!!
                    )
                })
                .subscribeOn(HanglooseApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isVisible.set(true)
                }
                .doFinally {
                    isVisible.set(false)
                }
                .subscribe({
                    Log.i(TAG, "success login : $it")
                    mSelectionList.value = it
                }, {
                    Log.i(TAG, """error login : ${it.localizedMessage}""")
                    mShowErrorSnackBar.value = it.localizedMessage
                    // TODO if error related to token mis-match navigate user to SignIn Activity
                })

        mCompositeDisposable!!.add(disposable)
    }

    fun registerResponse(): MutableLiveData<Response<ConsumerAuthDetailResponse>> {
        Log.i(TAG, "Live Data")
        return mConsumerAuthDetailResponse
    }

    fun getSelectionList(): MutableLiveData<SelectionList> {
        return mSelectionList
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