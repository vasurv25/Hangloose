package com.hangloose.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log

class SMSBroadcastReceiver : BroadcastReceiver() {

    private val TAG: String = "SMSBroadcastReceiver"
    val sms = SmsManager.getDefault()

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent!!.getExtras()

        try {

            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<Any>

                for (i in pdusObj.indices) {

                    val currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val phoneNumber = currentMessage.getDisplayOriginatingAddress()

                    var message = currentMessage.getDisplayMessageBody().split(":")[1]

                    message = message.substring(0, message.length - 1)
                    Log.i(TAG, "senderNum: $phoneNumber; message: $message")

                    val myIntent = Intent("otp")
                    myIntent.putExtra("message", message)
                    LocalBroadcastManager.getInstance(context!!).sendBroadcast(myIntent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception smsReceiver$e")
        }
    }
}