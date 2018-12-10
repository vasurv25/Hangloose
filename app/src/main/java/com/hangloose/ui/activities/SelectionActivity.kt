package com.hangloose.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.hangloose.R
import com.hangloose.ui.adapter.ViewPagerAdapter
import com.hangloose.ui.fragment.ActivitiesFragment
import com.hangloose.ui.fragment.AdventuresFragment
import kotlinx.android.synthetic.main.activity_selection.indicator
import kotlinx.android.synthetic.main.activity_selection.viewPager

class SelectionActivity : BaseActivity() {
    private var TAG = "SelectionActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
        Log.i(TAG, "onCreate")
    }

    override fun init() {
        Log.i(TAG, "init")
        val listOfFragment = ArrayList<Fragment>()
        listOfFragment.add(ActivitiesFragment.newInstance())
        listOfFragment.add(AdventuresFragment.newInstance())
        var viewPagerAdapter = ViewPagerAdapter(applicationContext, supportFragmentManager, listOfFragment)
        viewPager.adapter = viewPagerAdapter
        indicator.setViewPager(viewPager)
        viewPager.currentItem = 0
        viewPagerAdapter.registerDataSetObserver(indicator.dataSetObserver)
    }
}