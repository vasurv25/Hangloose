package com.hangloose.ui.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.activities.SavedRestaurantActivity
import com.hangloose.ui.activities.SignInActivity
import com.hangloose.utils.PreferenceHelper
import kotlinx.android.synthetic.main.layout_profile_item.view.*
import com.hangloose.HanglooseApp


class ProfileAdapter(
    val context: Context,
    private val listProfile: List<String>,
    private val listProfileIcons: List<Int>
) :
    RecyclerView.Adapter<ProfileAdapter.FilterViewHolder>() {

    private var mPreference: SharedPreferences? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.layout_profile_item, parent, false)
        mPreference = PreferenceHelper.defaultPrefs(context)
        return FilterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listProfile.size
    }

    override fun onBindViewHolder(holder: FilterViewHolder?, position: Int) {
        holder!!.bindFilterItems(listProfile[position], listProfileIcons[position], position)
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindFilterItems(item: String, itemIcon: Int, position: Int) {
            itemView.tvProfile.text = item
//            itemView.tvProfile.setCompoundDrawables(
//                ContextCompat.getDrawable(context, itemIcon),
//                null,
//                ContextCompat.getDrawable(context, R.drawable.ic_right_arrow), null
//            )
            itemView.tvProfile.setCompoundDrawablesWithIntrinsicBounds(itemIcon, 0, R.drawable.ic_right_arrow, 0)

            itemView.setOnClickListener {
                when (it.tvProfile.text) {
                    context.resources.getString(R.string.saved_restaurant) -> {
                        val intent = Intent(context, SavedRestaurantActivity::class.java)
                        context.startActivity(intent)
                    }
                    context.resources.getString(R.string.logout) -> {
                        mPreference!!.edit().clear().commit()
                        HanglooseApp.getInstance().clearApplicationData()
                        var intent = Intent(context, SignInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}