package com.hangloose.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun showSnackBar(view: View, msg: String, color: Int, bgColor: Int) {
    val snackbar = Snackbar.make(
        view, msg,
        Snackbar.LENGTH_LONG
    )
    val snackbarView = snackbar.view
    snackbarView.setBackgroundColor(bgColor)
    val textView =
        snackbarView.findViewById(android.support.design.R.id.snackbar_text) as TextView
    textView.setTextColor(color)
    textView.textSize = 14f
    snackbar.show()
}

fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
}

fun validatePhoneNumber(phoneNumber: String?): Boolean {
    var isValid = !phoneNumber.isNullOrEmpty()
    if (isValid) {
        isValid = phoneNumber!!.length == 10
    }
    return isValid
}

fun validatePassword(password: String?): Boolean {
    var isValid = !password.isNullOrEmpty()
    if (isValid) {
        isValid = password!!.length >= 6
    }
    return isValid
}

fun validateConfirmPassword(password: String?, confirmPassword: String?): Boolean {
    var isValid = !confirmPassword.isNullOrEmpty()
    if (isValid) {
        isValid = password == confirmPassword
    }
    return isValid
}

fun checkAndRequestPermissions(activity: Activity): Boolean {
    val permissionSendMessage = ContextCompat.checkSelfPermission(
        activity,
        Manifest.permission.SEND_SMS
    )
    val receiveSMS = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
    val readSMS = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS)
    val listPermissionsNeeded = ArrayList<String>()
    if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS)
    }
    if (readSMS != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.READ_SMS)
    }
    if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
    }
    if (!listPermissionsNeeded.isEmpty()) {
        ActivityCompat.requestPermissions(
            activity,
            listPermissionsNeeded.toTypedArray(),
            REQUEST_ID_MULTIPLE_PERMISSIONS
        )
        return false
    }
    return true
}