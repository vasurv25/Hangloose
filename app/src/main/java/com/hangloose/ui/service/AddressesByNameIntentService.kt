package com.hangloose.ui.service

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import android.util.Log
import java.util.Locale.getDefault

class AddressesByNameIntentService : IntentService("Address Service") {

    private val TAG = "AddressesIntentService"
    private var mAddressResultReceiver : ResultReceiver? = null
    private var mLocation : Location? = null

    override fun onHandleIntent(intent: Intent?) {
        var msg = ""
        mAddressResultReceiver = intent!!.getParcelableExtra("address_receiver")

        if (mAddressResultReceiver == null) {
            Log.e(
                TAG,
                "No receiver in intent"
            )
            return
        }

        val addressName = intent.getStringExtra("address_name")

        if (addressName == null) {
            msg = "No name found"
            sendResultsToReceiver(0, msg, null)
            return
        }
        mLocation = intent.getParcelableExtra(
            addressName)
        val geoCoder = Geocoder(this, getDefault())
        var addresses: List<Address>? = null

        try {
            addresses = geoCoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 10)
            //addresses = geocoder.getFromLocationName(addressName, 10)
//            for (i in 0..addresses!!.size) {
//                Log.d(TAG,"Addressssssssss : " + addresses.)
//            }
        } catch (ioException: Exception) {
            Log.e("", "Error in getting addresses for the given name")
        }


        if (addresses == null || addresses.isEmpty()) {
            msg = "No address found for the address name"
            sendResultsToReceiver(1, msg, null)
        } else {
            Log.d(TAG, "number of addresses received " + addresses.size)
            val addressList : Array<String> = arrayOf(addresses.size.toString())
            for ((j, address) in addresses.withIndex()) {
                val addressInfo = ArrayList<String>()
                for (i in 0..address.maxAddressLineIndex) {
                    addressInfo.add(address.getAddressLine(i))
                }
                addressList[j] = TextUtils.join(
                    System.getProperty("line.separator"),
                    addressInfo
                )
                Log.d(TAG, "Address" + addressList[j])
            }
            sendResultsToReceiver(2, "", addressList)
        }
    }
    private fun sendResultsToReceiver(resultCode: Int, message: String, addressList: Array<String>?) {
        val bundle = Bundle()
        bundle.putString("msg", message)
        bundle.putStringArray("addressList", addressList)
        mAddressResultReceiver!!.send(resultCode, bundle)
    }
}