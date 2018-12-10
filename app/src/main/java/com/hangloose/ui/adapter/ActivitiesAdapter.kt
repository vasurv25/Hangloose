package com.hangloose.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import kotlinx.android.synthetic.main.activities_recylcer_item.view.ivActivities
import kotlinx.android.synthetic.main.activities_recylcer_item.view.ivSelector

class ActivitiesAdapter(val context: Context, val contentList: Array<Int>) :
    RecyclerView.Adapter<ActivitiesAdapter.ActivitiesViewHolder>() {

    private var TAG = "ActivitiesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ActivitiesViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.activities_recylcer_item, parent, false)
        Log.i(TAG, "onCreateViewHolder")
        return ActivitiesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder?, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        holder!!.backgroundImage.setImageResource(contentList.get(position))
    }

    inner class ActivitiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val backgroundImage = itemView.ivActivities
        val selector = itemView.ivSelector
    }
}