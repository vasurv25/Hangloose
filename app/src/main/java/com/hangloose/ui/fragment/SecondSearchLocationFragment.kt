package com.hangloose.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.hangloose.R
import com.hangloose.ui.adapter.PlacesAutoCompleteAdapter
import com.hangloose.ui.adapter.RecyclerItemClickListener
import com.hangloose.utils.API_NOT_CONNECTED
import com.hangloose.utils.KEY_ACTIVITIES_LIST
import com.hangloose.utils.KEY_ADVENTURES_LIST
import com.hangloose.viewmodel.LocationViewModel

class SecondSearchLocationFragment : BottomSheetDialogFragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener, View.OnClickListener {

    private var mAddressSearch: EditText? = null

    private var mAdapter: PlacesAutoCompleteAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mTextCurrentLocation: TextView? = null
    private var mViewSeparator: View? = null
    private var mLayoutParent: ConstraintLayout? = null
    private var mDialogDownArrow: ImageView? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    private val mLatLngBounds: LatLngBounds = LatLngBounds(LatLng(-0.0, 0.0), LatLng(0.0, 0.0))

    private var mAddress: String? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d("0000000000000 : ", "111111111 onAttach")
    }

    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        Log.d("0000000000000 : ", "111111111 setupDialog")
        val rootView = View.inflate(context, R.layout.second_fragment_bottom_sheet, null)
        dialog!!.setContentView(rootView)
        Log.d("555555555", "889898jfgjghdhgdsgrsgs")
        mAddressSearch = rootView.findViewById(R.id.etLocationSearchIn)
        mRecyclerView = rootView.findViewById(R.id.rvAddressesIn)
        mTextCurrentLocation = rootView.findViewById(R.id.tvCurrentLocationIn)
        mViewSeparator = rootView.findViewById(R.id.firstSeperatorIn)
        mLayoutParent = rootView.findViewById(R.id.layoutParentIn)
        mDialogDownArrow = rootView.findViewById(R.id.ivArrowDownIn)

        mAddressSearch!!.setOnTouchListener(this)
        mDialogDownArrow!!.setOnClickListener(this)
        mTextCurrentLocation!!.setOnClickListener(this)

        setDialogLayoutHeight()

        buildGoogleApiClient()

        //Set the coordinator layout behavior
        val params = (rootView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)

            rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    Log.d("AAA", "Height : " + rootView.measuredHeight)
                    val height = rootView.measuredHeight
                    behavior.peekHeight = height
                }
            })
        }

        val layoutManager = LinearLayoutManager(context)
        mRecyclerView!!.layoutManager = layoutManager
        mAdapter = PlacesAutoCompleteAdapter(context!!, mGoogleApiClient!!, mLatLngBounds!!)
        mRecyclerView!!.adapter = mAdapter
        addSearchListener()

        mRecyclerView!!.addOnItemTouchListener(
            RecyclerItemClickListener(context!!, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val item = mAdapter!!.getItem(position)
                    val placeId: String = item.placeId.toString()
                    Log.i("TAG", "Autocomplete item selected: " + item.description)

                    val placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient!!, placeId)
                    placeResult.setResultCallback { places ->
                        if (places.count == 1) {
                            //Do the things here on Click.....
                            mAddress = item.description.toString()
                            sendDataToRestaurantFragment(Activity.RESULT_OK, mAddress!!)
                            /*var fragmentTransaction = fragmentManager!!.beginTransaction()
                            fragmentTransaction.remove(this@SecondSearchLocationFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()*/
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    Log.i("TAG", "Clicked: " + item.description)
                    Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId)
                }
            }
            ))
    }


    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onConnected(p0: Bundle?) {
        Log.v("Google API Callback", "Connection Done")
    }

    override fun onConnectionSuspended(i: Int) {
        Log.v("Google API Callback", "Connection Suspended")
        Log.v("Code", i.toString())
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.v("Google API Callback", "Connection Failed")
        Log.v("Error Code", connectionResult.errorCode.toString())
        Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show()
    }


    private fun addSearchListener() {
        mAddressSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Location Fragment", "Address : " + s.toString())
                if (s.toString() == "" || !mGoogleApiClient!!.isConnected) {
                    if (mGoogleApiClient!!.isConnected) {
                        mAdapter!!.filter.filter(s.toString())
                    }
                } else {
                    mAdapter!!.filter.filter(s.toString())
                }

                if (s.toString() != "" && mGoogleApiClient!!.isConnected) {
                    mAdapter!!.filter.filter(s.toString())
                    mTextCurrentLocation!!.visibility = View.GONE
                    mViewSeparator!!.visibility = View.GONE
                } else if (!mGoogleApiClient!!.isConnected) {
                    Toast.makeText(context, API_NOT_CONNECTED, Toast.LENGTH_SHORT).show()
                    mTextCurrentLocation!!.visibility = View.VISIBLE
                    mViewSeparator!!.visibility = View.VISIBLE
                } else {
                    mTextCurrentLocation!!.visibility = View.VISIBLE
                    mViewSeparator!!.visibility = View.VISIBLE
                }
            }

        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v!!.id) {
            mAddressSearch!!.id -> {
                if (event!!.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= mAddressSearch!!.getRight() - mAddressSearch!!.getTotalPaddingRight()) {
                        mAddressSearch!!.setText("")
                        return true;
                    }
                }
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            mDialogDownArrow!!.id -> {
                dialog.dismiss()
            }
            mTextCurrentLocation!!.id -> {
                sendDataToRestaurantFragment(Activity.RESULT_CANCELED, null)
            }
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        Log.d("0000000000000 : ", "111111111 buildGoogleApiClient")
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .addApi(Places.GEO_DATA_API)
            .build()
    }

    override fun onResume() {
        super.onResume()
        Log.d("0000000000000 : ", "111111111 onResume")
        if (!mGoogleApiClient!!.isConnected && !mGoogleApiClient!!.isConnecting) {
            Log.v("Google API", "Connecting")
            mGoogleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("0000000000000 : ", "111111111 onPause")
        if (mGoogleApiClient!!.isConnected) {
            Log.v("Google API", "Dis-Connecting")
            mGoogleApiClient!!.disconnect()
            //mFusedLocationClient.removeLocationUpdates(mCallback)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("0000000000000 : ", "111111111 onStop")
    }

    override fun onStart() {
        super.onStart()
        Log.d("0000000000000 : ", "111111111 onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("0000000000000 : ", "111111111 onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("0000000000000 : ", "111111111 onDestroyView")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("0000000000000 : ", "111111111 onActivityCreated")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("0000000000000 : ", "111111111 onDetach")
    }

    private fun setDialogLayoutHeight() {
        var height = getScreenDisplayHeight()
        var layoutParams =  mLayoutParent!!.layoutParams
        Log.d("Search", "Screen Height : " + (height / 1.2).toInt() + "-" + height)
        layoutParams.height = (height / 1.2).toInt()
        mLayoutParent!!.layoutParams = layoutParams

    }

    private fun getScreenDisplayHeight() : Int {
        var displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun sendDataToRestaurantFragment(resultCode: Int, address: String?) {
        var intent = Intent()
        //AppConstant.INTENT_KEY_SECOND_FRAGMENT_DATA
        intent.putExtra("456", address);
        var fragment = targetFragment
        fragment!!.onActivityResult(100, resultCode, intent)
    }
}
