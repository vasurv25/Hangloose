package com.hangloose.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.util.Log

class ViewPagerAdapter(
    val context: Context,
    fragmentManager: FragmentManager,
    private val listOfFragment: ArrayList<Fragment>
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