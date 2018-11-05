package com.hangloose.handler.network.retrofit

import android.content.Context
import com.hangloose.utils.isOnline
import okhttp3.Interceptor
import okhttp3.Response

/**
 * interceptor to check connectivity
 */
class ConnectivityInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline(context)) {
            throw NoConnectivityException(context)
        }
        val builder = chain!!.request().newBuilder()
        return chain.proceed(builder.build())
    }
}