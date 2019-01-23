package com.careapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.databinding.ActivityMainBinding
import com.careapp.dbutils.DbHelper
import com.careapp.fragments.AnganwadiCentersListFragment
import com.careapp.interfaces.IParserListener
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException

class MainActivity : BaseActivity(), IParserListener<JsonObject> {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        replaceHomeFragment()
        binding.txtLogout.setOnClickListener {
            PopUtils.simpleDialog(
                this,
                "Are you sure you want to Logout?",
                "Logout",
                DialogInterface.OnClickListener { dialog, which -> requestForLogoutWS() })
        }
    }

    private fun requestForLogoutWS() {
        try {
            val call =
                BaseApplication.wsInterface!!.requestForLogout(
                    AppLocalStorage.getInstance(this).getString(
                        AppLocalStorage.PREF_USER_TOKEN
                    )
                )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_LOGOUT, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    public fun setHeading(heading: String) {
        binding.txtHeading.text = heading
    }

    private fun replaceHomeFragment() {
        replaceFragment(AnganwadiCentersListFragment(), false)
    }

    override fun successResponse(requestCode: Int, response: JsonElement?) {
        when (requestCode) {
            WSUtils.REQ_FOR_LOGOUT -> {
                AppLocalStorage.getInstance(this).clear()
                DbHelper(this).deleteAll()
                finishAffinity()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        when (requestCode) {
            WSUtils.REQ_FOR_LOGOUT -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForLogoutWS() })
            }
        }
    }

    override fun noInternetConnection(requestCode: Int) {
        when (requestCode) {
            WSUtils.REQ_FOR_LOGOUT -> {

                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForLogoutWS() })
            }
        }
    }

}
