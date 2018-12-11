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
import com.hangloose.ui.adapter.ActivitiesAdapter
import com.hangloose.ui.model.ActivitiesState

class ActivitiesFragment : Fragment() {

    private var TAG = "ActivitiesFragment"
    private var mRecyclerView: RecyclerView? = null
    private lateinit var mContext: Context
    private var mContentList: ArrayList<ActivitiesState> = ArrayList()
    private var mList = arrayOf(
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out,
        R.drawable.boys_day_out
    )

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_activities, null)
        mRecyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        mRecyclerView!!.isNestedScrollingEnabled = false
        mRecyclerView!!.setHasFixedSize(false)
        initRecyclerView()
        return rootView
    }

    companion object {
        fun newInstance(): Fragment {
            Log.i("Tag", "init")
            return ActivitiesFragment()
        }
    }

    private fun initRecyclerView() {
        Log.i(TAG, "initRecyclerView")
        val layoutManager = GridLayoutManager(mContext, 2)
        mRecyclerView!!.layoutManager = layoutManager
        var adapter = ActivitiesAdapter(mContext, getData())
        mRecyclerView!!.adapter = adapter
    }

    private fun getData(): ArrayList<ActivitiesState> {
        for (i in 0 until mList.size) {
            mContentList.add(ActivitiesState(R.drawable.boys_day_out))
        }
        return mContentList
    }
}