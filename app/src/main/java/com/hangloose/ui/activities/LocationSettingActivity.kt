package com.hangloose.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hangloose.ui.fragment.SearchLocationFragment
import com.hangloose.R
import kotlinx.android.synthetic.main.content_location_setting.*
import android.support.design.widget.BottomSheetBehavior
import com.hangloose.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.activity_location_setting.*
import android.arch.lifecycle.Observer
import com.hangloose.model.RestaurantList
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_RESTAURANT_DATA
import retrofit2.Response

//https://www.androhub.com/bottom-sheets-dialog-in-android/
//https://www.androidhive.info/2017/12/android-working-with-bottom-sheet/
class LocationSettingActivity : BaseActivity(), View.OnClickListener, SearchLocationFragment.ContentListener {

    private var TAG = "LocationSettingActivity"

    private val REQUEST_LOCATION_MAPS = 9
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 109
    private var mAddress: String? = null
    private val REQUEST_CHECK_SETTINGS = 10
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var mLocationViewModel : LocationViewModel? = null
    private var mRestaurantData = ArrayList<RestaurantData>()

    val mCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            onLocationChanged(locationResult!!.lastLocation)
        }
    }


    override fun init() {
        btnEnableLocation.setOnClickListener(this)
        tvLocationManually.setOnClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_setting)
        //mActivitiesSelectedList = intent.getStringArrayListExtra(KEY_ACTIVITIES_LIST)
        //mAdventuresSelectedList = intent.getStringArrayListExtra(KEY_ADVENTURES_LIST)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        //By default set BottomSheet Behavior as Collapsed and Height 0
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetBehavior!!.peekHeight = 0

        if (mBottomSheetBehavior != null && mBottomSheetBehavior is BottomSheetBehavior<*>) {
            mBottomSheetBehavior!!.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

        initBinding()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            btnEnableLocation.id -> {
                onEnableLocationClick()
            }
            tvLocationManually.id -> {
                openLocationSearchDialog()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                //checkLocationPermission()
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
                        Toast.makeText(this, "Please enable Location service!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            }
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior!!.peekHeight = 1000
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onItemClicked(location: String?) {
        mAddress = location
        mLocationViewModel!!.restaurantListApiRequest()
    }

    private fun initBinding() {
        mLocationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        mLocationViewModel!!.getRestaurantList().observe(this, Observer<Response<List<RestaurantList>>> { t ->
            val data = t!!.body()
            (0 until data!!.size).forEach { i ->
                mRestaurantData.add(
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
            navigateToTabsActivity()
        })
    }

    private fun navigateToTabsActivity() {
        var intent = Intent(this, TabsActivity::class.java)
        intent.putExtra("123", mAddress)
        intent.putParcelableArrayListExtra(KEY_RESTAURANT_DATA, mRestaurantData)
        startActivity(intent)
    }


    private fun onEnableLocationClick() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
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
    }

    @SuppressLint("MissingPermission")
    private fun enableGPS() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build()
            mGoogleApiClient!!.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 1000 / 2
            locationRequest.fastestInterval = 1000 / 4
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
    }

    private fun onLocationChanged(location: Location) {
        mFusedLocationClient.removeLocationUpdates(mCallback)
        /*val geoCoder = Geocoder(this)
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        if (address.size > 0) {
            mAddress = address[0].getAddressLine(0)
            //mSelectionViewModel.restaurantListApiRequest(mActivitiesSelectedList, mAdventuresSelectedList)
        }*/
        mLocationViewModel!!.restaurantListApiRequest()
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
                    Log.i(TAG, "${location.latitude} && ${location.longitude}")
                    onLocationChanged(location)
                }
            }
        }
    }

    private fun openLocationSearchDialog() {
        var searchLocationFragment = SearchLocationFragment()
        searchLocationFragment.show(supportFragmentManager, "LocationDialog")
    }
}