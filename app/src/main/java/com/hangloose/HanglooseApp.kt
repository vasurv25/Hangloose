package com.hangloose

import android.app.Application
import android.content.Context
import com.hangloose.network.ApiInf
import com.hangloose.network.RetrofitApiHandler
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class HanglooseApp : Application() {

    private var api: ApiInf? = null
    private var scheduler: Scheduler? = null

    companion object {
        fun create(context: Context): HanglooseApp {
            return HanglooseApp()[context]
        }
    }

    operator fun get(context: Context): HanglooseApp {
        return context.applicationContext as HanglooseApp
    }

    fun getApiService(): ApiInf {
        if (api == null) {
            api = RetrofitApiHandler(get(this)).create()
        }

        return api!!
    }

    fun setApiService(api: ApiInf) {
        this.api = api
    }

    fun subscribeScheduler(): Scheduler {
        if (scheduler == null) {
            scheduler = Schedulers.io()
        }

        return scheduler!!
    }

    fun setScheduler(scheduler: Scheduler) {
        this.scheduler = scheduler
    }
}