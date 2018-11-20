package com.hangloose.ui.activities

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private var mActivityForgotPasswordBinding: ActivityForgotPasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        mActivityForgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        mActivityForgotPasswordBinding!!.clickHandler = this
    }

    fun onCloseClick(view: View) {
        this@ForgotPasswordActivity.finish()
    }
}
