package com.hangloose.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.hangloose.utils.KEY_ENTIRE_RESTAURANT_DATA
import com.hangloose.utils.hideSoftKeyboard


class SearchFragment : Fragment(), View.OnTouchListener {

    private var TAG = "SearchFragment"
    private var mEntireRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mRestaurantSearchList: RecyclerView? = null
    private var mSearchRestaurant: EditText? = null
    private var mAdpater: SearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEntireRestaurantData = arguments!!.getParcelableArrayList(KEY_ENTIRE_RESTAURANT_DATA)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        mRestaurantSearchList = rootView.findViewById(R.id.rvRestaurant)
        mSearchRestaurant = rootView.findViewById(R.id.etRestaurantSearch)
        retainInstance = true
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
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun setUpSearchView() {
        mSearchRestaurant!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                if (mRestaurantSearchList!!.visibility != View.INVISIBLE) {
//                    mRestaurantSearchList!!.visibility = View.INVISIBLE
//                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //if (mRestaurantSearchList!!.visibility != View.VISIBLE && s.toString() != "") {
                //    mRestaurantSearchList!!.visibility = View.VISIBLE
                    mAdpater!!.filterSearchData(s.toString())
                //}
            }
        })
    }

    private fun setUpAdapter() {
        mAdpater = SearchAdapter(context!!, mEntireRestaurantData!!, mEntireRestaurantData!!)
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
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