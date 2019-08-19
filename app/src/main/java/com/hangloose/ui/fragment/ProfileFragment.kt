package com.hangloose.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hangloose.R
import com.hangloose.ui.adapter.ProfileAdapter
import kotlinx.android.synthetic.main.content_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.hangloose.utils.KEY_EMAIL_ID
import com.hangloose.utils.KEY_USER_NAME
import com.hangloose.utils.PreferenceHelper
import com.hangloose.utils.PreferenceHelper.get

class ProfileFragment : Fragment() {

    private val listProfile =
        arrayListOf(
            "Favourites",
            "Liked Restaurants",
            //"Offers",
            "Account Settings",
            "Queries & Suggestions",
            "Help & Support",
            "Logout"
        )
    var isShow = true
    var scrollRange = -1

    private val listProfileIcons = arrayListOf(
        R.drawable.saved_icon,
        R.drawable.order_icon,
        //R.drawable.offers_icon,
        R.drawable.settings_icon,
        R.drawable.offers_icon,
        R.drawable.help_icon,
        R.drawable.logout_icon
    )
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mPreference: SharedPreferences? = null
    private var name: String? = null
    private var email: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, null)
        val textName = rootView.findViewById<TextView>(R.id.tvName)
        val textEmail = rootView.findViewById<TextView>(R.id.tvEmailId)
        textName.text = name
        textEmail.text = email

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        mGoogleApiClient!!.connect()
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreference = PreferenceHelper.defaultPrefs(activity!!)
        name = mPreference!![KEY_USER_NAME]
        email = mPreference!![KEY_EMAIL_ID]
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
        rv_profile.adapter = ProfileAdapter(context!!, listProfile, listProfileIcons, mGoogleApiClient)
    }
}