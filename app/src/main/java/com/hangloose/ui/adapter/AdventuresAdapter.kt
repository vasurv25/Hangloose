package com.hangloose.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hangloose.R
import com.hangloose.ui.model.AdventuresDetails
import kotlinx.android.synthetic.main.activities_recylcer_item.view.cbSelector
import kotlinx.android.synthetic.main.adventures_recycler_item.view.ivAdventure

class AdventuresAdapter(val context: Context, private var contentList: ArrayList<AdventuresDetails>) :
    RecyclerView.Adapter<AdventuresAdapter.AdventuresViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdventuresViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.adventures_recycler_item, parent, false)
        Log.i(TAG, "onCreateViewHolder")
        return AdventuresViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdventuresViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        holder!!.bindAdventuresView(contentList[position])
    }

    private var TAG = "AdventuresAdapter"
    private var mAdventuresList: ArrayList<String>? = ArrayList()

    override fun getItemCount(): Int {
        return contentList.size
    }


    inner class AdventuresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindAdventuresView(contentItem: AdventuresDetails) {
            Log.i(TAG, """Image : ${contentItem.image}""")
            Glide.with(context).load(contentItem.image).into(itemView.ivAdventure)
            itemView.ivAdventure.setOnClickListener {
                itemView.cbSelector.isChecked = !contentItem.checked
            }

            itemView.cbSelector.setOnCheckedChangeListener { _, isChecked ->
                contentItem.checked = isChecked
                Log.i(TAG, "Checked : $isChecked")
                if (isChecked) {
                    mAdventuresList!!.add(contentItem.id)
                } else {
                    mAdventuresList!!.remove(contentItem.id)
                }
            }

            itemView.cbSelector.isChecked = contentItem.checked
            Log.i(TAG, "Checked : ${contentItem.checked}")
        }
    }

    fun getAdventuresList(): ArrayList<String> {
        return mAdventuresList!!
    }
}