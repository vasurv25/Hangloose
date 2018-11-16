package com.hangloose.network

import android.content.Context
import com.hangloose.R
import java.io.IOException

class NoConnectivityException(val context: Context) : IOException() {
    override fun getLocalizedMessage(): String {
        return context.getString(R.string.no_network_connectivity)
    }
}