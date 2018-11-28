package com.hangloose.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.hangloose.model.ResetPassword
import com.hangloose.utils.validatePassword

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
        }

        override fun afterTextChanged(edit: Editable?) {
//            if (validateConfirmPassword(edit.toString())) {
//                isConfirmPasswordValid.set(true)
//                isConfirmPasswordValid.notifyChange()
//            }
        }
    }

    fun onResetClick(view: View) {
//        val validPassword = validatePassword()
//        isPasswordValid.set(validPassword)
//        isPasswordValid.notifyChange()
//
//        val validConfirmPassword = validateConfirmPassword()
//        isConfirmPasswordValid.set(validConfirmPassword)
//        isConfirmPasswordValid.notifyChange()
//        if (validPassword && validConfirmPassword) {
//            //action
//        }
    }
}