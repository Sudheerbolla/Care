package com.careapp.activities

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.adapters.HomesListAdapter
import com.careapp.databinding.ActivityHomesListBinding
import com.careapp.interfaces.IClickListener
import com.careapp.interfaces.IParserListener
import com.careapp.models.HomesModel
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException

class HomesListActivity : BaseActivity(), IClickListener, View.OnClickListener, IParserListener<JsonObject> {

    private var rootView: View? = null
    private lateinit var binding: ActivityHomesListBinding
    private var homesArrayList: ArrayList<HomesModel>? = null
    private var dialog: Dialog? = null
    private var homesListAdapter: HomesListAdapter? = null

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_homes_list)
        rootView = binding.root
        dialog = PopUtils.SimpleProgressDialog(this)
        initComponents()
    }

    private fun initComponents() {
        homesArrayList = ArrayList()
        setQuestionsListAdapter()
        requestForGetHomesListWS()
    }

    private fun requestForGetHomesListWS() {
        try {
            showProgress()
            val call = BaseApplication.wsInterface!!.getQuestionsList(
                AppLocalStorage.getInstance(this).getString(
                    AppLocalStorage.PREF_USER_TOKEN
                ), "0", "20"
            )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_GET_HOMES, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun setQuestionsListAdapter() {
        homesListAdapter = HomesListAdapter(homesArrayList!!, this)
        binding.recyclerView.adapter = homesListAdapter
    }

    override fun onClick(view: View, position: Int) {
        val selectedModel = homesArrayList?.get(position)
    }

    override fun onLongClick(view: View, position: Int) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    override fun successResponse(requestCode: Int, response: JsonElement?) {
        Log.e("success: ", response.toString())
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_HOMES -> {
                parseGetHomesListResponse(response as JsonObject)
            }
        }
    }

    private fun parseGetHomesListResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("questions")) {
                val centersList = jsonObject.get("questions").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        homesArrayList!!.add(HomesModel(jsonObjects!! as JsonObject))
                    }
                }
                homesListAdapter!!.notifyDataSetChanged()
            } else StaticUtils.showIndefiniteToast(
                rootView!!,
                getString(R.string.something_went_wrong),
                getString(R.string.retry),
                View.OnClickListener { requestForGetHomesListWS() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        Log.e("error: ", error)
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_HOMES -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetHomesListWS() })
            }
        }
    }

    override fun noInternetConnection(requestCode: Int) {
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_HOMES -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetHomesListWS() })
            }
        }
    }

}
