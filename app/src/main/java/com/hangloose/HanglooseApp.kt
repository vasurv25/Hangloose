package com.hangloose

import android.app.Application
import com.hangloose.database.RoomDBHandler
import com.hangloose.network.ApiInf
import com.hangloose.network.RetrofitApiHandler
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class HanglooseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        api = RetrofitApiHandler(this).create()
        roomDatabaseHandler = RoomDBHandler(this)
    }

    companion object {
        private var api: ApiInf? = null
        private var mScheduler: Scheduler? = null

        fun getApiService(): ApiInf? {
            return api!!
        }

        fun subscribeScheduler(): Scheduler {
            if (mScheduler == null) {
                mScheduler = Schedulers.io()
            }

            return mScheduler!!
        }

        fun setScheduler(scheduler: Scheduler) {
            this.mScheduler = scheduler
        }

        private var roomDatabaseHandler: RoomDBHandler? = null

        fun getDataHandler(): RoomDBHandler? {
            return roomDatabaseHandler
        }

    }
}