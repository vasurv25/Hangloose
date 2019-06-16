package com.hangloose.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.design.widget.BottomSheetDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.hangloose.R
import android.content.Intent
import android.location.Geocoder
import com.hangloose.ui.service.AddressesByNameIntentService
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.util.Log
import kotlinx.android.synthetic.main.content_bottom_sheet.*
import android.view.ViewTreeObserver

class SearchLocationFragment : BottomSheetDialogFragment() {

    private var mAddressResultReceiver : AddressListResultReceiver? = null
    private var mAddressListView : ListView? = null
    private var mAddressSearch : EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        val rootView = View.inflate(context,R.layout.fragment_bottom_sheet, null)
        dialog!!.setContentView(rootView)
        mAddressListView = rootView.findViewById(R.id.lvAddresses)
        mAddressSearch = rootView.findViewById(R.id.etLocationSearch)
        addSearchListener()


        //Set the coordinator layout behavior
        val params = (rootView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)

            rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val height = rootView.measuredHeight
                    behavior.peekHeight = height
                }
            })
        }
        mAddressResultReceiver = AddressListResultReceiver(Handler())
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

    private fun addSearchListener() {
        mAddressSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Location Fragment", "Address : " + s.toString())
                getAddresses(s.toString())
            }

        })
    }

    private fun getAddresses(addName: String) {
        /*if (!Geocoder.isPresent()) {
            Toast.makeText(
                context,
                "Can't find address, ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }*/
        val intent = Intent(context, AddressesByNameIntentService::class.java)
        intent.putExtra("address_receiver", mAddressResultReceiver)
        intent.putExtra("address_name", addName)
        context!!.startService(intent)
    }

    private inner class AddressListResultReceiver internal constructor(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            if (resultCode == 0) {
                Toast.makeText(
                    context,
                    "Enter address name, ",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (resultCode == 1) {
                Toast.makeText(
                    context,
                    "Address not found, ",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val addressList = resultData.getStringArray("addressList")
            showResults(addressList)
        }
    }

    private fun showResults(addressList: Array<String>?) {
        val arrayAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1, addressList!!
        )
        mAddressListView!!.adapter = arrayAdapter
    }
}