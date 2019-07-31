package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.hangloose.R
import com.hangloose.ui.adapter.SearchAdapter
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA
import com.hangloose.utils.hideSoftKeyboard


class SearchFragment : Fragment(), View.OnTouchListener {

    private var TAG = "SearchFragment"
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mRestaurantSearchList: RecyclerView? = null
    private var mSearchRestaurant: EditText? = null
    private var mAdpater: SearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, null)
        mRestaurantSearchList = rootView.findViewById(R.id.rvRestaurant)
        mSearchRestaurant = rootView.findViewById(R.id.etRestaurantSearch)
        mSearchRestaurant!!.setOnTouchListener(this)
        setUpAdapter()
        setUpSearchView()
        return rootView
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v!!.id) {
            mSearchRestaurant!!.id -> {
                if (event!!.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= mSearchRestaurant!!.right - mSearchRestaurant!!.totalPaddingRight) {
                        mSearchRestaurant!!.setText("")
                        hideSoftKeyboard(activity!!)
                        return true;
                    }
                }
            }
        }
        return false
    }

    private fun setUpSearchView() {
        mSearchRestaurant!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mAdpater!!.filterSearchData(s.toString())
            }
        })
    }

    private fun setUpAdapter() {
        mAdpater = SearchAdapter(context!!, mRestaurantData!!, mRestaurantData!!)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRestaurantSearchList!!.layoutManager = layoutManager
        mRestaurantSearchList!!.addItemDecoration(
            DividerItemDecoration(
                mRestaurantSearchList!!.context,
                layoutManager.orientation
            )
        )
        mRestaurantSearchList!!.adapter = mAdpater
    }
}