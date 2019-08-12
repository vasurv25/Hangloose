package com.hangloose.ui.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hangloose.R
import kotlinx.android.synthetic.main.ambience_image_item.view.*

class AmbienceImageAdapter(val context: Context, val imageList: ArrayList<String>) :
    PagerAdapter() {

    private var TAG = "AmbienceImageAdapter"
    private var layoutInflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView = layoutInflater!!.inflate(R.layout.ambience_image_item, parent, false)
        Glide.with(context).load(imageList[position]).into(itemView.ivAmbience)
        parent.addView(itemView.ivAmbience)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        container.removeView(`object` as View?)
    }
}