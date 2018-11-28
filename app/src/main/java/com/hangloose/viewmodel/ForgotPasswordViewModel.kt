package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.hangloose.model.ForgotPassword
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.utils.validatePhoneNumber
import io.reactivex.disposables.CompositeDisposable

class ForgotPasswordViewModel : ViewModel() {

    val mForgotPasswordRequest = ForgotPassword(AUTH_TYPE.MOBILE.name, "")
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    var isPhoneValid = ObservableBoolean()
    var mShowErrorSnackBar: MutableLiveData<String> = MutableLiveData()

    val phoneWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mForgotPasswordRequest.id = edit.toString()
        }

        override fun afterTextChanged(edit: Editable?) {
            if (validatePhoneNumber(edit.toString())) {
                isPhoneValid.set(true)
                isPhoneValid.notifyChange()
            }
        }
    }

    fun onNextClick(view: View) {
        val validPhone = validatePhoneNumber(mForgotPasswordRequest.id)
        isPhoneValid.set(validPhone)
        isPhoneValid.notifyChange()
        if (validPhone) {
            //action
        }
    }
}