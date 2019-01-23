package com.careapp.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import com.careapp.R
import com.careapp.databinding.ActivityDataLoadingBinding
import com.careapp.services.UpdateDataToDBInBackground
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.Constants
import com.careapp.utils.StaticUtils


class DataLoadingActivity : BaseActivity() {

    private lateinit var binding: ActivityDataLoadingBinding
    private var myReceiver: DataDbReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_loading)
        setState()
    }

    override fun onStart() {
        super.onStart()
        setReceiver()
    }

    private fun setReceiver() {
        myReceiver = DataDbReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.BROADCAST_DATA_SYNC)
        registerReceiver(myReceiver, intentFilter)
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver!!, intentFilter)
    }

    override fun onStop() {
        try {
            if (myReceiver != null) {
                unregisterReceiver(myReceiver)
                LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onStop()
    }

    private fun setState() {
        if (AppLocalStorage.getInstance(this).getBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED))
            navigateToMainActivity()
        else if (!AppLocalStorage.getInstance(this).getBoolean(AppLocalStorage.PREF_IS_DATA_UPDATING)) {
            startDBOperationService()
        }
    }

    fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun startDBOperationService() {
        val intent = Intent(this, UpdateDataToDBInBackground::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else startService(intent)
    }

    inner class DataDbReceiver : BroadcastReceiver() {

        private var context: Context? = null

        @SuppressLint("UnsafeProtectedBroadcastReceiver")
        override fun onReceive(context: Context, intent: Intent) {
            this.context = context
            val dataStatus = intent.getBooleanExtra(Constants.DATA_STATUS, false)
            if (dataStatus) {
                navigateToMainActivity()
            } else {
                StaticUtils.showIndefiniteToast(window.decorView.rootView,
                    "Something went wrong when updating data. Please retry.",
                    getString(R.string.retry),
                    View.OnClickListener {
                        setState()
                    })
            }
        }

    }

}
