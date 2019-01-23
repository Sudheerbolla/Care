package com.careapp.utils

import android.app.Activity
import android.content.Context
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

object RuntimePermissionUtils {

    fun checkPermission(context: Context, permission: String): Int {
        return ContextCompat.checkSelfPermission(context, permission)
    }

    fun requestForPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    fun requestForPermission(activity: Activity, permission: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permission, requestCode)
    }

}
