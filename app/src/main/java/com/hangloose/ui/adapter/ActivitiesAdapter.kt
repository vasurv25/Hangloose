package com.hangloose.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.model.ActivitiesState
import kotlinx.android.synthetic.main.activities_recylcer_item.view.cbSelector
import kotlinx.android.synthetic.main.activities_recylcer_item.view.ivActivities

class ActivitiesAdapter(val context: Context, val contentList: ArrayList<ActivitiesState>) :
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
        holder!!.bindActivitiesView(contentList[position])
    }

    inner class ActivitiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindActivitiesView(contentItem: ActivitiesState) {
            itemView.ivActivities.setImageResource(contentItem.image!!)

            itemView.ivActivities.setOnClickListener {
                itemView.cbSelector.isChecked = !contentItem.checked
            }

            itemView.cbSelector.setOnCheckedChangeListener { _, isChecked ->
                contentItem.checked = isChecked
                Log.i(TAG, "Checked : $isChecked")
            }

            itemView.cbSelector.isChecked = contentItem.checked
            Log.i(TAG, "Checked : ${contentItem.checked}")
        }
    }
}