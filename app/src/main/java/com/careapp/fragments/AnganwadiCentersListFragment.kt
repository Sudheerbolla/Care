package com.careapp.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.activities.AddAnganwadiCenterActivity
import com.careapp.activities.MainActivity
import com.careapp.adapters.AnganwadiListAdapter
import com.careapp.databinding.FragmentAnganwadiCentersListBinding
import com.careapp.dbutils.DbHelper
import com.careapp.interfaces.IClickListener
import com.careapp.interfaces.IParserListener
import com.careapp.models.AnganwadiModel
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.Constants
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException

class AnganwadiCentersListFragment : BaseFragment(), IClickListener, View.OnClickListener, IParserListener<JsonObject> {

    private var rootView: View? = null
    private lateinit var binding: FragmentAnganwadiCentersListBinding
    private var anganwadisArrayList: ArrayList<AnganwadiModel>? = null
    private lateinit var mainActivity: MainActivity
    private var dialog: Dialog? = null
    private var anganwadiListAdapter: AnganwadiListAdapter? = null

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.setHeading("Anganwadi centers")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        dialog = PopUtils.SimpleProgressDialog(mainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_anganwadi_centers_list, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CREATE_ANGANWADI && resultCode == Activity.RESULT_OK && data!!.hasExtra("reload") && data.getBooleanExtra(
                "reload",
                false
            )
        ) {
            requestForGetAnganwadisListWS()
        }
    }

    private fun initComponents() {
        anganwadisArrayList = ArrayList()
        anganwadisArrayList?.addAll(DbHelper(mainActivity).getAnganwadiCenters)
        setAnganwadisListAdapter()
        binding.txtAddAnganwadiCenter.setOnClickListener(this)
    }

    private fun requestForGetAnganwadisListWS() {
        try {
            showProgress()
            val call =
                BaseApplication.wsInterface!!.getAnganwadiCenters(
                    AppLocalStorage.getInstance(mainActivity).getString(
                        AppLocalStorage.PREF_USER_TOKEN
                    )
                )
            WSCallBacksListener.requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_ANGANWADI_CENTERS, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun setAnganwadisListAdapter() {
        anganwadiListAdapter = AnganwadiListAdapter(mainActivity, anganwadisArrayList!!, this)
        binding.recyclerView.adapter = anganwadiListAdapter
    }

    override fun onClick(view: View, position: Int) {
        val selectedModel = anganwadisArrayList?.get(position)
        mainActivity.replaceFragment(CategoriesListFragment.newInstance(selectedModel?.anganwadiId!!), true)
    }

    override fun onLongClick(view: View, position: Int) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtAddAnganwadiCenter -> {
                navigateToAddNewAnganwadiCenterScreen()
            }
        }
    }

    private fun navigateToAddNewAnganwadiCenterScreen() {
        startActivityForResult(Intent(mainActivity, AddAnganwadiCenterActivity::class.java), Constants.CREATE_ANGANWADI)
        mainActivity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    override fun successResponse(requestCode: Int, response: JsonElement?) {
        Log.e("success: ", response.toString())
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_ANGANWADI_CENTERS -> {
                parseGetAnganwadiCentersResponse(response as JsonObject)
            }
        }
    }

    private fun parseGetAnganwadiCentersResponse(jsonObject: JsonObject) {
        try {
            val dbHelper = DbHelper(mainActivity)
            if (jsonObject.has("centers")) {
                anganwadisArrayList!!.clear()
                val centersList = jsonObject.get("centers").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        val ang = AnganwadiModel(jsonObjects!! as JsonObject)
                        anganwadisArrayList!!.add(ang)
                        dbHelper.addAnganwadiCenter(ang)
                    }
                }
                anganwadiListAdapter!!.notifyDataSetChanged()
            } else StaticUtils.showIndefiniteToast(
                rootView!!,
                getString(R.string.something_went_wrong),
                getString(R.string.retry),
                View.OnClickListener { requestForGetAnganwadisListWS() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        Log.e("error: ", error)
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_ANGANWADI_CENTERS -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetAnganwadisListWS() })
            }
        }
    }

    override fun noInternetConnection(requestCode: Int) {
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_ANGANWADI_CENTERS -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetAnganwadisListWS() })
            }
        }
    }

}
