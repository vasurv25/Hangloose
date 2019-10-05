package com.hangloose.ui.activities

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivityResetBinding
import com.hangloose.utils.PASSWORD_CONFIRM_PASSWORD_DOES_NOT_MATCH
import com.hangloose.utils.VALID_PASSWORD
import com.hangloose.utils.hideSoftKeyboard
import com.hangloose.utils.showSnackBar
import com.hangloose.viewmodel.ResetPasswordViewModel
import kotlinx.android.synthetic.main.activity_reset.*
import kotlinx.android.synthetic.main.activity_sign_up.ilConfirmPassword
import kotlinx.android.synthetic.main.activity_sign_up.ilPassword
import retrofit2.Response

class ResetActivity : BaseActivity() {
    override fun init() {

    }

    private var TAG = "ResetActivity"
    private var mActivityResetBinding: ActivityResetBinding? = null
    private lateinit var mResetPasswordViewModel: ResetPasswordViewModel
    private var mNavigationFlag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        mNavigationFlag = intent.getIntExtra("flag", 0)
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
        if (mNavigationFlag == 0) {
            val intent = Intent(this@ResetActivity, SignInActivity::class.java)
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } else {
            val intent = Intent(this@ResetActivity, TabsActivity::class.java)
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
    /**
     * method to dismiss keyboard on outside touch
     */
    fun onOutsideTouch(view: View) {
        hideSoftKeyboard(this)
    }
}
