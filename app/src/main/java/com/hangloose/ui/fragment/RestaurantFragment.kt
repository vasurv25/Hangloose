package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder
import kotlinx.android.synthetic.main.fragment_restaurant.swipeView


class RestaurantFragment : Fragment() {

    private var mSwipePlaceHolderView: SwipePlaceHolderView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_restaurant, null)
        mSwipePlaceHolderView = rootView.findViewById(R.id.swipeView) as SwipePlaceHolderView
        setSwipeableView()
        return rootView
    }

    private fun setSwipeableView() {
        mSwipePlaceHolderView!!.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(3)
            .setSwipeDecor(
                SwipeDecor()
                    .setPaddingTop(20)
                    .setRelativeScale(0.01f)
            )

        /*for (profile in Utils.loadProfiles(this.applicationContext)) {
            mSwipePlaceHolderView!!.addView(TinderCard(mContext, profile, mSwipeView))
        }*/
    }
}