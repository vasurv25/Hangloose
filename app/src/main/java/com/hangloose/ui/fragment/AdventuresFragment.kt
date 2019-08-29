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
import com.hangloose.ui.adapter.AdventuresAdapter
import com.hangloose.ui.model.AdventuresDetails
import com.hangloose.utils.KEY_ADVENTURES_LIST

class AdventuresFragment : Fragment() {

    private var TAG = "AdventuresFragment"
    private var mRecyclerView: RecyclerView? = null
    private lateinit var mContext: Context
    private var mContentList: ArrayList<AdventuresDetails> = ArrayList()
    private var mAdapter: AdventuresAdapter? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mContentList = arguments!!.getParcelableArrayList(KEY_ADVENTURES_LIST)
            Log.i(TAG, "AdventuresList : $mContentList")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_adventures, container, false)
        mRecyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        mRecyclerView!!.isNestedScrollingEnabled = false
        mRecyclerView!!.setHasFixedSize(false)
        initRecyclerView()
        return rootView
    }

    companion object {
        fun newInstance(adventuresList: ArrayList<AdventuresDetails>): AdventuresFragment {
            Log.i("Tag", "init")
            val fragment = AdventuresFragment()
            val args = Bundle()
            args.putParcelableArrayList(KEY_ADVENTURES_LIST, adventuresList)
            fragment.arguments = args
            return fragment
        }
    }

    private fun initRecyclerView() {
        Log.i(TAG, "initRecyclerView")
        val layoutManager = GridLayoutManager(mContext, 2)
        mRecyclerView!!.layoutManager = layoutManager
        mAdapter = AdventuresAdapter(mContext, mContentList)
        mRecyclerView!!.adapter = mAdapter

        (activity as SelectionActivity).let {
            it.didClickNextButton = {
                //mCallBack!!.transferData(mAdapter.getAdventuresList(), 2)
            }
        }
    }

    fun getSelectedAdventures(): ArrayList<String> {
        Log.i(TAG, "getSelectedAdventures : " + mAdapter!!.getAdventuresList())
        return mAdapter!!.getAdventuresList()
    }
}