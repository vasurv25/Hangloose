package com.hangloose

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.hangloose.database.RoomDBHandler
import com.hangloose.network.ApiInf
import com.hangloose.network.RetrofitApiHandler
import com.hangloose.ui.receiver.EmptyDBReceiver
import com.hangloose.utils.REQUEST_CODE_DB_DELETE
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.io.File
import java.util.*

class HanglooseApp : Application() {

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
        setAlarmForDB()
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
        if (dir != null && dir!!.isDirectory()) {
            val children = dir!!.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    private fun setAlarmForDB() {
        val currentTime = System.currentTimeMillis()
        Log.d("Hangloose App", "CurrentTime :" + currentTime)
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 22)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        if (currentTime <= cal.timeInMillis) {
            val intent = Intent(this, EmptyDBReceiver::class.java)
            //intent.putExtra()
            val pendingIntent =
                PendingIntent.getBroadcast(this, REQUEST_CODE_DB_DELETE, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Toast.makeText(
                this, "Alarm will start at time specified",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}