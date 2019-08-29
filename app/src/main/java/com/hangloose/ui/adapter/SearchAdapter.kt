package com.hangloose.ui.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.activities.RestaurantDetailsActivity
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.EXTRA_RESTAURANT_DETAILS_DATA
import kotlinx.android.synthetic.main.saved_restaurant_item.view.*

class SearchAdapter(
    val context: Context,
    private val mRestaurantData: ArrayList<RestaurantData>,
    var mFilteredList: ArrayList<RestaurantData>
) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.search_restaurant_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder?, position: Int) {
        SearchViewHolder(holder!!.itemView).bindSearchItems(mFilteredList[position])
    }

    fun filterSearchData(query: String) {

        repeat(mRestaurantData.size) {
            //            mRestaurantData.any { item -> item.name!!.contains(query, false) }
            mFilteredList = mRestaurantData.filter { it.name!!.contains(query, true) } as ArrayList<RestaurantData>
        }
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindSearchItems(itemDetails: RestaurantData) {
//            Picasso.with(context).load(itemDetails.images).into(itemView.ivRestaurant)
            itemView.tvRestaurantName.text = itemDetails.name
            itemView.setOnClickListener {
                val intent = Intent(context, RestaurantDetailsActivity::class.java)
                intent.putExtra(EXTRA_RESTAURANT_DETAILS_DATA, itemDetails)
                context.startActivity(intent)
            }
        }
    }
}