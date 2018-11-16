package com.hangloose.utils

import android.content.Context
import android.net.ConnectivityManager
import com.hangloose.network.NoConnectivityException

/**
 * utilities for network
 */

private const val TAG: String = "NetworkUtils"

fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = connectivityManager.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}

/**
 * get retrofit no network error
 */
fun getRetrofitError(throwable: Throwable): String? {
    return (throwable as? NoConnectivityException)?.localizedMessage
}