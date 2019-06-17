package com.hangloose.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
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
import kotlinx.android.synthetic.main.fragment_restaurant.view.editLocation

class RestaurantFragment : Fragment() {

    private var TAG = "RestaurantFragment"
    private var mSwipePlaceHolderView: SwipePlaceHolderView? = null
    private var mBtFilter: ImageButton? = null
    private var mBtRadioRealGroup: RadioRealButtonGroup? = null
    private var mEditLocation: EditText? = null
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mAddress: String? = null
    private var mContext: TabsActivity? = null
    private val LOCATION_REQUEST_CODE = 109

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
        mAddress = arguments!!.getString("abc")

        Log.i(TAG, "Restaurant data : $mRestaurantData")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.i(TAG, "onAttach : ")
        if (context is TabsActivity) {
            mContext = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_restaurant, null)
        mSwipePlaceHolderView = rootView!!.findViewById(R.id.swipeView) as SwipePlaceHolderView
        mBtFilter = rootView.findViewById(R.id.ibFilter) as ImageButton
        mBtRadioRealGroup = rootView.findViewById(R.id.segmentedButtonGroup) as RadioRealButtonGroup
        mEditLocation = rootView.findViewById(R.id.editLocation) as EditText
        mEditLocation!!.setText(mAddress)
        setSwipeableView()
        setLocationSearch(rootView)
        return rootView
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    private fun setLocationSearch(rootView: View) {
        rootView.editLocation.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
//                checkLocationPermission()
//                if (event.rawX >= (rootView.editLocation.right - rootView.editLocation.compoundDrawables[DRAWABLE_END].bounds.width())) {
//                    //Open Maps
//                    val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                        .build(activity)
//                    startActivityForResult(intent, REQUEST_LOCATION_MAPS)


                openLocationSearchDialog()
                return@OnTouchListener true
//                }
            }
            false
        })
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
                mSwipePlaceHolderView!!.addView(SwipeableCardView(mContext!!, data, mSwipePlaceHolderView!!))
            }
        }

        //mBtFilter!!.setOnClickListener { (activity as TabsActivity).replaceFragment(FilterFragment()) }

        mBtRadioRealGroup!!.setOnPositionChangedListener { _, currentPosition, _ ->
            when (currentPosition) {
                0 -> {
                    var one = mRestaurantData!!.map { it.restaurantType }.filter { it.equals("VEGETARIAN") }
                    Log.i(TAG, "VEG : $one")
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("VEGETERIAN")) {
                            mSwipePlaceHolderView!!.addView(
                                SwipeableCardView(
                                    mContext!!,
                                    data,
                                    mSwipePlaceHolderView!!
                                )
                            )
                        }
                    }
                }
                1 -> {
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("NON_VEGETERIAN")) {
                            mSwipePlaceHolderView!!.addView(
                                SwipeableCardView(
                                    context!!,
                                    data,
                                    mSwipePlaceHolderView!!
                                )
                            )
                        }
                    }
                }
                else -> {
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("BAR")) {
                            mSwipePlaceHolderView!!.addView(
                                SwipeableCardView(
                                    mContext!!,
                                    data,
                                    mSwipePlaceHolderView!!
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openLocationSearchDialog() {
        var fragment = SearchLocationFragment()
        fragment.setTargetFragment(this, 100)
        var fragmentManager = fragmentManager
        fragment.show(fragmentManager, "SearchLocation")
//        var fragmentTransaction = fragmentManager!!.beginTransaction()
//        fragmentTransaction.replace(R.id.fragmentContainer, SearchLocationFragment())
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, intent : Intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                var address = intent.getStringExtra ("456")
                Log.d(TAG, "Addresssssssssssssss : $address")
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