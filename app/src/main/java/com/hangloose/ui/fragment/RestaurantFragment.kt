package com.hangloose.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.hangloose.R
import com.hangloose.listener.LikedInsertionListener
import com.hangloose.listener.SavedInsertionListener
import com.hangloose.ui.activities.FilterActivity
import com.hangloose.ui.activities.SwipeableCardView
import com.hangloose.ui.activities.TabsActivity
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.*
import com.hangloose.utils.PreferenceHelper.get
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder
import kotlinx.android.synthetic.main.fragment_restaurant.view.editLocation


class RestaurantFragment : Fragment(), View.OnClickListener, LikedInsertionListener, SavedInsertionListener {

    private var TAG = "RestaurantFragment"
    private var mSwipePlaceHolderView: SwipePlaceHolderView? = null
    private var mBtFilter: ImageButton? = null
    private var mBtRadioRealGroup: RadioRealButtonGroup? = null
    private var mEditLocation: EditText? = null
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mAddress: String? = null
    private var mContext: TabsActivity? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()
    private var mHeader: String? = null

    private lateinit var mListenerCallback: LocationNavigationListener

    private var mPreference: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // retain this fragment
        retainInstance = true

        mPreference = PreferenceHelper.defaultPrefs(mContext!!)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
        mActivitiesSelectedList = arguments!!.getStringArrayList(KEY_ACTIVITIES_LIST)
        mAdventuresSelectedList = arguments!!.getStringArrayList(KEY_ADVENTURES_LIST)
        val headerToken: String? = mPreference!![X_AUTH_TOKEN]
        mAddress = mPreference!![PREFERENCE_ADDRESS]
        Log.i(TAG, """Header : $headerToken""")
        Log.i(TAG, """Address99999999454 : $mAddress""")
        mHeader = headerToken
        Log.i(TAG, "LikedRestaurant data : $mRestaurantData")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is TabsActivity) {
            mContext = context
        }
        try {
            mListenerCallback = context as LocationNavigationListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement ContentListener"
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_restaurant, null)
        mSwipePlaceHolderView = rootView!!.findViewById(R.id.swipeView) as SwipePlaceHolderView
        mBtFilter = rootView.findViewById(R.id.ibFilter) as ImageButton
        mBtRadioRealGroup = rootView.findViewById(R.id.segmentedButtonGroup) as RadioRealButtonGroup
        mEditLocation = rootView.findViewById(R.id.editLocation) as EditText
        mBtFilter!!.setOnClickListener(this)
        setSwipeableView()
        setLocationSearch(rootView)
        if (!TextUtils.isEmpty(mAddress)) {
            mEditLocation!!.setText(mAddress)
        }
        return rootView
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ibFilter -> {
                val intent = Intent(context, FilterActivity::class.java)
                startActivityForResult(intent, REQUEST_FILTER_ACTIVITY)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setLocationSearch(rootView: View) {
        rootView.editLocation.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                mListenerCallback.navigateToLocationActivityFromHomeFrag()
                return@OnTouchListener true
            }
            false
        })
    }

    interface LocationNavigationListener {
        fun navigateToLocationActivityFromHomeFrag()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onLikedRecordInserted(id: Long) {
        Log.i("Hangloose", "onLikedRecordInserted")
    }

    override fun onSavedRecordInserted(id: Long) {
        Log.i("Hangloose", "onSavedRecordInserted")

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
                mSwipePlaceHolderView!!.addView(
                    SwipeableCardView(
                        mContext!!,
                        data,
                        mSwipePlaceHolderView!!,
                        this,
                        this
                    )
                )
            }
        }

//        mBtFilter!!.setOnClickListener { (activity as TabsActivity).replaceFragment(FilterFragment()) }

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
                                    mSwipePlaceHolderView!!,
                                    this, this
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
                                    mContext!!,
                                    data,
                                    mSwipePlaceHolderView!!,
                                    this, this
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
                                    mSwipePlaceHolderView!!,
                                    this, this
                                )
                            )
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