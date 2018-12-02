package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivityResetBinding
import com.hangloose.utils.PASSWORD_CONFIRM_PASSWORD_DOES_NOT_MATCH
import com.hangloose.utils.VALID_PASSWORD
import com.hangloose.utils.showSnackBar
import com.hangloose.viewmodel.ResetPasswordViewModel
import kotlinx.android.synthetic.main.activity_reset.*
import kotlinx.android.synthetic.main.activity_sign_up.ilConfirmPassword
import kotlinx.android.synthetic.main.activity_sign_up.ilPassword
import retrofit2.Response
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent
import com.hangloose.utils.hideSoftKeyboard

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

        mResetPasswordViewModel.verifyNewPassResponse().observe(this,
            Observer<Response<Int>> {
                onNavigateToSignInScreen()
            })

        mResetPasswordViewModel.isPasswordValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (!(sender as ObservableBoolean).get()) {
                    ilPassword.error = VALID_PASSWORD
                } else {
                    ilPassword.error = null
                }
            }
        })
        mResetPasswordViewModel.isConfirmPasswordValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (!(sender as ObservableBoolean).get()) {
                    ilConfirmPassword.error = PASSWORD_CONFIRM_PASSWORD_DOES_NOT_MATCH
                } else {
                    ilConfirmPassword.error = null
                }
            }
        })

        mResetPasswordViewModel.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                ll_Reset_Pass,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
    }

    fun onCancelClick(view: View) {
        Log.i(TAG, "onCancelClick")
        onNavigateToSignInScreen()
    }

    private fun onNavigateToSignInScreen() {
        val intent = Intent(this@ResetActivity, SignInActivity::class.java)
        intent.flags = FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
    /**
     * method to dismiss keyboard on outside touch
     */
    fun onOutsideTouch(view: View) {
        hideSoftKeyboard(this)
    }
}
