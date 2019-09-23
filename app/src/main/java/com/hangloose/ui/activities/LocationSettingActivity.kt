package com.hangloose.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.hangloose.R
import com.hangloose.databinding.ActivityEnableLocationBinding
import com.hangloose.model.RestaurantList
import com.hangloose.ui.adapter.PlacesAutoCompleteAdapter
import com.hangloose.ui.adapter.RecyclerItemClickListener
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.*
import com.hangloose.utils.PreferenceHelper.get
import com.hangloose.utils.PreferenceHelper.set
import com.hangloose.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.activity_enable_location.*
import retrofit2.Response
import java.io.IOException


//https://www.androhub.com/bottom-sheets-dialog-in-android/
//https://www.androidhive.info/2017/12/android-working-with-bottom-sheet/
class LocationSettingActivity : BaseActivity(), View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener {

    private var TAG = "LocationSettingActivity"

    private var mAdapter: PlacesAutoCompleteAdapter? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    private val mLatLngBounds: LatLngBounds = LatLngBounds(LatLng(-0.0, 0.0), LatLng(0.0, 0.0))
    private var mAddress: String? = null

    private var mLocationViewModel: LocationViewModel? = null
    private var mRestaurantData: ArrayList<RestaurantData> = ArrayList()
    private var mPreference: SharedPreferences? = null
    private var mHeaderToken: String? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()

    private val LOCATION_REQUEST_CODE = 109
    private val REQUEST_CHECK_SETTINGS = 110
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mManager: LocationManager? = null
    var mFlagLocationNavigation = 0

    private var mActivityLocationBinding: ActivityEnableLocationBinding? = null

    private var mEntireRestaurantData = ArrayList<RestaurantData>()
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null

