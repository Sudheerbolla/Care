package com.careapp.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.view.View
import com.careapp.BuildConfig
import com.careapp.R
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.Constants
import com.careapp.utils.RuntimePermissionUtils
import com.careapp.utils.StaticUtils

class SplashActivity : BaseActivity() {

    private lateinit var locationManager: LocationManager

    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    override fun onResume() {
        super.onResume()
        initComponents()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    private fun initComponents() {
//        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED, false)
        if (isLocationEnabled) {
            if (RuntimePermissionUtils.checkPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Handler().postDelayed({
                    if (TextUtils.isEmpty(AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)))
                        navigateToLoginActivity()
                    else if (!AppLocalStorage.getInstance(this).getBoolean(
                            AppLocalStorage.PREF_IS_DATA_UPDATED,
                            false
                        )
                    ) {
                        navigateToDataUpdatingActivity()
                    } else navigateToMainActivity()
                }, Constants.DELAY_TIME)
            } else {
                requestPermissions()
            }
        } else {
            showAlert()
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            StaticUtils.showIndefiniteToast(window.decorView.rootView,
                "Location permission is manditory. Please allow it.",
                "Allow",
                View.OnClickListener {
                    RuntimePermissionUtils.requestForPermission(
                        this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        Constants.LOCATION_REQUEST_CODE
                    )
                })
        } else {
            StaticUtils.showIndefiniteToast(window.decorView.rootView,
                "Location permission is manditory to use the application.",
                "Allow",
                View.OnClickListener {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                })
//            RuntimePermissionUtils.requestForPermission(
//                this,
//                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                Constants.LOCATION_REQUEST_CODE
//            )
        }
    }

    private fun navigateToDataUpdatingActivity() {
        startActivity(Intent(this, DataLoadingActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            if (StaticUtils.isAllPermissionsGranted(grantResults)) {
                initComponents()
            } else {
                StaticUtils.showIndefiniteToast(window.decorView.rootView,
                    "Location permission is manditory to use the application.",
                    "Allow",
                    View.OnClickListener {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    private fun getLastLocation() {

    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Location Settings is set to 'Off'.\nPlease Enable Location to use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
        dialog.show()
    }

}