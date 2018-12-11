package com.hangloose.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.adapter.AdventuresAdapter
import com.hangloose.ui.model.ActivitiesState

class AdventuresFragment : Fragment() {

    private var TAG = "AdventuresFragment"
    private var mRecyclerView: RecyclerView? = null
    private lateinit var mContext: Context
    private var mContentList: ArrayList<ActivitiesState> = ArrayList()

    private val mList = arrayOf(
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal,
        R.drawable.morning_meal
    )

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_adventures, null)
        mRecyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        initRecyclerView()
        return rootView
    }

    companion object {
        fun newInstance(): Fragment {
            Log.i("Tag", "init")
            return AdventuresFragment()
        }
    }

    private fun initRecyclerView() {
        Log.i(TAG, "initRecyclerView")
        val layoutManager = GridLayoutManager(mContext, 2)
        mRecyclerView!!.layoutManager = layoutManager
        var adapter = AdventuresAdapter(mContext, getData())
        mRecyclerView!!.adapter = adapter
    }

    private fun getData(): ArrayList<ActivitiesState> {
        for (i in 0 until mList.size) {
            mContentList.add(ActivitiesState(R.drawable.morning_meal))
        }
        return mContentList
    }
}