package com.hangloose.ui.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log

class ViewPagerAdapter(
    val context: Context,
    private val fragmentManager: FragmentManager,
    val listOfFragment: ArrayList<Fragment>
) :
    FragmentPagerAdapter(fragmentManager) {

    private var TAG = "ViewPagerAdapter"

    override fun getItem(position: Int): Fragment {
        Log.i(TAG, "ViewPagerAdapter")
        return listOfFragment[position]
    }

    override fun getCount(): Int {
        return listOfFragment.size
    }
}