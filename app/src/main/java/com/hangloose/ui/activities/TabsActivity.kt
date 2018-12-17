package com.hangloose.ui.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.widget.Toast
import com.hangloose.R
import com.hangloose.ui.fragment.ActivitiesFragment
import com.hangloose.ui.fragment.RestaurantFragment
import kotlinx.android.synthetic.main.activity_tab.tabLayout

class TabsActivity : BaseActivity() {

    private val TAG = "TabsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
    }

    override fun init() {
        tabLayout.addTab(tabLayout.newTab().setText("1"))
        tabLayout.addTab(tabLayout.newTab().setText("2"))
        tabLayout.addTab(tabLayout.newTab().setText("3"))

        replaceFragment(RestaurantFragment())

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
                            .show();
                    } else if (tabLayout.selectedTabPosition == 1) {
                        Toast.makeText(this@TabsActivity, "Tab " + tabLayout.selectedTabPosition, Toast.LENGTH_LONG)
                            .show();
                    } else if (tabLayout.selectedTabPosition == 2) {
                        Toast.makeText(this@TabsActivity, "Tab " + tabLayout.selectedTabPosition, Toast.LENGTH_LONG)
                            .show();
                    }
                }

            })
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}
