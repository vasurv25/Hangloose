package com.hangloose.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.hangloose.R
import com.hangloose.databinding.ActivitySelectionBinding
import com.hangloose.model.RestaurantList
import com.hangloose.ui.adapter.ViewPagerAdapter
import com.hangloose.ui.fragment.ActivitiesFragment
import com.hangloose.ui.fragment.AdventuresFragment
import com.hangloose.ui.model.ActivitiesDetails
import com.hangloose.ui.model.AdventuresDetails
import com.hangloose.ui.model.RestaurantData
import com.hangloose.ui.model.SelectionList
import com.hangloose.utils.KEY_ACTIVITIES_LIST
import com.hangloose.utils.PreferenceHelper
import com.hangloose.utils.showSnackBar
import com.hangloose.viewmodel.SelectionViewModel
import kotlinx.android.synthetic.main.activity_selection.btNextSelection
import kotlinx.android.synthetic.main.activity_selection.indicator
import kotlinx.android.synthetic.main.activity_selection.ll_selection
import kotlinx.android.synthetic.main.activity_selection.tvSelectionHeading
import kotlinx.android.synthetic.main.activity_selection.viewPager
import retrofit2.Response

class SelectionActivity : BaseActivity() {

    private var TAG = "SelectionActivity"
    var didClickNextButton: (() -> Unit)? = null
    private lateinit var mSelectionViewModel: SelectionViewModel
    private var mActivitiesList = ArrayList<ActivitiesDetails>()
    private var mAdventuresList = ArrayList<AdventuresDetails>()
    private var mActivitySelectionBinding: ActivitySelectionBinding? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()
    private var mActivitiesFragment: ActivitiesFragment? = null
    private var mAdventuresFragment: AdventuresFragment? = null
    private var mRestaurantData = ArrayList<RestaurantData>()
    private var mPreference: SharedPreferences? = null

    private val REQUEST_LOCATION_MAPS = 9
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 109
    private var mAddress: String? = null
    private val REQUEST_CHECK_SETTINGS = 10
    private var mGoogleApiClient: GoogleApiClient? = null
    val mCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            onLocationChanged(locationResult!!.lastLocation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreference = PreferenceHelper.defaultPrefs(this)
        initBinding()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun init() {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                checkLocationPermission()
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

    //override fun onBackPressed() {}

    private fun setFragments() {
        Log.i(TAG, "init")
        tvSelectionHeading.text = getString(R.string.select_your_activities)
        val listOfFragment = ArrayList<Fragment>()
        mActivitiesFragment = ActivitiesFragment.newInstance(mActivitiesList)
        mAdventuresFragment = AdventuresFragment.newInstance(mAdventuresList)
        listOfFragment.add(mActivitiesFragment!!)
        listOfFragment.add(mAdventuresFragment!!)
        val viewPagerAdapter = ViewPagerAdapter(applicationContext, supportFragmentManager, listOfFragment)
        viewPager.adapter = viewPagerAdapter
        indicator.setViewPager(viewPager)
        viewPager.currentItem = 0
        viewPagerAdapter.registerDataSetObserver(indicator.dataSetObserver)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (viewPager.currentItem == 0) {
                    tvSelectionHeading.text = getString(R.string.select_your_activities)
                    btNextSelection.visibility = View.GONE
                } else {
                    tvSelectionHeading.text = getString(R.string.select_your_adventure)
                    btNextSelection.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    private fun initBinding() {
        mActivitySelectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_selection)
        mActivitySelectionBinding!!.clickHandler = this
        mSelectionViewModel = ViewModelProviders.of(this).get(SelectionViewModel::class.java)
        mActivitySelectionBinding!!.selectionViewModel = mSelectionViewModel
        mSelectionViewModel.selectionListApiRequest()
        mSelectionViewModel.getSelectionList().observe(this, Observer<SelectionList> { t ->
            Log.i(TAG, "onChanged")
            (0 until t!!.activities.size).forEach { i ->
                val list = t.activities
                Log.i(TAG, "Activities : " + list[i].id!!)
                mActivitiesList.add(
                    ActivitiesDetails(
                        list[i].createdAt!!,
                        list[i].updatedAt!!,
                        list[i].id!!,
                        list[i].name!!,
                        list[i].image!!
                    )
                )
            }
            (0 until t.adventures.size).forEach { i ->
                val list = t.adventures
                Log.i(TAG, "Adventures : " + list[i].id!!)
                mAdventuresList.add(
                    AdventuresDetails(
                        list[i].createdAt!!,
                        list[i].updatedAt!!,
                        list[i].id!!,
                        list[i].name!!,
                        list[i].image!!
                    )
                )
            }
            setFragments()
        })

        mSelectionViewModel.getRestaurantList().observe(this, Observer<Response<List<RestaurantList>>> { t ->
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
            //checkLocationPermission()
            //onNavigateToLocationScreen()
        })

        mSelectionViewModel.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                ll_selection,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
    }

    fun oNextClick(view: View?) {
        //didClickNextButton?.invoke()
        mActivitiesSelectedList.addAll(mActivitiesFragment!!.getSelectedActivities())
        mAdventuresSelectedList.addAll(mAdventuresFragment!!.getSelectedAdventures())
        if (mActivitiesSelectedList.size != 0 && mAdventuresSelectedList.size != 0) {
            //mSelectionViewModel.restaurantListApiRequest(mActivitiesSelectedList, mAdventuresSelectedList)
            onNavigateToLocationScreen(mActivitiesSelectedList, mActivitiesSelectedList)
        }
    }

    private fun onNavigateToLocationScreen(
        mActivitiesSelectedList: ArrayList<String>,
        mActivitiesSelectedList1: ArrayList<String>
    ) {
        val intent = Intent(this, LocationSettingActivity::class.java)
        intent.putParcelableArrayListExtra(KEY_ACTIVITIES_LIST, mActivitiesList)
        intent.putParcelableArrayListExtra(KEY_ACTIVITIES_LIST, mAdventuresList)
        startActivity(intent)
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
            mSelectionViewModel.restaurantListApiRequest(mActivitiesSelectedList, mAdventuresSelectedList)
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
}