package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivityAccountSettingsBinding
import com.hangloose.model.ConsumerResendOtpRequest
import com.hangloose.utils.*
import com.hangloose.utils.PreferenceHelper.get
import com.hangloose.viewmodel.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.activity_account_settings.*
import retrofit2.Response
import java.util.regex.Pattern

class AccountSettingsActivity : BaseActivity() {
    override fun init() {

    }

    private val TAG: String = "AccountSettingsActivity"
    private var mActivityAccountSettingsBinding: ActivityAccountSettingsBinding? = null
    private lateinit var mForgotPasswordViewModel: ForgotPasswordViewModel
    var isPhoneValid = ObservableBoolean()
    private var mConsumerResendOtpRequest: ConsumerResendOtpRequest? =
        ConsumerResendOtpRequest(AUTH_TYPE.MOBILE.name, null, "VERIFY_MOBILE")
    private var mNavigationFlag = 0
    private var mPreference: SharedPreferences? = null
    private var name: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        mNavigationFlag = intent.getIntExtra("flag", 0)
        mPreference = PreferenceHelper.defaultPrefs(this)
        setUpUI()
    }

    private fun setUpUI() {
        name = mPreference!![KEY_USER_NAME]
        email = mPreference!![KEY_EMAIL_ID]
        if (stringToNumber(name!!)) {
            textLayoutName.hint = "Phone Number"
        } else {
            textLayoutName.hint = "Name"
        }
        editName.setText(name)
        if (email != null) {
            editEmail.setText(email)
            tvResetPassword.visibility = View.GONE
        } else {
            textLayoutEmail.visibility = View.GONE
            tvResetPassword.visibility = View.VISIBLE
        }
    }

    /**
     * Check if name field contains number or name
     */
    private fun stringToNumber(string: String): Boolean {
        return Pattern.compile("[0-9]").matcher(string).find()
    }

    private fun initBinding() {
        mActivityAccountSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_settings)
        mActivityAccountSettingsBinding!!.clickHandler = this
        mForgotPasswordViewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        mActivityAccountSettingsBinding!!.forgotPasswordViewModel = mForgotPasswordViewModel
        mForgotPasswordViewModel.initiateOTPResponse()
            .observe(this, Observer<Response<Int>> {
                onNavigateOTPScreen()
            })
    }

    fun onNextClick(view: View) {
        mConsumerResendOtpRequest!!.id = name
        mForgotPasswordViewModel.initiateForgotPassword(mConsumerResendOtpRequest!!)

    }

    fun onCloseClick(view: View) {
        this@AccountSettingsActivity.finish()
    }

    private fun onNavigateOTPScreen() {
        val intent = Intent(this@AccountSettingsActivity, OTPActivity::class.java)
        intent.putExtra(getString(R.string.key_otp_recognize), OTP_RECOGNIZE.RESET_OTP.name)
        intent.putExtra(getString(R.string.key_mobile_no), mConsumerResendOtpRequest!!.id)
        intent.putExtra("flag", mNavigationFlag)
        startActivity(intent)
    }
}
