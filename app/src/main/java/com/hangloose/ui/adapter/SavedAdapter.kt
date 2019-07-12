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

class SavedAdapter(
    val context: Context,
    val mRestaurantData: ArrayList<RestaurantData>
) :
    RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SavedViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.saved_restaurant_item, parent, false)
        return SavedViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mRestaurantData.size
    }

    override fun onBindViewHolder(holder: SavedViewHolder?, position: Int) {
        SavedViewHolder(holder!!.itemView).bindSearchItems(mRestaurantData[position])
    }

    inner class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindSearchItems(itemDetails: RestaurantData) {
//            Picasso.with(context).load(itemDetails.images).into(itemView.ivRestaurant)
            itemView.tvRestaurantName.text = itemDetails.name
            itemView.tvRestuarntDesc.text = itemDetails.about
            itemView.setOnClickListener {
                val intent = Intent(context, RestaurantDetailsActivity::class.java)
                intent.putExtra(EXTRA_RESTAURANT_DETAILS_DATA, itemDetails)
                context.startActivity(intent)
            }
        }
    }
}