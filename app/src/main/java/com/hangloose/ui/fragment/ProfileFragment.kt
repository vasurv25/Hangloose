package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.adapter.ProfileAdapter
import kotlinx.android.synthetic.main.content_profile.*

class ProfileFragment : Fragment() {

    private val listProfile =
        arrayListOf(
            "Saved Restaurant",
            "Liked Restaurant",
            "Offers",
            "Account Settings",
            "Help & Support",
            "Logout"
        )


    private val listProfileIcons = arrayListOf(
        R.drawable.profile,
        R.drawable.profile,
        R.drawable.profile,
        R.drawable.profile,
        R.drawable.profile,
        R.drawable.profile
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, null)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAdapter()
    }

    private fun setAdapter() {
        val lLM = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_profile.layoutManager = lLM
        rv_profile.adapter = ProfileAdapter(context!!, listProfile, listProfileIcons)
    }
}