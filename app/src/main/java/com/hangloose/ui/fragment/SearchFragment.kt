package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, null)
        return rootView
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
        var filterList = filter(query!!)
        return true
    }

    private fun filter(query: String): ArrayList<RestaurantData> {
        val lowerCaseQuery = query.toLowerCase()

        val filteredModelList: ArrayList<RestaurantData> = ArrayList()
        for (model in mRestaurantData!!) {
            val text = model.name
            if (text!!.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }
}