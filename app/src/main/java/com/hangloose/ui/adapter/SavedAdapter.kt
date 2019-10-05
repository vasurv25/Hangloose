package com.hangloose.ui.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.hangloose.HanglooseApp
import com.hangloose.R
import com.hangloose.ui.activities.RestaurantDetailsActivity
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.EXTRA_RESTAURANT_DETAILS_DATA
import kotlinx.android.synthetic.main.saved_restaurant_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class SavedAdapter(
    val context: Context,
    private val mRestaurantData: ArrayList<RestaurantData>,
    val type: String
) :
    RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.saved_restaurant_item, parent, false)
        return SavedViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        SavedViewHolder(holder!!.itemView).bindSearchItems(mRestaurantData[position])
    }

    override fun getItemCount(): Int {
        return mRestaurantData.size
    }

    inner class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindSearchItems(itemDetails: RestaurantData) {
            if (type == context.resources.getString(R.string.key_saved)) {
                itemView.ibDelete.visibility = View.VISIBLE
            } else {
                itemView.ibDelete.visibility = View.GONE
            }
            itemDetails.logo?.let {
                itemView.ivRestaurant.visibility = View.VISIBLE
                Glide.with(context).load(itemDetails.logo).into(itemView.ivRestaurant) }
            itemView.tvRestaurantName.text = itemDetails.name
            itemView.tvRestuarntDesc.text = itemDetails.about
            itemView.setOnClickListener {
                val intent = Intent(context, RestaurantDetailsActivity::class.java)
                intent.putExtra(EXTRA_RESTAURANT_DETAILS_DATA, itemDetails)
                context.startActivity(intent)
            }
            itemView.ibDelete.setOnClickListener { HanglooseApp.getDataHandler()!!.deleteUnsavedRestaurant(itemDetails) }
        }
    }
}