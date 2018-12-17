package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.activities.SwipeableCardView
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA
import com.hangloose.utils.dpToPx
import com.hangloose.utils.getDisplaySize
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder

class RestaurantFragment : Fragment() {
    private var TAG = "RestaurantFragment"
    private var mSwipePlaceHolderView: SwipePlaceHolderView? = null
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
        Log.i(TAG, "Restaurant data : $mRestaurantData")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_restaurant, null)
        mSwipePlaceHolderView = rootView.findViewById(R.id.swipeView) as SwipePlaceHolderView
        setSwipeableView()
        return rootView
    }

    private fun setSwipeableView() {
        val bottomMargin = dpToPx(160)
        val windowSize = getDisplaySize(activity!!.windowManager)
        Log.i(TAG, "Window Size : $windowSize")
        Log.i(TAG, "Bottom Margin : $bottomMargin")
        mSwipePlaceHolderView!!.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(3)
            .setHeightSwipeDistFactor(10f)
            .setWidthSwipeDistFactor(5f)
            .setSwipeDecor(
                SwipeDecor()
                    .setViewWidth(windowSize.x)
                    .setViewHeight(windowSize.y - bottomMargin)
                    .setViewGravity(Gravity.CENTER)
                    .setRelativeScale(0.01f)
                    .setSwipeMaxChangeAngle(2f)
            )

        for (data in mRestaurantData!!) {
            mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
        }
    }
}