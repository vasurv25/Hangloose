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
import com.hangloose.ui.activities.SelectionActivity
import com.hangloose.ui.adapter.ActivitiesAdapter
import com.hangloose.ui.model.ActivitiesDetails
import com.hangloose.utils.KEY_ACTIVITIES_LIST

class ActivitiesFragment : Fragment() {

    private var TAG = "ActivitiesFragment"
    private var mRecyclerView: RecyclerView? = null
    private lateinit var mContext: Context
    private var mContentList: ArrayList<ActivitiesDetails> = ArrayList()
    var mAdapter : ActivitiesAdapter? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mContentList = arguments!!.getParcelableArrayList(KEY_ACTIVITIES_LIST)
            Log.i(TAG, "ActivitiesList : $mContentList")
        }
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
        fun newInstance(activitiesList: ArrayList<ActivitiesDetails>): ActivitiesFragment {
            Log.i("Activities Instance", "init : $activitiesList")
            val fragment = ActivitiesFragment()
            val args = Bundle()
            args.putParcelableArrayList(KEY_ACTIVITIES_LIST, activitiesList)
            fragment.arguments = args
            return fragment
        }
    }

    private fun initRecyclerView() {
        Log.i(TAG, "initRecyclerView")
        val layoutManager = GridLayoutManager(mContext, 2)
        mRecyclerView!!.layoutManager = layoutManager
        mAdapter = ActivitiesAdapter(mContext, mContentList)
        mRecyclerView!!.adapter = mAdapter

        (activity as SelectionActivity).let {
            it.didClickNextButton = {
                //mCallBack!!.transferData(mAdapter!!.getActivitiesList(), 1)
            }
        }
    }

    fun getSelectedActivities() : ArrayList<String> {
        return mAdapter!!.getActivitiesList()
    }

//    private fun getData(): ArrayList<ActivitiesState> {
//        for (i in 0 until mList.size) {
//            mContentList.add(ActivitiesState(R.drawable.boys_day_out))
//        }
//        return mContentList
//    }
}