package com.hangloose.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import com.hangloose.R
import com.hangloose.ui.fragment.ProfileFragment
import com.hangloose.ui.fragment.RestaurantFragment
import com.hangloose.ui.fragment.SearchFragment
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA
import com.hangloose.utils.KEY_RESTAURANT_DATA
import kotlinx.android.synthetic.main.activity_tab.*

class TabsActivity : BaseActivity() {

    private val TAG = "TabsActivity"
    private var mRestaurantData: ArrayList<RestaurantData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        mRestaurantData = intent.getParcelableArrayListExtra(KEY_RESTAURANT_DATA)
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        replaceFragment(RestaurantFragment())
    }

    @SuppressLint("NewApi")
    override fun init() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.search))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.profile))

        tabLayout.setTabTextColors(Color.parseColor("#cccccc"), Color.parseColor("#b72125"))
        tabLayout.getTabAt(0)!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)

        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab!!.icon!!.setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_IN)
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when {
                        tabLayout.selectedTabPosition == 0 -> {
                            tab!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
                            replaceFragment(RestaurantFragment())
                        }
                        tabLayout.selectedTabPosition == 1 -> {
                            tab!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
                            replaceFragment(SearchFragment())
                        }
                        tabLayout.selectedTabPosition == 2 -> {
                            tab!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
                            replaceFragment(ProfileFragment())
                        }
                    }
                }
            })
    }

    fun replaceFragment(fragment: Fragment) {
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        if (fragment is RestaurantFragment) {
            val args = Bundle()
            args.putParcelableArrayList(KEY_DATA, mRestaurantData)
            fragment.arguments = args
        } else if (fragment is SearchFragment) {
            tabLayout.getTabAt(1)!!.select()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
