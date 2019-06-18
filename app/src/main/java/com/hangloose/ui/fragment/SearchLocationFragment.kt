package com.hangloose.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.hangloose.R
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.hangloose.ui.adapter.PlacesAutoCompleteAdapter
import com.hangloose.ui.adapter.RecyclerItemClickListener
import com.hangloose.utils.API_NOT_CONNECTED
import kotlinx.android.synthetic.main.content_bottom_sheet.*
import kotlinx.android.synthetic.main.content_location_setting.*
import kotlinx.android.synthetic.main.location_search_adapter.*

class SearchLocationFragment : BottomSheetDialogFragment(), GoogleApiClient.ConnectionCallbacks,
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

    private lateinit var mCallback: ContentListener

    private var mAddress: String? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as ContentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement ContentListener"
            )
        }
    }

    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        val rootView = View.inflate(context, R.layout.fragment_bottom_sheet, null)
        dialog!!.setContentView(rootView)
        mAddressSearch = rootView.findViewById(R.id.etLocationSearch)
        mRecyclerView = rootView.findViewById(R.id.rvAddresses)
        mTextCurrentLocation = rootView.findViewById(R.id.tvCurrentLocation)
        mViewSeparator = rootView.findViewById(R.id.firstSeperator)
        mLayoutParent = rootView.findViewById(R.id.layoutParent)
        mDialogDownArrow = rootView.findViewById(R.id.ivArrowDown)

        mAddressSearch!!.setOnTouchListener(this)
        mDialogDownArrow!!.setOnClickListener(this)

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
                            mCallback.onItemClicked(item.description.toString())
                            Toast.makeText(context, item.description, Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
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
        Log.v("Error Code", connectionResult.getErrorCode().toString())
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
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .addApi(Places.GEO_DATA_API)
            .build()
    }

    override fun onResume() {
        super.onResume()
        if (!mGoogleApiClient!!.isConnected && !mGoogleApiClient!!.isConnecting) {
            Log.v("Google API", "Connecting")
            mGoogleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient!!.isConnected) {
            Log.v("Google API", "Dis-Connecting")
            mGoogleApiClient!!.disconnect()
        }
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

    // Container Activity must implement this interface
    interface ContentListener {
        fun onItemClicked(location: String?)
    }
}