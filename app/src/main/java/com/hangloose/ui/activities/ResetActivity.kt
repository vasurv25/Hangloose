package com.hangloose.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivityResetBinding
import com.hangloose.utils.PASSWORD_CONFIRM_PASSWORD_DOES_NOT_MATCH
import com.hangloose.utils.VALID_PASSWORD
import com.hangloose.viewmodel.ResetPasswordViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class ResetActivity : AppCompatActivity() {

    private var TAG = "ResetActivity"
    private var mActivityResetBinding: ActivityResetBinding? = null
    private lateinit var mResetPasswordViewModel: ResetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        mActivityResetBinding = DataBindingUtil.setContentView(this, R.layout.activity_reset)
        mResetPasswordViewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel::class.java)
        mActivityResetBinding!!.resetPasswordViewModel = mResetPasswordViewModel
        mResetPasswordViewModel.isPasswordValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                ilPassword.isErrorEnabled = !(sender as ObservableBoolean).get()
                if (ilPassword.isErrorEnabled) {
                    ilPassword.error = VALID_PASSWORD
                }
            }
        })
        mResetPasswordViewModel.isConfirmPasswordValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                ilConfirmPassword.isErrorEnabled = !(sender as ObservableBoolean).get()
                if (ilConfirmPassword.isErrorEnabled) {
                    ilConfirmPassword.error = PASSWORD_CONFIRM_PASSWORD_DOES_NOT_MATCH
                }
            }
        })
    }

    fun onCancelClick(view: View) {
        Log.i(TAG, "onCancelClick")
    }
}
