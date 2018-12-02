package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hangloose.R
import com.hangloose.ui.model.ConsumerDetails
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerOTPRequest
import com.hangloose.model.ConsumerOtpVerifyRequest
import com.hangloose.ui.model.ConsumerData
import com.hangloose.utils.OTP_RECOGNIZE
import com.hangloose.utils.hideSoftKeyboard
import com.hangloose.utils.showSnackBar
import com.hangloose.viewmodel.ConsumerOTPViewModel
import kotlinx.android.synthetic.main.activity_otp.*

class OTPActivity : BaseActivity(), View.OnClickListener {

    private val TAG: String = "OTPActivity"
    private var mConsumerOtpViewModel: ConsumerOTPViewModel = ConsumerOTPViewModel()
    private var mConsumerOTPRequest: ConsumerOTPRequest =
        ConsumerOTPRequest(null, null)
    private var mConsumerVerifyOTPRequest: ConsumerOtpVerifyRequest =
        ConsumerOtpVerifyRequest(null, null, "VERIFY_MOBILE")
    private var mOtpNavigation: String? = null
    private var mPhoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        mOtpNavigation = intent.getStringExtra(getString(R.string.key_otp_recognize))
        mPhoneNumber = intent.getStringExtra(getString(R.string.key_mobile_no))
        if (mOtpNavigation!!.equals(OTP_RECOGNIZE.REGISTER_OTP)) {
            tvMobileNumber.text = ConsumerDetails.consumerData!!.mobile
        } else {
            tvMobileNumber.text = mPhoneNumber
        }
        initBinding()
    }

    private fun addTextWatcher() {
        otpEdittext1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (otpEdittext1.text.length == 1) {
                    otpEdittext2.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        otpEdittext2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (otpEdittext2.text.length == 1) {
                    otpEdittext3.requestFocus()
                }
                if (otpEdittext2.text.isEmpty()) {
                    otpEdittext1.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        otpEdittext3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (otpEdittext3.text.length == 1) {
                    otpEdittext4.requestFocus()
                }
                if (otpEdittext3.text.isEmpty()) {
                    otpEdittext2.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        otpEdittext4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (otpEdittext4.text.isEmpty()) {
                    otpEdittext3.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun init() {
        btnNext.setOnClickListener(this)
        addTextWatcher()
    }

    override fun onClick(view: View) {
        when (view.id) {
            btnNext.id -> {
                onNextClick()
            }
        }
    }

    /**
     * method to intialize data binding with view
     */
    private fun initBinding() {
        mConsumerOtpViewModel = ViewModelProviders.of(this).get(ConsumerOTPViewModel::class.java)
        mConsumerOtpViewModel.otpResponse()
            ?.observe(this, Observer<retrofit2.Response<ConsumerAuthDetailResponse>> { t ->
                Log.i(TAG, "onChanged")
                if (mOtpNavigation.equals(OTP_RECOGNIZE.RESET_OTP.name)) {
                    val consumerDetails = t!!.body()!!.consumer!!
                    val headers = t.headers()
                    var typeList = t!!.body()!!.consumerAuths!!.map { it.type }
                    var type = typeList.get(0)
                    val consumerData = ConsumerData(
                        headers.get("X-AUTH-TOKEN").toString(),
                        null,
                        consumerDetails.id,
                        consumerDetails.mobile,
                        type)
                    ConsumerDetails.consumerData = consumerData
                    Log.i(TAG, """onChanged : ${ConsumerDetails.consumerData}""")
                    onNavigateResetPasswordScreen()
                } else {
                    Toast.makeText(this, getString(R.string.user_registration_msg), Toast.LENGTH_LONG).show()
                    onNavigateLocationScreen()
                }
            })

        mConsumerOtpViewModel.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                llOTP,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
    }

    private fun onNextClick() {
        hideSoftKeyboard(this)
        if (otpEdittext1.text.isNotEmpty() && otpEdittext2.text.isNotEmpty() &&
            otpEdittext3.text.isNotEmpty() && otpEdittext4.text.isNotEmpty()
        ) {
            val otp = "${otpEdittext1.text}${otpEdittext2.text}${otpEdittext3.text}${otpEdittext4.text}".toInt()
            Log.i(TAG, "onNextClick$otp")
            if (mOtpNavigation!!.equals(OTP_RECOGNIZE.REGISTER_OTP.name)) {
                mConsumerOTPRequest.otp = otp.toString()
                Log.i(TAG, "Register OTP")
                mConsumerOTPRequest.mobileNo = ConsumerDetails.consumerData!!.mobile
                mConsumerOtpViewModel.verifyOtpForRegistration(mConsumerOTPRequest)
            } else {
                mConsumerVerifyOTPRequest.id = mPhoneNumber
                Log.i(TAG, "mobileNo ${mConsumerVerifyOTPRequest.id}")
                mConsumerVerifyOTPRequest.otp = otp.toString()
                mConsumerVerifyOTPRequest.otpRequestReason = "VERIFY_MOBILE"
                mConsumerOtpViewModel.verfiyOtpForResetPass(mConsumerVerifyOTPRequest)
            }
        }
    }

    private fun onNavigateLocationScreen() {
        var intent = Intent(this@OTPActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun onNavigateResetPasswordScreen() {
        var intent = Intent(this@OTPActivity, ResetActivity::class.java)
        startActivity(intent)
    }

    /**
     * method to dismiss keyboard on outside touch
     */
    fun onOutsideTouch(view: View) {
        hideSoftKeyboard(this)
    }
}