package com.hangloose

import android.app.Application
import com.hangloose.handler.DataHandler
import com.hangloose.handler.network.retrofit.RetrofitApiHandler

class HanglooseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val retrofitApiHandler = RetrofitApiHandler(this)
        mDataHandler = DataHandler(retrofitApiHandler, applicationContext)
    }
    companion object {
        private var mDataHandler: DataHandler? = null

        fun getDataHandler(): DataHandler? {
            return mDataHandler
        }
    }
}