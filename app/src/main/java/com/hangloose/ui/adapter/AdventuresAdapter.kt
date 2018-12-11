package com.hangloose.ui.adapter

import android.content.ClipData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.model.ActivitiesState
import kotlinx.android.synthetic.main.activities_recylcer_item.view.cbSelector
import kotlinx.android.synthetic.main.adventures_recycler_item.view.ivAdventure
import java.util.ArrayList

class AdventuresAdapter(val context: Context, val contentList: ArrayList<ActivitiesState>) :
    RecyclerView.Adapter<AdventuresAdapter.AdventuresViewHolder>() {

    private var TAG = "AdventuresAdapter"

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AdventuresViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.adventures_recycler_item, parent, false)
        Log.i(TAG, "onCreateViewHolder")
        return AdventuresViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun onBindViewHolder(holder: AdventuresViewHolder?, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        holder!!.bindAdventuresView(contentList[position])
    }

    inner class AdventuresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindAdventuresView(contentItem: ActivitiesState) {
            itemView.ivAdventure.setImageResource(contentItem.image!!)

            itemView.cbSelector.setOnCheckedChangeListener { _, isChecked ->
                contentItem.checked = isChecked
                Log.i(TAG, "Checked : $isChecked")
            }

            itemView.cbSelector.isChecked = contentItem.checked
            Log.i(TAG, "Checked : ${contentItem.checked}")
        }
    }

    fun removeItem(position: Int) {
        contentList.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun restoreItem(item: ClipData.Item, position: Int) {
        //contentList.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }
}