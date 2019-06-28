package com.hangloose.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import kotlinx.android.synthetic.main.layout_filter_item.view.*
import kotlinx.android.synthetic.main.layout_profile_item.view.*

class ProfileAdapter(
    val context: Context,
    private val listProfile: List<String>,
    private val listProfileIcons: List<Int>
) :
    RecyclerView.Adapter<ProfileAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.layout_profile_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listProfile.size
    }

    override fun onBindViewHolder(holder: FilterViewHolder?, position: Int) {
        holder!!.bindFilterItems(listProfile[position], listProfileIcons[position])
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindFilterItems(item: String, itemIcon: Int) {
            itemView.tvProfile.text = item
//            itemView.tvProfile.setCompoundDrawables(
//                ContextCompat.getDrawable(context, itemIcon),
//                null,
//                ContextCompat.getDrawable(context, R.drawable.ic_right_arrow), null
//            )
            itemView.tvProfile.setCompoundDrawablesWithIntrinsicBounds(itemIcon, 0, R.drawable.ic_right_arrow, 0)
        }
    }
}