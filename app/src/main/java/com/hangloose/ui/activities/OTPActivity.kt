package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.R
import com.hangloose.model.ConsumerDetails
import com.hangloose.network.ConsumerAuthDetailResponse
import com.hangloose.network.ConsumerOTPRequest
import com.hangloose.viewmodel.ConsumerOTPViewModel
import kotlinx.android.synthetic.main.activity_otp.*

class OTPActivity : BaseActivity(), View.OnClickListener {

    private val TAG: String = "OTPActivity"
    private var mConsumerOtpViewModel: ConsumerOTPViewModel = ConsumerOTPViewModel()
    private var mConsumerOTPRequest: ConsumerOTPRequest =
        ConsumerOTPRequest(null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
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
            })
    }

    private fun onNextClick() {
        if (otpEdittext1.text.isNotEmpty() && otpEdittext2.text.isNotEmpty() &&
            otpEdittext3.text.isNotEmpty() && otpEdittext4.text.isNotEmpty()
        ) {
            val otp = "${otpEdittext1.text}${otpEdittext2.text}${otpEdittext3.text}${otpEdittext4.text}".toInt()
            Log.i(TAG, "onNextClick$otp")
            mConsumerOTPRequest.otp = otp.toString()
            mConsumerOTPRequest.mobileNo = ConsumerDetails.consumerData!!.mobile
            mConsumerOtpViewModel.verifyOTP(mConsumerOTPRequest)
        }
    }
}