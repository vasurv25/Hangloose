package com.hangloose.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivityForgotPasswordBinding
import com.hangloose.utils.VALID_PHONE
import com.hangloose.utils.hideSoftKeyboard
import com.hangloose.viewmodel.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.activity_sign_in.*

class ForgotPasswordActivity : AppCompatActivity() {

    private var mActivityForgotPasswordBinding: ActivityForgotPasswordBinding? = null
    private lateinit var mForgotPasswordViewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        mActivityForgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        mActivityForgotPasswordBinding!!.clickHandler = this
        mForgotPasswordViewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        mActivityForgotPasswordBinding!!.forgotPasswordViewModel = mForgotPasswordViewModel
        mForgotPasswordViewModel.isPhoneValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                textLayoutPhone.isErrorEnabled = !(sender as ObservableBoolean).get()
                if (textLayoutPhone.isErrorEnabled) {
                    textLayoutPhone.error = VALID_PHONE
                }
            }
        })
    }

    fun onCloseClick(view: View) {
        this@ForgotPasswordActivity.finish()
    }

    /**
     * method to dismiss keyboard on outside touch
     */
    fun onOutsideTouch(view: View) {
        hideSoftKeyboard(this)
    }
}
