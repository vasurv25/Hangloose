package com.hangloose.ui.activities

import android.annotation.SuppressLint
import android.annotation.TargetApi
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
import com.hangloose.ui.fragment.ProfileFragment
import com.hangloose.ui.fragment.RestaurantFragment
import com.hangloose.ui.fragment.SearchFragment
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA
import com.hangloose.utils.KEY_RESTAURANT_DATA
import kotlinx.android.synthetic.main.activity_tab.tabLayout

class TabsActivity : BaseActivity(), TabLayout.OnTabSelectedListener {

    private val TAG = "TabsActivity"
    private var mRestaurantData: ArrayList<RestaurantData>? = null
    private var mAddress: String? = null
    private val navLabels =
        intArrayOf(R.string.nav_home, R.string.nav_search, R.string.nav_profile)
    private val navIcons =
        intArrayOf(R.drawable.home, R.drawable.search, R.drawable.profile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        mRestaurantData = intent.getParcelableArrayListExtra(KEY_RESTAURANT_DATA)
        mAddress = intent.getStringExtra("123")
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

    @TargetApi(Build.VERSION_CODES.M)
    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab!!.customView!!.findViewById<ImageView>(R.id.tabIcon)
            .setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_IN)
        tab.customView!!.findViewById<TextView>(R.id.tabText)
            .setTextColor(resources.getColor(R.color.colorGrey, null))
    }

    @TargetApi(Build.VERSION_CODES.M)
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
        tab!!.customView!!.findViewById<ImageView>(R.id.tabIcon)
            .setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
        tab.customView!!.findViewById<TextView>(R.id.tabText)
            .setTextColor(resources.getColor(R.color.colorPrimary, null))
    }

    fun replaceFragment(fragment: Fragment) {
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        if (fragment is RestaurantFragment || fragment is SearchFragment) {
            val args = Bundle()
            args.putString("abc", mAddress)
            args.putParcelableArrayList(KEY_DATA, mRestaurantData)
            fragment.arguments = args
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
