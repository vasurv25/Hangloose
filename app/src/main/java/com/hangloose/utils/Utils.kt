package com.hangloose.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.hangloose.R
import com.hangloose.utils.PreferenceHelper.get
import com.hangloose.utils.PreferenceHelper.set
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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
        arrayOf(Manifest.permission.RECEIVE_SMS),
        REQUEST_PERMISSIONS
    )
}

fun getDisplaySize(windowManager: WindowManager): Point {
    return try {
        if (Build.VERSION.SDK_INT > 16) {
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        } else {
            Point(0, 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Point(0, 0)
    }
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun getLatLongFromLocationName(activity: Activity, address: String?): Address? {
    var location: Address? = null
    try {
        val geoCoder = Geocoder(activity)
        var list = geoCoder.getFromLocationName(address, 1)
        location = list[0]
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(activity, activity.getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
    }
    return location
}

fun deleteCache(context: Context) {
    try {
        val dir = context.cacheDir
        deleteDir(dir)
    } catch (e: Exception) {
    }
}

fun deleteDir(dir: File?): Boolean {
    if (dir != null && dir.isDirectory) {
        val children = dir.list()
        for (i in children.indices) {
            val success = deleteDir(File(dir, children[i]))
            if (!success) {
                return false
            }
        }
        return dir.delete()
    } else return if (dir != null && dir.isFile) {
        dir.delete()
    } else {
        false
    }
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd", Locale.getDefault()
    )
    val date = Date()
    return dateFormat.format(date)
}

fun isTutorialShown(context: Context, key: String): Boolean {
    val mPreference = PreferenceHelper.defaultPrefs(context)
    return mPreference[key]!!
}

fun setTutorialShown(context: Context, key: String){
    val mPreference = PreferenceHelper.defaultPrefs(context)
    mPreference[key] = true
}