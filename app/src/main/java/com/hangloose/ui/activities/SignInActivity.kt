package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.hangloose.R
import com.hangloose.databinding.ActivitySignInBinding
import com.hangloose.listener.ResponseListener
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.viewmodel.ConsumerViewModel

class SignInActivity : AppCompatActivity(), ResponseListener {

    private val TAG = "SignInActivity"
    private var consumerViewModel: ConsumerViewModel = ConsumerViewModel()
    private var activitySignInBinding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        activitySignInBinding = DataBindingUtil.setContentView<ActivitySignInBinding>(this, R.layout.activity_sign_in)
        activitySignInBinding!!.consumerViewModel = consumerViewModel

        consumerViewModel = ViewModelProviders.of(this).get(ConsumerViewModel::class.java)

        consumerViewModel.loginResponse()?.observe(this, Observer<ConsumerAuthDetailResponse> {
            fun onChanged(t: ConsumerAuthDetailResponse?) {
                if (t != null) {
                    Log.i(TAG, "onChanged")
                }
            }
        })
    }

    override fun onSuccess(response: Any) {
        Log.i(TAG, "onSuccess")
    }

    override fun onError(errorMessage: String?, ex: Throwable) {
        Log.i(TAG, errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        consumerViewModel.reset()
    }
}