package com.hangloose.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import com.hangloose.R
import com.hangloose.ui.adapter.ViewPagerAdapter
import com.hangloose.ui.fragment.ActivitiesFragment
import com.hangloose.ui.fragment.AdventuresFragment
import kotlinx.android.synthetic.main.activity_selection.btRefresh
import kotlinx.android.synthetic.main.activity_selection.indicator
import kotlinx.android.synthetic.main.activity_selection.tvSelectionHeading
import kotlinx.android.synthetic.main.activity_selection.viewPager

class SelectionActivity : BaseActivity() {
    private var TAG = "SelectionActivity"
    var didClickStartButton: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
        Log.i(TAG, "onCreate")
    }

    override fun init() {
        Log.i(TAG, "init")
        tvSelectionHeading.text = getString(R.string.select_your_activities)
        val listOfFragment = ArrayList<Fragment>()
        listOfFragment.add(ActivitiesFragment.newInstance())
        listOfFragment.add(AdventuresFragment.newInstance())
        var viewPagerAdapter = ViewPagerAdapter(applicationContext, supportFragmentManager, listOfFragment)
        viewPager.adapter = viewPagerAdapter
        indicator.setViewPager(viewPager)
        viewPager.currentItem = 0
        viewPagerAdapter.registerDataSetObserver(indicator.dataSetObserver)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (viewPager.currentItem == 0) {
                    tvSelectionHeading.text = getString(R.string.select_your_activities)
                    btRefresh.visibility = View.GONE
                } else {
                    tvSelectionHeading.text = getString(R.string.select_your_adventure)
                    btRefresh.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })

        btRefresh.setOnClickListener { didClickStartButton?.invoke() }
    }
}