    val mCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            onLocationChanged(locationResult!!.lastLocation)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreference = PreferenceHelper.defaultPrefs(this)
        getIntentData()
        initBinding()
        getHeaderAndAddressFromPreference()
        buildGoogleApiClient()
        setAdapterForLocationList()
    }

    override fun init() {}

    override fun onResume() {
        super.onResume()
        if (!mGoogleApiClient!!.isConnected && !mGoogleApiClient!!.isConnecting) {
            mGoogleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }


    override fun onConnected(p0: Bundle?) {
        Log.d("Google API Callback", "Connection Done")
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("Google API Callback", "Connection Suspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("Google API Callback", "Connection Failed")
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            tvGetCurrentLocation!!.id -> {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                checkLocationPermission()
            }
            ivArrowBackLocation!!.id -> {
                backPressNavigation()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v!!.id) {
            etLocationSearchLocation!!.id -> {
                if (event!!.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= etLocationSearchLocation!!.right - etLocationSearchLocation!!.totalPaddingRight) {
                        etLocationSearchLocation!!.setText("")
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onBackPressed() {
        backPressNavigation()
    }

    private fun backPressNavigation() {
        super.onBackPressed()
        if (mFlagLocationNavigation == 1) {
            intent.putParcelableArrayListExtra(KEY_RESTAURANT_DATA, null)
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        finish()
    }

    private fun navigateToRestaurantScreen() {
        if (mFlagLocationNavigation == 0) {
            val intent = Intent(this, TabsActivity::class.java)
            intent.putStringArrayListExtra(KEY_ACTIVITIES_LIST, mActivitiesSelectedList)
            intent.putStringArrayListExtra(KEY_ADVENTURES_LIST, mAdventuresSelectedList)
            intent.putExtra(KEY_LATITUDE, mLatitude)
            intent.putExtra(KEY_LONGTITUDE, mLongitude)
            startActivity(intent)
        } else {
            Log.d(TAG, "LikedRestaurant Data : $mRestaurantData")
            val intent = Intent()
            intent.putExtra(KEY_LATITUDE, mLatitude)
            intent.putExtra(KEY_LONGTITUDE, mLongitude)
            setResult(START_LOCATION_SCREEN, intent)
            finish()
        }
    }

    private fun initBinding() {
        mActivityLocationBinding = DataBindingUtil.setContentView(this, R.layout.activity_enable_location)
        mLocationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        mActivityLocationBinding!!.locationViewModel = mLocationViewModel

        etLocationSearchLocation!!.setOnTouchListener(this)
        tvGetCurrentLocation!!.setOnClickListener(this)
        ivArrowBackLocation!!.setOnClickListener(this)
        setArrowBackVisibilityForHome()
        mLocationViewModel!!.getRestaurantList().observe(this, Observer<Response<List<RestaurantList>>> { t ->
            val data = t!!.body()
            (0 until data!!.size).forEach { i ->
                if (data[i].distanceFromLocation!! <= 50) {
                    var logo: String? = null
                    val ambienceList: ArrayList<String>? = ArrayList()
                    var menuList: ArrayList<String>? = ArrayList()
                    (0 until data[i].documents!!.size).forEach { j ->
                        when {
                            data[i].documents!![j].documentType.equals("LOGO") -> {
                                Log.d(TAG, "Logo : " + data[i].documents!![j].location)
                                logo = data[i].documents!![j].location
                            }
                            data[i].documents!![j].documentType.equals("AMBIENCE") -> ambienceList!!.add(data[i].documents!![j].location!!)
                            else -> menuList!!.add(data[i].documents!![j].location!!)
                        }
                    }
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
                            data[i].updatedAt,
                            data[i].distanceFromLocation,
                            data[i].about,
                            data[i].tags,
                            data[i].openCloseTime,
                            data[i].number,
                            logo,
                            ambienceList,
                            menuList
                        )
                    )
                }

                var logo: String? = null
                val ambienceList: ArrayList<String>? = ArrayList()
                val menuList: ArrayList<String>? = ArrayList()
                (0 until data[i].documents!!.size).forEach { j ->
                    if (data[i].documents!![j].documentType.equals("LOGO")) {
                        Log.d(TAG, "Logo : " + data[i].documents!![j].location)
                        logo = data[i].documents!![j].location
                    } else if (data[i].documents!![j].documentType.equals("AMBIENCE")) {
                        ambienceList!!.add(data[i].documents!![j].location!!)
                    } else {
                        menuList!!.add(data[i].documents!![j].location!!)
                    }
                }
                mEntireRestaurantData.add(
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
                        data[i].updatedAt,
                        data[i].distanceFromLocation,
                        data[i].about,
                        data[i].tags,
                        data[i].openCloseTime,
                        data[i].number,
                        logo,
                        ambienceList,
                        menuList
                    )
                )
            }
            Log.d(TAG, "Response")
            mPreference!![PREFERENCE_ADDRESS] = mAddress
            setRestaurantData(mRestaurantData)
            setEntireRestaurantData(mEntireRestaurantData)
            navigateToRestaurantScreen()
        })

        mLocationViewModel!!.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                layoutParentLocation,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
    }

    private fun setArrowBackVisibilityForHome() {
        if (mFlagLocationNavigation == 0) {
            ivArrowBackLocation.visibility = View.GONE
        } else {
            ivArrowBackLocation.visibility = View.VISIBLE
        }
    }

    private fun setAdapterForLocationList() {
        val layoutManager = LinearLayoutManager(this)
        rvAddressesLocation.layoutManager = layoutManager
        mAdapter = PlacesAutoCompleteAdapter(this, mGoogleApiClient!!, mLatLngBounds)
        rvAddressesLocation.adapter = mAdapter
        addSearchListener()

        rvAddressesLocation.addOnItemTouchListener(
            RecyclerItemClickListener(this, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val item = mAdapter!!.getItem(position)
                    val placeId: String = item.placeId.toString()
                    Log.i(TAG, "Autocomplete item selected: " + item.description)

                    val placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient!!, placeId)
                    placeResult.setResultCallback { places ->
                        if (places.count == 1) {
                            //Do the things here on Click.....
                            mAddress = item.description.toString()
                            var locationWithLatLong = getLatLongFromLocationName(this@LocationSettingActivity, mAddress)
                            Log.d(TAG, "LocationWithLatLong : $locationWithLatLong")
                            hideSoftKeyboard(this@LocationSettingActivity)
                            etLocationSearchLocation.setText("")
                            locationWithLatLong?.let {
                                Log.d(
                                    TAG,
                                    "LatLong : " + locationWithLatLong.latitude + "-" + locationWithLatLong.longitude
                                )
                                mLatitude = locationWithLatLong.latitude
                                mLongitude = locationWithLatLong.longitude
                                mLocationViewModel!!.restaurantListApiRequest(
                                    mActivitiesSelectedList, mAdventuresSelectedList, "", ""
                                    , locationWithLatLong.latitude, locationWithLatLong.longitude, mHeaderToken,
                                    "")
                            }
                        } else {
                            Toast.makeText(
                                this@LocationSettingActivity,
                                getString(R.string.slow_internet),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                    Log.i(TAG, "Clicked: " + item.description)
                    Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId)
                }
            }
            ))
    }

    private fun addSearchListener() {
        etLocationSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "Address : " + s.toString())
                if (s.toString() == "" || !mGoogleApiClient!!.isConnected) {
                    if (mGoogleApiClient!!.isConnected) {
                        mAdapter!!.filter.filter(s.toString())
                    }
                } else {
                    mAdapter!!.filter.filter(s.toString())
                }
                if (s.toString() != "" && mGoogleApiClient!!.isConnected) {
                    mAdapter!!.filter.filter(s.toString())
                    tvGetCurrentLocation!!.visibility = View.GONE
                    firstSeperatorLocation!!.visibility = View.GONE
                } else if (!mGoogleApiClient!!.isConnected) {
                    tvGetCurrentLocation!!.visibility = View.VISIBLE
                    firstSeperatorLocation!!.visibility = View.VISIBLE
                } else {
                    tvGetCurrentLocation!!.visibility = View.VISIBLE
                    firstSeperatorLocation!!.visibility = View.VISIBLE
                }
            }
        })
    }


    @Synchronized
    private fun buildGoogleApiClient() {
        Log.d(TAG, " 111111111 buildGoogleApiClient")
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .addApi(Places.GEO_DATA_API)
            .build()
    }

    private fun getHeaderAndAddressFromPreference() {
        mAddress = mPreference!![PREFERENCE_ADDRESS]
        mHeaderToken = mPreference!![X_AUTH_TOKEN]
    }

    private fun getIntentData() {
        mActivitiesSelectedList = intent!!.getStringArrayListExtra(KEY_ACTIVITIES_LIST)
        mAdventuresSelectedList = intent!!.getStringArrayListExtra(KEY_ADVENTURES_LIST)
        mFlagLocationNavigation = intent!!.getIntExtra(FLAG_LOCATION_NAVIGATION, 0)
    }

    //---------------------------Location------------------------------------------------------------------------------------------------

    private fun checkLocationPermission() {
        mManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            } else {
                enableGPS()
            }
        } else {
            enableGPS()
            Log.d(TAG, "Network Enable : $mManager")
        }
    }

    @SuppressLint("MissingPermission")
    private fun buildAlertMessageNoGps() {
        var builder = AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, which ->
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                if (!mManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps()
                } else {
                    var location = mManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    Log.d(TAG, "Location11111 : $location")
                    //onLocationChanged(location)
                    enableGPS()
                }
            }
            .setNegativeButton(
                "No"
            ) { dialog, which -> dialog!!.cancel() }
        var alert = builder.create();
        alert.show();
    }

    @SuppressLint("MissingPermission")
    private fun enableGPS() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000
        locationRequest.fastestInterval = 5000
        val mLocationSettingRequestBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        mLocationSettingRequestBuilder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(this)
            .checkLocationSettings(mLocationSettingRequestBuilder.build())

        mFusedLocationClient.requestLocationUpdates(locationRequest, mCallback, Looper.myLooper())

        result.addOnSuccessListener { getUserLocation() }
        result.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
