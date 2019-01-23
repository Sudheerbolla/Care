package com.careapp.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

open class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun checkPermission(context: Context, permission: String): Int {
        return ContextCompat.checkSelfPermission(context, permission)
    }

    fun requestForPermission(permission: String, requestCode: Int) {
        requestPermissions(arrayOf(permission), requestCode)
    }

    fun requestForPermission(permission: Array<String>, requestCode: Int) {
        requestPermissions(permission, requestCode)
    }

//    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
//        if (FragmentUtils.sDisableFragmentAnimations) {
//            val a = object : Animation() {}
//            a.duration = 0
//            return a
//        }
//        return super.onCreateAnimation(transit, enter, nextAnim)
//    }

}
