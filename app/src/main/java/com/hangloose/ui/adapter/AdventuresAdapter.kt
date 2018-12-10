package com.hangloose.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import kotlinx.android.synthetic.main.activities_recylcer_item.view.ivSelector
import kotlinx.android.synthetic.main.adventures_recycler_item.view.ivAdventure

class AdventuresAdapter(val context: Context, val contentList: Array<Int>) :
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
        holder!!.backgroundImage.setImageResource(contentList.get(position))
    }

    inner class AdventuresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val backgroundImage = itemView.ivAdventure
        val selector = itemView.ivSelector
    }
}