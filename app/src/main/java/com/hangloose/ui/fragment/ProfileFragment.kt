package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.adapter.ProfileAdapter
import kotlinx.android.synthetic.main.content_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import android.support.design.widget.CoordinatorLayout


class ProfileFragment : Fragment() {

    private val listProfile =
        arrayListOf(
            "Favourites",
            "Liked Restaurant",
            "Offers",
            "Account Settings",
            "Help & Support",
            "Logout"
        )
    var isShow = true
    var scrollRange = -1

    private val listProfileIcons = arrayListOf(
        R.drawable.saved_icon,
        R.drawable.order_icon,
        R.drawable.offers_icon,
        R.drawable.settings_icon,
        R.drawable.help_icon,
        R.drawable.logout_icon
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, null)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAdapter()
        app_bar.addOnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (scrollRange + verticalOffset == 0) {
                toolbar_layout.title = resources.getString(R.string.profile)
                cardViewProfile.visibility = ViewGroup.GONE
                isShow = true
            } else if (isShow) {
                toolbar_layout.title = " "
                cardViewProfile.visibility = ViewGroup.VISIBLE
                isShow = false
            }
        }
    }

    private fun setAdapter() {
        val lLM = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_profile.layoutManager = lLM
        rv_profile.adapter = ProfileAdapter(context!!, listProfile, listProfileIcons)
    }
}