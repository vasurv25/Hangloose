package com.hangloose.ui.activities

import android.os.Bundle
import com.hangloose.R
import com.hangloose.ui.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_selection.indicator
import kotlinx.android.synthetic.main.activity_selection.viewPager

class SelectionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
    }

    override fun init() {
        var viewPagerAdapter = ViewPagerAdapter(applicationContext, supportFragmentManager)
        viewPager.adapter = viewPagerAdapter
        indicator.setViewPager(viewPager)
        viewPager.currentItem = 0
        viewPagerAdapter.registerDataSetObserver(indicator.dataSetObserver)
    }
}