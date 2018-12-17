package com.hangloose.ui.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Toast
import com.hangloose.R
import com.hangloose.ui.fragment.RestaurantFragment
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA
import com.hangloose.utils.KEY_RESTAURANT_DATA
import kotlinx.android.synthetic.main.activity_tab.tabLayout

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

    override fun init() {
        tabLayout.addTab(tabLayout.newTab().setText("1"))
        tabLayout.addTab(tabLayout.newTab().setText("2"))
        tabLayout.addTab(tabLayout.newTab().setText("3"))

        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tabLayout.selectedTabPosition == 0) {
                        replaceFragment(RestaurantFragment())
                        Toast.makeText(this@TabsActivity, "Tab " + tabLayout.selectedTabPosition, Toast.LENGTH_LONG)
                            .show()
                    } else if (tabLayout.selectedTabPosition == 1) {
                        Toast.makeText(this@TabsActivity, "Tab " + tabLayout.selectedTabPosition, Toast.LENGTH_LONG)
                            .show()
                    } else if (tabLayout.selectedTabPosition == 2) {
                        Toast.makeText(this@TabsActivity, "Tab " + tabLayout.selectedTabPosition, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }

    private fun replaceFragment(fragment: Fragment) {
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        val args = Bundle()
        args.putParcelableArrayList(KEY_DATA, mRestaurantData)
        fragment.arguments = args
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
