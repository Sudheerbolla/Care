package com.careapp.activities

import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.careapp.R
import com.careapp.utils.FragmentUtils

open class BaseActivity : AppCompatActivity() {

    fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun changeStatusBarColorToWhite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
        }
    }

    fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
        }
    }

    fun replaceFragment(fragment: Fragment, needToAddToBackStack: Boolean) {
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        setCustomAnimation(fragmentTransaction, false)
        if (needToAddToBackStack)
            fragmentTransaction.replace(R.id.mainFrame, fragment, tag).addToBackStack(tag).commit()
        else
            fragmentTransaction.replace(R.id.mainFrame, fragment, tag).commit()
    }

    fun addFragment(fragment: Fragment, needToAddToBackStack: Boolean) {
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        setCustomAnimation(fragmentTransaction, false)
        if (needToAddToBackStack) fragmentTransaction.add(R.id.mainFrame, fragment, tag).addToBackStack(tag).commit()
        else fragmentTransaction.add(R.id.mainFrame, fragment, tag).commit()
    }

    fun replaceFragmentChildSpManager(fragment: Fragment, needToAddToBackStack: Boolean) {
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        setCustomAnimation(fragmentTransaction, false)
        if (needToAddToBackStack) fragmentTransaction.replace(
            R.id.mainFrame,
            fragment,
            tag
        ).addToBackStack(tag).commit()
        else fragmentTransaction.replace(R.id.mainFrame, fragment, tag).commit()
    }

    fun replaceFragment(fragment: Fragment) {
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        setCustomAnimation(fragmentTransaction, false)
        fragmentTransaction.replace(R.id.mainFrame, fragment, tag).addToBackStack(tag).commitAllowingStateLoss()
    }

    fun replaceFragmentWithoutAnimation(fragment: Fragment, needToAddToBackStack: Boolean) {
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (needToAddToBackStack) fragmentTransaction.replace(
            R.id.mainFrame,
            fragment,
            tag
        ).addToBackStack(tag).commitAllowingStateLoss()
        else fragmentTransaction.replace(R.id.mainFrame, fragment, tag).commitAllowingStateLoss()
    }

    fun addFragmentWithoutAnimation(fragment: Fragment, needToAddToBackStack: Boolean) {
        FragmentUtils.sDisableFragmentAnimations = true
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (needToAddToBackStack) fragmentTransaction.add(
            R.id.mainFrame,
            fragment,
            tag
        ).addToBackStack(tag).commitAllowingStateLoss()
        else fragmentTransaction.add(R.id.mainFrame, fragment, tag).commitAllowingStateLoss()
        FragmentUtils.sDisableFragmentAnimations = false
    }

    fun clearBackStack() {
        try {
            FragmentUtils.sDisableFragmentAnimations = true
            val fragment = supportFragmentManager
//        if (fragment.executePendingTransactions())
            if (fragment.backStackEntryCount > 0)
                fragment.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            FragmentUtils.sDisableFragmentAnimations = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun popBackStack() {
        FragmentUtils.sDisableFragmentAnimations = true
        val fragment = supportFragmentManager
        fragment.popBackStackImmediate()
        FragmentUtils.sDisableFragmentAnimations = false
    }

    fun restartFragment(fragment: Fragment) {
//        val frg: Fragment = supportFragmentManager.findFragmentByTag(MapLocationsFragment::class.simpleName)
//        val ft = supportFragmentManager.beginTransaction()
//        ft.detach(frg)
//        ft.attach(frg)
//        ft.commit()
    }

    private fun setCustomAnimation(ft: FragmentTransaction, reverseAnimation: Boolean) {
        if (!reverseAnimation) {
            ft.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        } else {
            ft.setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            )
        }
    }

}