package com.hangloose.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.hangloose.R
import com.hangloose.databinding.ActivityOtpBinding
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.viewmodel.ConsumerOTPViewModel
import android.arch.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_otp.*

class OTPActivity : BaseActivity() {

    private val TAG: String = "OTPActivity"
    private var mActivityOtpBinding: ActivityOtpBinding? = null
    private var mConsumerOtpViewModel: ConsumerOTPViewModel = ConsumerOTPViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        initBinding()
        pvOTP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d(
                    TAG,
                    "onTextChanged() called with: s = [$s], start = [$start], before = [$before], count = [$count]"
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun init() {}

    /**
     * method to intialize data binding with view
     */
    private fun initBinding() {
        mActivityOtpBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        mActivityOtpBinding!!.consumerOTPViewModel = mConsumerOtpViewModel
        mConsumerOtpViewModel = ViewModelProviders.of(this).get(ConsumerOTPViewModel::class.java)
        mConsumerOtpViewModel.loginResponse()
            ?.observe(this, Observer<retrofit2.Response<ConsumerAuthDetailResponse>> { t ->
                Log.i(TAG, "onChanged")
            })
    }
}