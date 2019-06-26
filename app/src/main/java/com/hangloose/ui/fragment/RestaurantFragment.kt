package com.hangloose.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hangloose.R
import com.hangloose.model.RestaurantList
import com.hangloose.ui.activities.FilterActivity
import com.hangloose.ui.activities.SwipeableCardView
import com.hangloose.ui.activities.TabsActivity
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.*
import com.hangloose.utils.PreferenceHelper.get
import com.hangloose.utils.PreferenceHelper.set
import com.hangloose.viewmodel.LocationViewModel
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder
import kotlinx.android.synthetic.main.fragment_restaurant.*
import kotlinx.android.synthetic.main.fragment_restaurant.view.editLocation
import org.w3c.dom.Text
import retrofit2.Response


class RestaurantFragment : Fragment(), View.OnClickListener {

    private var TAG = "RestaurantFragment"
    private var mSwipePlaceHolderView: SwipePlaceHolderView? = null
    private var mBtFilter: ImageButton? = null
    private var mBtRadioRealGroup: RadioRealButtonGroup? = null
    private var mEditLocation: EditText? = null
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mAddress: String? = null
    private var mContext: TabsActivity? = null
    private var mFlag = 0
    private val LOCATION_REQUEST_CODE = 109
    private var mSecondSearchLocationFragment: SecondSearchLocationFragment? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()

    private var mGoogleApiClient: GoogleApiClient? = null
    private val REQUEST_CHECK_SETTINGS = 10
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocationViewModel: LocationViewModel? = null
    private var mHeader: String? = null

    private var mPreference: SharedPreferences? = null
    var mFragmentInstance: RestaurantFragment? = null

    val mCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            onLocationChanged(locationResult!!.lastLocation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("0000000000000 : ", "111111111 onCreateRF$")

        // retain this fragment
        retainInstance = true

        mPreference = PreferenceHelper.defaultPrefs(mContext!!)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
        mFlag = arguments!!.getInt("111")
        mActivitiesSelectedList = arguments!!.getStringArrayList(KEY_ACTIVITIES_LIST)
        mAdventuresSelectedList = arguments!!.getStringArrayList(KEY_ADVENTURES_LIST)
        val headerToken: String? = mPreference!![X_AUTH_TOKEN]
        mAddress = mPreference!![PREFERENCE_ADDRESS]
        Log.i(TAG, """Header : $headerToken""")
        Log.i(TAG, """Address99999999454 : $mAddress""")
        mHeader = headerToken

        Log.i(TAG, "Flag : $mFlag")
        Log.i(TAG, "Restaurant data : $mRestaurantData")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is TabsActivity) {
            mContext = context
            Log.d("0000000000000 : ", "111111111 onAttachRF : $mContext")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_restaurant, null)
        mSwipePlaceHolderView = rootView!!.findViewById(R.id.swipeView) as SwipePlaceHolderView
        mBtFilter = rootView.findViewById(R.id.ibFilter) as ImageButton
        mBtRadioRealGroup = rootView.findViewById(R.id.segmentedButtonGroup) as RadioRealButtonGroup
        mEditLocation = rootView.findViewById(R.id.editLocation) as EditText
        Log.d("0000000000000 : ", "111111111 onCreateRF :" + mContext)

        setSwipeableView()
        setLocationSearch(rootView)
        Log.d("0000000000000 : ", "111111111 onCreateRF")

        if (!TextUtils.isEmpty(mAddress)) {
            mEditLocation!!.setText(mAddress)
        }

        mLocationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        mLocationViewModel!!.getRestaurantList().observe(this, Observer<Response<List<RestaurantList>>> { t ->
            val data = t!!.body()
            (0 until data!!.size).forEach { i ->
                mRestaurantData!!.add(
                    RestaurantData(
                        data[i].address!!,
                        data[i].createdAt,
                        data[i].discount,
                        data[i].id,
                        data[i].images,
                        data[i].latitude,
                        data[i].longitude,
                        data[i].name,
                        data[i].offer,
                        data[i].priceFortwo,
                        data[i].ratings,
                        data[i].restaurantType,
                        data[i].updatedAt
                    )
                )
            }
            Log.d(TAG, "Response")
        })
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext!!)
        mBtFilter!!.setOnClickListener(this)
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

    override fun onPause() {
        super.onPause()
        Log.d("0000000000000 : ", "111111111 onPauseRF")
        mFusedLocationClient.flushLocations()
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
                                    mContext!!,
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
        mSecondSearchLocationFragment = SecondSearchLocationFragment()
        mSecondSearchLocationFragment!!.setTargetFragment(this, 100)
        var fragmentManager = fragmentManager
        mSecondSearchLocationFragment!!.show(fragmentManager, "SecondSearchLocation")
//        var fragmentTransaction = fragmentManager!!.beginTransaction()
//        fragmentTransaction.replace(R.id.fragmentContainer, SearchLocationFragment())
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
    }

    /*override fun onActivityResult(requestCode : Int, resultCode : Int, intent : Intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                var address = intent.getStringExtra ("456")
                mEditLocation!!.setText(address)
                Log.d(TAG, "Addresssssssssssssss : $address")
                mSecondSearchLocationFragment!!.dialog.dismiss()
            }
        }
    }*/

    private fun checkLocationPermission() {
        var activity = activity
        Log.d("Frag", "Context : $mContext" + "-" + isAdded)
        if (mContext != null) {
            if (ActivityCompat.checkSelfPermission(
                    mContext!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    mContext!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (isAdded) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        LOCATION_REQUEST_CODE
                    )
                }
            } else if (isAdded) {
                enableGPS()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableGPS() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(mContext!!)
                .addApi(LocationServices.API)
                .build()
            mGoogleApiClient!!.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10 * 1000
            locationRequest.fastestInterval = 5000
            val mLocationSettingRequestBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            mLocationSettingRequestBuilder.setAlwaysShow(true)

            val result = LocationServices.getSettingsClient(mContext!!)
                .checkLocationSettings(mLocationSettingRequestBuilder.build())

            mFusedLocationClient.requestLocationUpdates(locationRequest, mCallback, Looper.myLooper())

            result.addOnSuccessListener { getUserLocation() }
            result.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
//                    exception.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS)
                    ActivityCompat.startIntentSenderForResult(
                        mContext!!,
                        exception.resolution.intentSender,
                        REQUEST_CHECK_SETTINGS,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                }
            }
        }
    }

    private fun onLocationChanged(location: Location) {
        mFusedLocationClient.removeLocationUpdates(mCallback)
        val geoCoder = Geocoder(mContext)
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        if (address.size > 0) {
            mAddress = address[0].getAddressLine(0)
            Log.d("Fragment", "Addressssssssss : $mAddress")
            //mSelectionViewModel.restaurantListApiRequest(mActivitiesSelectedList, mAdventuresSelectedList)
            mLocationViewModel!!.restaurantListApiRequest(
                mActivitiesSelectedList,
                mAdventuresSelectedList,
                location.latitude,
                location.longitude,
                mHeader
            )
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                mContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                mContext!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                if (location != null) {
                    //18.5599463 && 73.7913587
                    //18.5592426 && 73.7938646
                    //18.5589954 && 73.7928294
                    Log.d("hgcgjjjh", "Address00000000000 : ${location.latitude} && ${location.longitude}")
                    Toast.makeText(mContext, "123" + location.latitude + location.longitude, Toast.LENGTH_SHORT).show()
                    onLocationChanged(location)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("Frag", "Request code : $requestCode")
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (ActivityCompat.checkSelfPermission(
                        mContext!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    checkLocationPermission()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val handler = Handler().postDelayed({ getUserLocation() }, 1000)
                    }
                    Activity.RESULT_CANCELED -> {
                        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected()) {
                            mGoogleApiClient!!.disconnect()
                            mGoogleApiClient = null
                            mFusedLocationClient.removeLocationUpdates(mCallback)
                        }
                        Toast.makeText(mContext, "Please enable Location service!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            100 -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == 100) {
                        var address = data!!.getStringExtra("456")
                        mAddress = address
                        mEditLocation?.let {
                            mEditLocation!!.setText(mAddress)
                        }
                        mPreference!![PREFERENCE_ADDRESS] = mAddress
                        mSecondSearchLocationFragment?.let { mSecondSearchLocationFragment!!.dialog.dismiss() }
                        var addressWithLatLong = mContext?.let { getLatLongFromLocationName(it, address) }
                        Log.d(TAG, "Addresssssssssssssss : ${addressWithLatLong!!.latitude}")
                        mLocationViewModel!!.restaurantListApiRequest(
                            mActivitiesSelectedList, mAdventuresSelectedList
                            , addressWithLatLong.latitude, addressWithLatLong.longitude, mHeader
                        )
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    checkLocationPermission()
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