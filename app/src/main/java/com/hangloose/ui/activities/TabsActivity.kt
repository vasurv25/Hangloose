package com.hangloose.ui.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.hangloose.R
import com.hangloose.database.LikedDBInf
import com.hangloose.ui.fragment.*
import com.hangloose.ui.model.RestaurantData
import com.hangloose.ui.service.LikedDBService
import com.hangloose.utils.*
import com.hangloose.utils.PreferenceHelper.get
import kotlinx.android.synthetic.main.activity_tab.tabLayout
import java.util.*
import kotlin.collections.ArrayList

class TabsActivity : BaseActivity(), TabLayout.OnTabSelectedListener, RestaurantFragment.LocationNavigationListener {

    private val TAG = "TabsActivity"
    private var mRestaurantData: ArrayList<RestaurantData>? = null

    private var mPreference: SharedPreferences? = null

    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()
    private val navLabels =
        intArrayOf(R.string.nav_home, R.string.nav_search, R.string.nav_profile)
    private val navIcons =
        intArrayOf(R.drawable.home, R.drawable.search, R.drawable.profile)

    var mHeader: String? = null

    private var mEntireRestaurantData = ArrayList<RestaurantData>()
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        mPreference = PreferenceHelper.defaultPrefs(this)

        mActivitiesSelectedList = intent.getStringArrayListExtra(KEY_ACTIVITIES_LIST)
        mAdventuresSelectedList = intent.getStringArrayListExtra(KEY_ADVENTURES_LIST)
        mRestaurantData = intent.getParcelableArrayListExtra(KEY_RESTAURANT_DATA)
        mEntireRestaurantData = intent.getParcelableArrayListExtra(KEY_ENTIRE_RESTAURANT_DATA)
        mLatitude = intent.getDoubleExtra(KEY_LATITUDE, 0.0)
        mLongitude = intent.getDoubleExtra(KEY_LONGTITUDE, 0.0)
        val headerToken: String? = mPreference!![X_AUTH_TOKEN]
        Log.i(TAG, """Header : $headerToken""")
        mHeader = headerToken
        Log.i(TAG, "LikedRestaurant data : $mRestaurantData")
        onTabSelected(tabLayout.getTabAt(0))

        setAlarmForDB()
    }

    private fun setAlarmForDB() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 2)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val intent = Intent(this, LikedDBService::class.java)
        val pendingIntent = PendingIntent.getService(this, REQUEST_CODE_DB_DELETE, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    @SuppressLint("NewApi")
    override fun init() {
        (0 until navLabels.size).forEach { i ->
            val tab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
            val tabLabel = tab.findViewById(R.id.tabText) as TextView
            val tabIcon = tab.findViewById(R.id.tabIcon) as ImageView
            tabLabel.text = getString(navLabels[i])
            /* if (i == 0) {
                 tabLabel.setTextColor(resources.getColor(R.color.colorPrimary, null))
                 tabIcon.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
             } else {
                 tabLabel.setTextColor(resources.getColor(R.color.colorGrey, null))
             }*/
            tabIcon.setImageResource(navIcons[i])
            tabLayout.addTab(tabLayout.newTab().setCustomView(tab))
        }

        tabLayout.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tab!!.customView!!.findViewById<ImageView>(R.id.tabIcon)
                .setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_IN)
            tab.customView!!.findViewById<TextView>(R.id.tabText)
                .setTextColor(resources.getColor(R.color.colorGrey, null))
        } else {
            tab!!.customView!!.findViewById<ImageView>(R.id.tabIcon)
                .setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_IN)
            tab.customView!!.findViewById<TextView>(R.id.tabText)
                .setTextColor(resources.getColor(R.color.colorGrey))
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when {
            tabLayout.selectedTabPosition == 0 -> {
                replaceFragment(RestaurantFragment())
            }
            tabLayout.selectedTabPosition == 1 -> {
                replaceFragment(SearchFragment())

            }
            tabLayout.selectedTabPosition == 2 -> {
                replaceFragment(ProfileFragment())
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tab!!.customView!!.findViewById<ImageView>(R.id.tabIcon)
                .setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
            tab.customView!!.findViewById<TextView>(R.id.tabText)
                .setTextColor(resources.getColor(R.color.colorPrimary))
        } else {
            tab!!.customView!!.findViewById<ImageView>(R.id.tabIcon)
                .setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
            tab.customView!!.findViewById<TextView>(R.id.tabText)
                .setTextColor(resources.getColor(R.color.colorPrimary))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        Log.i(TAG, "LikedRestaurant data : $mRestaurantData")
        if (fragment is RestaurantFragment || fragment is SearchFragment) {
            val args = Bundle()
            args.putStringArrayList(KEY_ACTIVITIES_LIST, mActivitiesSelectedList)
            args.putStringArrayList(KEY_ADVENTURES_LIST, mAdventuresSelectedList)
            args.putParcelableArrayList(KEY_DATA, mRestaurantData)
            args.putParcelableArrayList(KEY_ENTIRE_RESTAURANT_DATA, mEntireRestaurantData)
            args.putDouble(KEY_LATITUDE, mLatitude!!)
            args.putDouble(KEY_LONGTITUDE, mLongitude!!)
            fragment.arguments = args
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (tabLayout.selectedTabPosition != 0) {
            tabLayout.getTabAt(0)!!.select()
        } else {
            val intent = Intent(this, SelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun navigateToLocationActivityFromHomeFrag() {
        val intent = Intent(this, LocationSettingActivity::class.java)
        intent.putStringArrayListExtra(KEY_ACTIVITIES_LIST, mActivitiesSelectedList)
        intent.putStringArrayListExtra(KEY_ADVENTURES_LIST, mAdventuresSelectedList)
        intent.putExtra(FLAG_LOCATION_NAVIGATION, 1)
        startActivityForResult(intent, START_LOCATION_SCREEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == START_LOCATION_SCREEN) {
            //var data = data!!.getParcelableArrayListExtra<RestaurantData>(KEY_RESTAURANT_DATA)
            if (data == null) {
                Log.d(TAG, "LikedRestaurant Data : $mRestaurantData")
                replaceFragment(RestaurantFragment())
            } else {
                mRestaurantData = data.getParcelableArrayListExtra(KEY_RESTAURANT_DATA)
                Log.d(TAG, "LikedRestaurant Data : $mRestaurantData")
                replaceFragment(RestaurantFragment())
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}
