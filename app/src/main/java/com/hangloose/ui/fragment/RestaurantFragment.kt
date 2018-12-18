package com.hangloose.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.hangloose.R
import com.hangloose.ui.activities.SwipeableCardView
import com.hangloose.ui.activities.TabsActivity
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
    private var mBtSearch: Button? = null
    private var mBtRadioRealGroup: RadioRealButtonGroup? = null
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
        Log.i(TAG, "Restaurant data : $mRestaurantData")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_restaurant, null)
        mSwipePlaceHolderView = rootView!!.findViewById(R.id.swipeView) as SwipePlaceHolderView
        mBtSearch = rootView.findViewById(R.id.btSearch) as Button
        mBtRadioRealGroup = rootView.findViewById(R.id.segmentedButtonGroup) as RadioRealButtonGroup
        setSwipeableView()
        return rootView
    }

    private fun setSwipeableView() {
        val sideMargin = dpToPx(220)
        val bottomMargin = dpToPx(180)
        val windowSize = getDisplaySize(activity!!.windowManager)
        Log.i(TAG, "Window Size : $windowSize")
        Log.i(TAG, "Bottom Margin : $bottomMargin")
        mSwipePlaceHolderView!!.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(3)
            .setHeightSwipeDistFactor(10f)
            .setWidthSwipeDistFactor(5f)
            .setSwipeDecor(
                SwipeDecor()
                    .setViewWidth(windowSize.x - bottomMargin / 4)
                    .setViewHeight(windowSize.y - bottomMargin)
                    .setViewGravity(Gravity.CENTER)
                    .setRelativeScale(0.01f)
                    .setSwipeMaxChangeAngle(2f)
            )
        mSwipePlaceHolderView!!.removeAllViews()
        for (data in mRestaurantData!!) {
            if (data.restaurantType.equals("VEGETERIAN")) {
                mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
            }
        }

        mBtSearch!!.setOnClickListener { (activity as TabsActivity).replaceFragment(SearchFragment()) }

        mBtRadioRealGroup!!.setOnPositionChangedListener { button, currentPosition, lastPosition ->
            when (currentPosition) {
                0 -> {
                    var one = mRestaurantData!!.map { it.restaurantType }.filter { it.equals("VEGETARIAN") }
                    Log.i(TAG, "VEG : $one")
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("VEGETERIAN")) {
                            mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                        }
                    }
                }
                1 -> {
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("NON_VEGETERIAN")) {
                            mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                        }
                    }
                }
                else -> {
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("BAR")) {
                            mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                        }
                    }
                }
            }
        }
    }
}

/*mBtSegmentedGroup!!.setOnClickedButtonListener {
    when (it) {
        0 -> {
            var one = mRestaurantData!!.map { it.restaurantType }.filter { it.equals("VEGETARIAN") }
            Log.i(TAG, "VEG : $one")
            mSwipePlaceHolderView!!.removeAllViews()
            for (data in mRestaurantData!!) {
                if (data.restaurantType.equals("VEGETERIAN")) {
                    mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                }
            }
        }
        1 -> {
            mSwipePlaceHolderView!!.removeAllViews()
            for (data in mRestaurantData!!) {
                if (data.restaurantType.equals("NON_VEGETERIAN")) {
                    mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                }
            }
        }
        else -> {
            mSwipePlaceHolderView!!.removeAllViews()
            for (data in mRestaurantData!!) {
                if (data.restaurantType.equals("BAR")) {
                    mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                }
            }
        }
    }
}*/