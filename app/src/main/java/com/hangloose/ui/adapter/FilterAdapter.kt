package com.hangloose.ui.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import kotlinx.android.synthetic.main.layout_filter_item.view.*

class FilterAdapter(val context: Context, private val listFilter: List<String>) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_filter_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bindFilterItems(listFilter[position])
    }

    private var mTagsList: ArrayList<String> = ArrayList()

    override fun getItemCount(): Int {
        return listFilter.size
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindFilterItems(item: String) {
            itemView.cb_filter.text = item
            itemView.cb_filter.setOnCheckedChangeListener { button, b ->
                if (button.isChecked) {
                    if (!mTagsList.contains(item)) {
                        mTagsList.add(item)
                    }
                    button.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    mTagsList.remove(item)
                    button.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }
        }
    }

    fun getTagsList(): ArrayList<String> {
        return mTagsList
    }

//    fun clearFilter() {
//        itemView.cb_filter.isChecked = false
//    }
}