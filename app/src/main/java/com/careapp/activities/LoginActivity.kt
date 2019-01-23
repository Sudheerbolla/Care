package com.careapp.activities

import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.databinding.ActivityLoginBinding
import com.careapp.interfaces.IParserListener
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException

class LoginActivity : BaseActivity(), View.OnClickListener, IParserListener<JsonObject> {

    private lateinit var binding: ActivityLoginBinding
    private var dialog: Dialog? = null

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initComponents()
    }

    private fun initComponents() {
        dialog = PopUtils.SimpleProgressDialog(this)
        setListeners()
    }

    private fun setListeners() {
        binding.txtLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtLogin -> {
                val message = checkForValidations()
                if (TextUtils.isEmpty(message)) {
                    requestForLoginWS()
                }
            }
        }
    }

    private fun requestForLoginWS() {
        try {
            showProgress()
            val call =
                BaseApplication.wsInterface!!.requestForLogin(
                    binding.edtUserName.text.toString().trim(),
                    binding.edtPassword.text.toString().trim()
                )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_LOGIN, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun navigateToDataUpdatingActivity() {
        startActivity(Intent(this, DataLoadingActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun checkForValidations(): String {
        val email: String = binding.edtUserName.text.toString().trim()
        val password: String = binding.edtPassword.text.toString().trim()

        when {
            TextUtils.isEmpty(email) -> {
                binding.edtUserName.requestFocus()
                return getString(R.string.please_enter_username)
            }
            TextUtils.isEmpty(password) /*|| password.length < 6*/ -> {
                binding.edtPassword.requestFocus()
                return getString(R.string.please_enter_password)
            }
            else -> return ""
        }
    }

    override fun successResponse(requestCode: Int, response: JsonElement?) {
        Log.e("success: ", response.toString())
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_LOGIN -> {
                parseLoginResponse(response as JsonObject)
            }
        }
    }

    private fun parseLoginResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("token")) {
                AppLocalStorage.getInstance(this)
                    .setString(AppLocalStorage.PREF_USER_TOKEN, jsonObject.get("token").asString)
                AppLocalStorage.getInstance(this)
                    .setString(AppLocalStorage.PREF_USER_NAME, jsonObject.get("name").asString)
                navigateToDataUpdatingActivity()
            } else if (jsonObject.has("error")) {
                if (jsonObject.get("error").asBoolean) {
                    StaticUtils.showIndefiniteToast(
                        window.decorView.rootView,
                        jsonObject.get("message").asString,
                        getString(R.string.ok),
                        View.OnClickListener { })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        Log.e("error: ", error)
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_LOGIN -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForLoginWS() })
            }
        }
    }

    override fun noInternetConnection(requestCode: Int) {
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_LOGIN -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForLoginWS() })
            }
        }
    }

}
