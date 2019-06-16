package com.hangloose.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
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
import com.hangloose.utils.KEY_ACTIVITIES_LIST
import com.hangloose.utils.KEY_ADVENTURES_LIST
import kotlinx.android.synthetic.main.activity_location_setting.*
import android.support.design.widget.BottomSheetBehavior
import com.hangloose.R


class LocationSettingActivity : BaseActivity(), View.OnClickListener {

    private var TAG = "LocationSettingActivity"

    private val REQUEST_LOCATION_MAPS = 9
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 109
    private var mAddress: String? = null
    private val REQUEST_CHECK_SETTINGS = 10
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()

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
        mActivitiesSelectedList = intent.getStringArrayListExtra(KEY_ACTIVITIES_LIST)
        mAdventuresSelectedList = intent.getStringArrayListExtra(KEY_ADVENTURES_LIST)
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
        val geoCoder = Geocoder(this)
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        if (address.size > 0) {
            mAddress = address[0].getAddressLine(0)
            //mSelectionViewModel.restaurantListApiRequest(mActivitiesSelectedList, mAdventuresSelectedList)
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
                    Log.i(TAG, "${location.latitude} && ${location.longitude}")
                    onLocationChanged(location)
                }
            }
        }
    }

    private fun openLocationSearchDialog() {
        var searchLocationFragment = SearchLocationFragment()
        searchLocationFragment.show(supportFragmentManager, "LocationDialog")

        /*val behavior = BottomSheetBehavior.from(searchLocationFragment)
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        behavior.setPeekHeight(0)*/
    }
}