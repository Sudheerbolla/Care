package com.careapp.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.databinding.ActivityAddAnganwadiCenterBinding
import com.careapp.interfaces.IParserListener
import com.careapp.models.AnganwadiModel
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException

class AddAnganwadiCenterActivity : BaseActivity(), View.OnClickListener, IParserListener<JsonObject>,
    AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.spinnerDistrict -> {
                requestForGetBlocksWS()
            }
            R.id.spinnerBlock -> {
                requestForGetAWCsWS()
            }
            R.id.spinnerAWC -> {

            }
        }
    }

    private lateinit var binding: ActivityAddAnganwadiCenterBinding
    private var dialog: Dialog? = null

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_anganwadi_center)
        initComponents()
    }

    private fun initComponents() {
        dialog = PopUtils.SimpleProgressDialog(this)
        setListeners()
        requestForGetDistrictsWS()
    }

    private fun requestForGetDistrictsWS() {
        try {
            showProgress()
            val call =
                BaseApplication.wsInterface!!.getDistricts(
                    AppLocalStorage.getInstance(this).getString(
                        AppLocalStorage.PREF_USER_TOKEN
                    )
                )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_GET_DISTRICTS, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun requestForGetBlocksWS() {
        try {
            showProgress()
            val call =
                BaseApplication.wsInterface!!.getBlocks(
                    binding.spinnerDistrict.selectedItem.toString().trim(),
                    AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)
                )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_GET_BLOCKS, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun requestForGetAWCsWS() {
        try {
            showProgress()
            val call =
                BaseApplication.wsInterface!!.getListOfAWCs(
                    binding.spinnerBlock.selectedItem.toString().trim(),
                    AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)
                )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_GET_AWCS, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun requestForAddAnganwadiCenterWS() {
        try {
            showProgress()
            val angId = binding.spinnerAWC.selectedItem as AnganwadiModel
            val call =
                BaseApplication.wsInterface!!.addAWC(
                    angId.anganwadiId!!,
                    AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)
                )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_ADD_AWCS, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun setListeners() {
        binding.txtAddAnganwadiCenter.setOnClickListener(this)
        binding.spinnerDistrict.onItemSelectedListener = this
        binding.spinnerBlock.onItemSelectedListener = this
        binding.spinnerAWC.onItemSelectedListener = this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtAddAnganwadiCenter -> {
                val message = checkForValidations()
                if (TextUtils.isEmpty(message)) {
                    requestForAddAnganwadiCenterWS()
                }
            }
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
        val district: String = binding.spinnerDistrict.selectedItem.toString().trim()
        val block: String = binding.spinnerBlock.selectedItem.toString().trim()
        val awc: String = binding.spinnerAWC.selectedItem.toString().trim()

        when {
            TextUtils.isEmpty(district) -> {
                return "Please select District."
            }
            TextUtils.isEmpty(block) -> {
                return "Please select Block."
            }
            TextUtils.isEmpty(awc) -> {
                return "Please select AWC."
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
            WSUtils.REQ_FOR_GET_DISTRICTS -> {
                parseGetDistrictsResponse(response as JsonObject)
            }
            WSUtils.REQ_FOR_GET_BLOCKS -> {
                parseGetBlocksResponse(response as JsonObject)
            }
            WSUtils.REQ_FOR_GET_AWCS -> {
                parseGetAWCsResponse(response as JsonObject)
            }
            WSUtils.REQ_FOR_ADD_AWCS -> {
                parseAddAnganwadiCenterResponse(response as JsonObject)
            }
        }
    }
/*
    {"message":"Center is added to your list"}
    http://ec2-13-233-65-185.ap-south-1.compute.amazonaws.com:3000/api/v1/centers/5c3b83becee5a33068ffe2c7/add.json?token=96a056be-d6e7-42d8-b79e-a9f928b7c97c
*/

    private fun parseAddAnganwadiCenterResponse(jsonObject: JsonObject) {
        try {
            setResult(Activity.RESULT_OK, intent.putExtra("reload", true))
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseGetDistrictsResponse(jsonObject: JsonObject) {
        try {
            val districts = jsonObject.get("districts").asJsonArray
            val distArray = Array(districts.size()) {
                districts.get(it).asString.replace("\"", "")
            }
            binding.spinnerDistrict.adapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distArray)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseGetBlocksResponse(jsonObject: JsonObject) {
        try {
            val blocks = jsonObject.get("blocks").asJsonArray
            val blocksArray = Array(blocks.size()) {
                blocks.get(it).toString().replace("\"", "")
            }
            binding.spinnerBlock.adapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, blocksArray)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseGetAWCsResponse(jsonObject: JsonObject) {
        try {
            val centers = jsonObject.get("centers").asJsonArray
//            val distArray = Array(centers.size()) {
//                centers.get(it).asJsonObject.get("name").asString.replace("\"", "")
//            }
//            val distArray = Array(centers.size()) {
//                AnganwadiModel(centers.get(it).asJsonObject).toString().replace("\"", "")
//            }
            val distArray = Array(centers.size()) {
                AnganwadiModel(centers.get(it).asJsonObject)
            }
            binding.spinnerAWC.adapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distArray)
            binding.spinnerAWC.setSelection(0)
        } catch (e: Exception) {
            e.printStackTrace()
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
                    View.OnClickListener { requestForAddAnganwadiCenterWS() })
            }
            WSUtils.REQ_FOR_GET_DISTRICTS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetDistrictsWS() })
            }
            WSUtils.REQ_FOR_GET_BLOCKS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetBlocksWS() })
            }
            WSUtils.REQ_FOR_GET_AWCS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetAWCsWS() })
            }
            WSUtils.REQ_FOR_ADD_AWCS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForAddAnganwadiCenterWS() })
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
                    View.OnClickListener { requestForAddAnganwadiCenterWS() })
            }
            WSUtils.REQ_FOR_GET_DISTRICTS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetDistrictsWS() })
            }
            WSUtils.REQ_FOR_GET_BLOCKS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetBlocksWS() })
            }
            WSUtils.REQ_FOR_GET_AWCS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetAWCsWS() })
            }
            WSUtils.REQ_FOR_ADD_AWCS -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForAddAnganwadiCenterWS() })
            }
        }
    }

}

/*{
  "centers": [
    {
      "id": "5c3b83becee5a33068ffe2c2",
      "name": "ansaru hak ke makan me",
      "selectedHouseholdCount": 0,
      "completedHouseholdCount": 0
    },
    {
      "id": "5c3b83becee5a33068ffe2c3",
      "name": "Milo ray ke makaan me",
      "selectedHouseholdCount": 0,
      "completedHouseholdCount": 0
    }
    ]
    }*/


/*{"districts":["EAST_CHAMPARAN"]}*/


/*{"blocks":["ADAPUR","ARERAJ","BANJARIYA","PHENHARA","TETARIA","TURKAULIYA"]}*/