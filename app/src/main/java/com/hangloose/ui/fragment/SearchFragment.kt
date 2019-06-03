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
import com.hangloose.ui.adapter.SearchAdapter
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private var TAG = "SearchFragment"
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mSearchAdapter: SearchAdapter? = null
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
        mSearchAdapter = SearchAdapter(activity!!, mRestaurantData!!)
        mRestaurantSearchList!!.adapter = mSearchAdapter
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
        mSearchAdapter!!.searchFilter(query!!)
        return false
    }
}