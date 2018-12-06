package com.hangloose.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.telephony.SmsMessage
import android.util.Log
import java.util.regex.Pattern

class SMSBroadcastReceiver : BroadcastReceiver() {

    private val TAG: String = "SMSBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent!!.extras
        try {

            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<Any>

                for (i in pdusObj.indices) {

                    val currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val phoneNumber = currentMessage.getDisplayOriginatingAddress()

                    val pattern = Pattern.compile("(\\d{4})")

                    val isMatches = pattern.matcher(currentMessage.displayMessageBody)
                    if (isMatches.find()) {
                        Log.i(TAG, "senderNum: $phoneNumber; message: $isMatches")
                        val strOtp = isMatches.group(1)
                        val myIntent = Intent("otp")
                        myIntent.putExtra("message", strOtp)
                        LocalBroadcastManager.getInstance(context!!).sendBroadcast(myIntent)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception smsReceiver$e")
        }
    }
}