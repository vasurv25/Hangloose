package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivityForgotPasswordBinding
import com.hangloose.model.ConsumerResendOtpRequest
import com.hangloose.utils.*
import com.hangloose.viewmodel.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.activity_forgot_password.*
import retrofit2.Response

class ForgotPasswordActivity : BaseActivity() {
    override fun init() {

    }

    private val TAG: String = "ForgotPasswordActivity"
    private var mActivityForgotPasswordBinding: ActivityForgotPasswordBinding? = null
    private lateinit var mForgotPasswordViewModel: ForgotPasswordViewModel
    private var mPhoneNumber: String? = null
    var isPhoneValid = ObservableBoolean()
    private var mConsumerResendOtpRequest: ConsumerResendOtpRequest? =
        ConsumerResendOtpRequest(AUTH_TYPE.MOBILE.name, null, "VERIFY_MOBILE")
    private var mNavigationFlag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        mNavigationFlag = intent.getIntExtra("flag", 0)
    }

    private fun initBinding() {
        mActivityForgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        mActivityForgotPasswordBinding!!.clickHandler = this
        mForgotPasswordViewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        mActivityForgotPasswordBinding!!.forgotPasswordViewModel = mForgotPasswordViewModel
        mForgotPasswordViewModel.initiateOTPResponse()
            .observe(this, Observer<Response<Int>> {
                onNavigateOTPScreen()
            })

        isPhoneValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (!(sender as ObservableBoolean).get()) {
                    textLayoutPhone.error = VALID_PHONE
                } else {
                    textLayoutPhone.error = null
                }
            }
        })
        mForgotPasswordViewModel.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                llForgotPass,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
    }

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

    fun onNextClick(view: View) {
        hideSoftKeyboard(this)
        val validPhone = validatePhoneNumber(mPhoneNumber)
        isPhoneValid.set(validPhone)
        isPhoneValid.notifyChange()
        if (validPhone) {
            mConsumerResendOtpRequest!!.id = "+91$mPhoneNumber"
            mForgotPasswordViewModel.initiateForgotPassword(mConsumerResendOtpRequest!!)
        }
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

    private fun onNavigateOTPScreen() {
        val intent = Intent(this@ForgotPasswordActivity, OTPActivity::class.java)
        intent.putExtra(getString(R.string.key_otp_recognize), OTP_RECOGNIZE.RESET_OTP.name)
        intent.putExtra(getString(R.string.key_mobile_no), mConsumerResendOtpRequest!!.id)
        intent.putExtra("flag", mNavigationFlag)
        startActivity(intent)
    }
}
