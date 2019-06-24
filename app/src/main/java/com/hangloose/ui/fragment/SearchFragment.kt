package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private var TAG = "SearchFragment"
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    //private var mSearchAdapter: SearchAdapter? = null
    private var mRestaurantSearchList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
    }

    //https://www.journaldev.com/12478/android-searchview-example-tutorial
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, null)
        mRestaurantSearchList = rootView.findViewById(R.id.rvRestaurant)
        setUpAdapter()
        return rootView
    }

    private fun setUpAdapter() {
        //mSearchAdapter = SearchAdapter(activity!!, mRestaurantData!!)
        //mRestaurantSearchList!!.adapter = mSearchAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.search_view, menu)
        val item = menu!!.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionProvider(item) as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        //mSearchAdapter!!.searchFilter(query!!)
        return false
    }
}
/*2019-06-23 00:00:41.323 2360-2360/com.hangloose.debug E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.hangloose.debug, PID: 2360
    kotlin.KotlinNullPointerException
        at com.hangloose.ui.fragment.RestaurantFragment.checkLocationPermission(RestaurantFragment.kt:302)
        at com.hangloose.ui.fragment.RestaurantFragment.onActivityResult(RestaurantFragment.kt:436)
        at com.hangloose.ui.fragment.SecondSearchLocationFragment.sendDataToRestaurantFragment(SecondSearchLocationFragment.kt:320)
        at com.hangloose.ui.fragment.SecondSearchLocationFragment.onClick(SecondSearchLocationFragment.kt:235)
        at android.view.View.performClick(View.java:6669)
        at android.view.View.performClickInternal(View.java:6638)
        at android.view.View.access$3100(View.java:789)
        at android.view.View$PerformClick.run(View.java:26145)
        at android.os.Handler.handleCallback(Handler.java:873)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at android.os.Looper.loop(Looper.java:193)
        at android.app.ActivityThread.main(ActivityThread.java:6863)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:537)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)*/



/*
 Process: com.hangloose.debug, PID: 23459
    kotlin.KotlinNullPointerException
        at com.hangloose.viewmodel.LocationViewModel.restaurantListApiRequest(LocationViewModel.kt:33)
        at com.hangloose.ui.fragment.RestaurantFragment.onLocationChanged(RestaurantFragment.kt:345)
        at com.hangloose.ui.fragment.RestaurantFragment.access$onLocationChanged(RestaurantFragment.kt:45)
        at com.hangloose.ui.fragment.RestaurantFragment$mCallback$1.onLocationResult(RestaurantFragment.kt:69)
        at com.google.android.gms.internal.location.zzau.notifyListener(Unknown Source:4)
        at com.google.android.gms.common.api.internal.ListenerHolder.notifyListenerInternal(Unknown Source:17)
        at com.google.android.gms.common.api.internal.ListenerHolder$zaa.handleMessage(Unknown Source:5)
        at android.os.Handler.dispatchMessage(Handler.java:106)
        at android.os.Looper.loop(Looper.java:193)
        at android.app.ActivityThread.main(ActivityThread.java:6863)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:537)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
 */