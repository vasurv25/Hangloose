package com.hangloose.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import kotlinx.android.synthetic.main.layout_filter_item.view.*

class FilterAdapter(val context: Context, val listFilter: List<String>) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.layout_filter_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listFilter.size
    }

    override fun onBindViewHolder(holder: FilterViewHolder?, position: Int) {
        holder!!.bindFilterItems(listFilter[position])
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindFilterItems(item: String) {
            itemView.cb_filter.text = item
            itemView.cb_filter.setOnCheckedChangeListener { button, b ->
                if (button.isChecked) {
                    button.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    button.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }
        }
    }
}