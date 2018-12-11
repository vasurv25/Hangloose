package com.hangloose.ui.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log

class ViewPagerAdapter(
    val context: Context,
    private val fragmentManager: FragmentManager,
    val listOfFragment: ArrayList<Fragment>
) :
    FragmentStatePagerAdapter(fragmentManager) {

    private var TAG = "ViewPagerAdapter"

    override fun getItem(position: Int): Fragment {
        Log.i(TAG, "ViewPagerAdapter")
        return listOfFragment[position]
    }

    override fun getCount(): Int {
        return listOfFragment.size
    }
}