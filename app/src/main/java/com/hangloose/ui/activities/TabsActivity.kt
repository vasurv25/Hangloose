package com.hangloose.ui.activities

import android.annotation.SuppressLint
import android.annotation.TargetApi
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
import com.hangloose.ui.fragment.*
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.*
import com.hangloose.utils.PreferenceHelper.get
import kotlinx.android.synthetic.main.activity_tab.tabLayout

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

    var mHeader : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        mPreference = PreferenceHelper.defaultPrefs(this)
        mRestaurantData = intent.getParcelableArrayListExtra(KEY_RESTAURANT_DATA)
        mActivitiesSelectedList = intent.getStringArrayListExtra(KEY_ACTIVITIES_LIST)
        mAdventuresSelectedList = intent.getStringArrayListExtra(KEY_ADVENTURES_LIST)
        val headerToken: String? = mPreference!![X_AUTH_TOKEN]
        Log.i(TAG, """Header : $headerToken""")
        mHeader = headerToken
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        onTabSelected(tabLayout.getTabAt(0))
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
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        if (fragment is RestaurantFragment || fragment is SearchFragment) {
            val args = Bundle()
            args.putStringArrayList(KEY_ACTIVITIES_LIST, mActivitiesSelectedList)
            args.putStringArrayList(KEY_ADVENTURES_LIST, mAdventuresSelectedList)
            args.putParcelableArrayList(KEY_DATA, mRestaurantData)
            fragment.arguments = args
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, SelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun navigateToLocationActivityFromHomeFrag() {
        var intent = Intent(this, LocationSettingActivity::class.java)
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
                Log.d(TAG, "Restaurant Data : $mRestaurantData")
                replaceFragment(RestaurantFragment())
            } else {
                mRestaurantData = data.getParcelableArrayListExtra(KEY_RESTAURANT_DATA)
                Log.d(TAG, "Restaurant Data : $mRestaurantData")
                replaceFragment(RestaurantFragment())
            }
        }
    }
}
