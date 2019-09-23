package com.hangloose.ui.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.hangloose.ui.model.RestaurantData
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        init()
        Fabric.with(this, Crashlytics())
    }

    abstract fun init()

    companion object {
        var mRestaurantData : ArrayList<RestaurantData> = ArrayList()
        var mEntireRestaurantData:ArrayList<RestaurantData> = ArrayList()

        fun setRestaurantData(restaurantData: ArrayList<RestaurantData>) {
            mRestaurantData = restaurantData
        }

        fun getRestaurantData(): ArrayList<RestaurantData> {
            return mRestaurantData
        }

        fun setEntireRestaurantData(entireRestaurantData: ArrayList<RestaurantData>) {
            mEntireRestaurantData = entireRestaurantData
        }

        fun getEntireRestaurantData(): ArrayList<RestaurantData> {
            return mEntireRestaurantData
        }
    }
}