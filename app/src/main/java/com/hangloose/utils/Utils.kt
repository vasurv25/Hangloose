package com.hangloose.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.location.Geocoder
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng

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

fun requestPermissionForOtp(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(android.Manifest.permission.RECEIVE_SMS),
        REQUEST_PERMISSIONS
    )
}

fun getDisplaySize(windowManager: WindowManager): Point {
    try {
        if (Build.VERSION.SDK_INT > 16) {
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        } else {
            return Point(0, 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return Point(0, 0)
    }
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun getLocationFromAddress(context: Context, address: String): LatLng? {
    val coder = Geocoder(context)
    val addressList = coder.getFromLocationName(address, 5) ?: return null
    val location = addressList[0]
    location.latitude
    location.longitude
    return LatLng(location.latitude, location.longitude)
}

fun checkSelfPermission(context: Context): Boolean {
    return !(ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED)
}