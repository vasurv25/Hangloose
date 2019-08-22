package com.hangloose.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hangloose.HanglooseApp.Companion.getDataHandler

class LikedDBService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        getDataHandler()!!.emptyLikeDislikeRestaurants()
    }
}
