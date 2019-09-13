package com.hangloose

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.util.Log
import com.hangloose.database.RoomDBHandler
import com.hangloose.network.ApiInf
import com.hangloose.network.RetrofitApiHandler
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.io.File
import com.hangloose.utils.getCurrentDate


class HanglooseApp : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        api = RetrofitApiHandler(this).create()
        roomDatabaseHandler = RoomDBHandler(this)
        instance = this
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/segoe_ui.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build()
        )
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    companion object {
        private var api: ApiInf? = null
        private var mScheduler: Scheduler? = null
        private var instance: HanglooseApp? = null

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

        fun getInstance() : HanglooseApp {
            return instance!!
        }
    }

    fun clearApplicationData() {
        val cache = cacheDir
        val appDir = File(cache.parent)
        if (appDir.exists()) {
            val children = appDir.list()
            for (s in children) {
                if (s != "lib") {
                    deleteDir(File(appDir, s))
                    Log.i("TAG", "File /data/data/APP_PACKAGE/$s DELETED ")
                }
            }
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("Awww", "App in background")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("Yeeey", "App in foreground")
        checkForOldDateRetros()
    }

    private fun checkForOldDateRetros() {
        getDataHandler()!!.emptyRestroForOldDate(getCurrentDate())
    }
}