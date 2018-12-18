package com.hangloose.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Geocoder
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hangloose.R
import com.hangloose.ui.fragment.ProfileFragment
import com.hangloose.ui.fragment.RestaurantFragment
import com.hangloose.ui.fragment.SearchFragment
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA
import com.hangloose.utils.KEY_RESTAURANT_DATA
import kotlinx.android.synthetic.main.activity_tab.editLocation
import kotlinx.android.synthetic.main.activity_tab.tabLayout

class TabsActivity : BaseActivity() {

    private val TAG = "TabsActivity"
    private var mRestaurantData: ArrayList<RestaurantData>? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 109
    private val REQUEST_LOCATION_MAPS = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        mRestaurantData = intent.getParcelableArrayListExtra(KEY_RESTAURANT_DATA)
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        replaceFragment(RestaurantFragment())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        editLocation.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                Log.i(TAG, "Anjani1")
                if (event.rawX >= (editLocation.right - editLocation.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    //Open Maps
                    Log.i(TAG, "Anjani2")
                    val intent = Intent(this, LocationMapsActivity::class.java)
                    startActivityForResult(intent, REQUEST_LOCATION_MAPS)
                    return@OnTouchListener true
                }
            }
            false
        })
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
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    Log.i(TAG, "${location.latitude} && ${location.longitude}")
                    val geoCoder = Geocoder(this)
                    val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (address.size > 0) {
                        editLocation.setText(address[0].getAddressLine(0), TextView.BufferType.EDITABLE)
                    }
                }
            }
        }
    }

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
            LOCATION_REQUEST_CODE -> {
            }
        }
    }

    @SuppressLint("NewApi")
    override fun init() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.search))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.profile))

        tabLayout.setTabTextColors(Color.parseColor("#cccccc"), Color.parseColor("#b72125"))
        tabLayout.getTabAt(0)!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)

        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab!!.icon!!.setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_IN)
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when {
                        tabLayout.selectedTabPosition == 0 -> {
                            tab!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
                            replaceFragment(RestaurantFragment())
                        }
                        tabLayout.selectedTabPosition == 1 -> {
                            tab!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
                            replaceFragment(SearchFragment())
                        }
                        tabLayout.selectedTabPosition == 2 -> {
                            tab!!.icon!!.setColorFilter(Color.parseColor("#b72125"), PorterDuff.Mode.SRC_IN)
                            replaceFragment(ProfileFragment())
                        }
                    }
                }
            })
    }

    fun replaceFragment(fragment: Fragment) {
        Log.i(TAG, "Restaurant data : $mRestaurantData")
        if (fragment is RestaurantFragment) {
            val args = Bundle()
            args.putParcelableArrayList(KEY_DATA, mRestaurantData)
            fragment.arguments = args
        } else if (fragment is SearchFragment) {
            tabLayout.getTabAt(1)!!.select()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
