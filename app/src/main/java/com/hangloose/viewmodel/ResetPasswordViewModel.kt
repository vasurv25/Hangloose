package com.hangloose.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.hangloose.model.ResetPassword

class ResetPasswordViewModel : ViewModel() {

    var mResetPasswordRequest: ResetPassword? = null
    private var mConfirmPassword: String? = null
    var isPasswordValid = ObservableBoolean()
    var isConfirmPasswordValid = ObservableBoolean()

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
        }
    }

    val confirmPasswordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(edit: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
        }
    }

    fun onResetClick(view: View) {
        if (passwordValidate() && confirmPasswordValidate()) {
            //action
        }
    }

    private fun passwordValidate(): Boolean {
        var isValid = !mResetPasswordRequest!!.newPassword.isNullOrEmpty()
        if (isValid) {
            isValid = mResetPasswordRequest!!.newPassword!!.length >= 6
        }
        isPasswordValid.set(isValid)
        isPasswordValid.notifyChange()
        return isValid
    }

    private fun confirmPasswordValidate(): Boolean {
        var isValid = !mResetPasswordRequest!!.newPassword.isNullOrEmpty()
        if (isValid) {
            isValid = mResetPasswordRequest!!.newPassword == mConfirmPassword
        }
        isConfirmPasswordValid.set(isValid)
        isConfirmPasswordValid.notifyChange()
        return isValid
    }
}