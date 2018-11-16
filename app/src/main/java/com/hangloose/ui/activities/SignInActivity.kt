package com.hangloose.ui.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.hangloose.databinding.ActivitySignInBinding
import com.hangloose.listener.ResponseListener
import com.hangloose.viewmodel.ConsumerViewModel
import java.util.Observer
import java.util.Observable
import com.hangloose.R

class SignInActivity : AppCompatActivity(), ResponseListener, Observer {

    private val TAG = "SignInActivity"
    private var consumerViewModel: ConsumerViewModel? = null
    private var activitySignInBinding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setUpObserver(consumerViewModel!!)
    }

    private fun initBinding() {
        consumerViewModel = ConsumerViewModel(this)
        activitySignInBinding = DataBindingUtil.setContentView<ActivitySignInBinding>(this, R.layout.activity_sign_in)
        activitySignInBinding!!.consumerViewModel = consumerViewModel
    }

    override fun update(p0: Observable?, p1: Any?) {
        Log.i(TAG, "update")
    }

    fun setUpObserver(observable: Observable) {
        observable.addObserver(this)
    }

    override fun onSuccess(response: Any) {
        Log.i(TAG, "onSuccess")
    }

    override fun onError(errorMessage: String?, ex: Throwable) {
        Log.i(TAG, errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        consumerViewModel!!.reset()
    }
}
