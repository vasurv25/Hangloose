package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.adapter.SearchAdapter
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA


class SearchFragment : Fragment() {

    private var TAG = "SearchFragment"
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mRestaurantSearchList: RecyclerView? = null
    private var mSearchView: SearchView? = null
    private var mAdpater: SearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, null)
        mRestaurantSearchList = rootView.findViewById(R.id.rvRestaurant)
        mSearchView = rootView.findViewById(R.id.svRestaurant)
        setUpAdapter()
        setUpSearchView()
        return rootView
    }

    private fun setUpSearchView() {
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null)
                    mAdpater!!.filterSearchData(newText)
                return true
            }
        })

//        mSearchView!!.setOnTouchListener(View.OnTouchListener { p0, p1 ->
//            when (p0.id) {
//                svRestaurant.id -> {
//                    if (p1.action == MotionEvent.ACTION_UP) {
//                        if (p1.rawX >= mSearchView!!.right - mSearchView!!.paddingRight) {
//                            hideSoftKeyboard(activity as Activity)
//                            true
//                        }
//                    }
//                }
//            }
//            false
//        }
//        )
    }

    private fun setUpAdapter() {
        mAdpater = SearchAdapter(context!!, mRestaurantData!!, mRestaurantData!!)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRestaurantSearchList!!.layoutManager = layoutManager
        mRestaurantSearchList!!.adapter = mAdpater
    }
}