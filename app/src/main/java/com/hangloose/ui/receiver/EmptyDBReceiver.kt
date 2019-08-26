package com.hangloose.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hangloose.HanglooseApp

class EmptyDBReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Broadcast receiver for DB deletion",
            Toast.LENGTH_SHORT).show();
        HanglooseApp.getDataHandler()!!.emptyLikeDislikeRestaurants()
    }
}