//                    exception.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS)
                ActivityCompat.startIntentSenderForResult(
                    this,
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

    private fun onLocationChanged(location: Location) {
        mFusedLocationClient.removeLocationUpdates(mCallback)
        try {
            val geoCoder = Geocoder(this)
            val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
            if (address.size > 0) {
                mAddress = address[0].getAddressLine(0)
                Log.d("Fragment", "Addressssssssss : $mAddress")
                hideSoftKeyboard(this)
                location.let {
                    mLatitude = location.latitude
                    mLongitude = location.longitude
                    mLocationViewModel!!.restaurantListApiRequest(
                        mActivitiesSelectedList,
                        mAdventuresSelectedList,
                        "",
                        "",
                        location.latitude,
                        location.longitude,
                        mHeaderToken, ""
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                this, "Network Error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    //18.5599463 && 73.7913587
                    //18.5592426 && 73.7938646
                    //18.5589954 && 73.7928294
                    Log.d("hgcgjjjh", "Address00000000000 : ${location.latitude} && ${location.longitude}")
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
                        this,
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
                        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
                            mGoogleApiClient!!.disconnect()
                            mGoogleApiClient = null
                            mFusedLocationClient.removeLocationUpdates(mCallback)
                        }
                        Toast.makeText(
                            this@LocationSettingActivity,
                            "Please enable Location service!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.clear()
    }
}