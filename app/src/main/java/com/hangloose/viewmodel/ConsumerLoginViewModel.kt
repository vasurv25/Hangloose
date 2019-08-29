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
import com.hangloose.model.Activities
import com.hangloose.model.Adventures
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.ui.model.SelectionList
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.utils.MESSAGE_KEY
import com.hangloose.utils.validatePhoneNumber
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
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
    var isVisible = ObservableBoolean()
    private var mPhoneNumber: String? = null
    private var mSelectionList: MutableLiveData<SelectionList> = MutableLiveData()

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
            mConsumerLoginRequest.token = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            if (!edit.isNullOrEmpty()) {
                isPasswordValid.set(true)
                isPasswordValid.notifyChange()
            }
        }
    }

    fun onSignInClick(view: View) {
        Log.i(TAG, "onSignInClick")
        val validPhone = validatePhoneNumber(mPhoneNumber)
        isPhoneValid.set(validPhone)
        isPhoneValid.notifyChange()
        if (validPhone && passwordValidate()) {
            mConsumerLoginRequest.id = "+91$mPhoneNumber"
            verifySignIn()
        }
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
            .doOnSubscribe {
                isVisible.set(true)
            }
            .doFinally {
                isVisible.set(false)
            }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    mConsumerAuthDetailResponse.value = response
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
    }

    fun selectionListApiRequest(mHeader: String?) {

        val callActivities = getApiService()!!.getActivities(mHeader!!)
        var callAdventures = getApiService()!!.getAdventures(mHeader)

        val disposable =
            Observable.zip(callActivities, callAdventures,
                BiFunction<Response<List<Activities>>, Response<List<Adventures>>, SelectionList> { t1, t2 ->
                    SelectionList(
                        t1.body()!!,
                        t2.body()!!
                    )
                })
                .subscribeOn(subscribeScheduler())
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

    fun loginResponse(): MutableLiveData<Response<ConsumerAuthDetailResponse>> {
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