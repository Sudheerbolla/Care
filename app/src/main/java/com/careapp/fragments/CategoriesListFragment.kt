package com.careapp.fragments

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
import com.careapp.activities.MainActivity
import com.careapp.activities.QuestionsActivity
import com.careapp.adapters.CategoriesListAdapter
import com.careapp.databinding.FragmentCategoriesListBinding
import com.careapp.dbutils.DbHelper
import com.careapp.interfaces.IClickListener
import com.careapp.interfaces.IParserListener
import com.careapp.models.CategoriesModel
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException
import java.util.*

class CategoriesListFragment : BaseFragment(), IClickListener, View.OnClickListener, IParserListener<JsonObject> {

    companion object {
        fun newInstance(anganwadiId: String): CategoriesListFragment {
            val categoriesListFragment = CategoriesListFragment()
            val bundle = Bundle()
            bundle.putString("anganwadiId", anganwadiId)
            categoriesListFragment.arguments = bundle
            return categoriesListFragment
        }
    }

    private var rootView: View? = null
    private lateinit var binding: FragmentCategoriesListBinding
    private var categoriesListArrayList: ArrayList<CategoriesModel>? = null
    private lateinit var mainActivity: MainActivity
    private var dialog: Dialog? = null
    private var categoriesListAdapter: CategoriesListAdapter? = null
    private var anganwadiId: String? = ""

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.setHeading("Categories centers")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        dialog = PopUtils.SimpleProgressDialog(mainActivity)
        arguments.let {
            anganwadiId = it?.getString("anganwadiId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories_list, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    private fun initComponents() {
        categoriesListArrayList = ArrayList()
        categoriesListArrayList?.addAll(DbHelper(mainActivity).getCategories)
        setCategoriesListAdapter()
//        requestForGetCategoriesListWS()
    }

    private fun requestForGetCategoriesListWS() {
        try {
            showProgress()
            val call =
                BaseApplication.wsInterface!!.getCategories(
                    AppLocalStorage.getInstance(mainActivity).getString(AppLocalStorage.PREF_USER_TOKEN)
                )
            WSCallBacksListener.requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_CATEGORIES, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun setCategoriesListAdapter() {
        categoriesListAdapter = CategoriesListAdapter(categoriesListArrayList!!, this)
        binding.recyclerView.adapter = categoriesListAdapter
    }

    override fun onClick(view: View, position: Int) {
        val selectedModel = categoriesListArrayList?.get(position)
//        mainActivity.replaceFragment(QuestionsListFragment(), true)
        val intent = Intent(mainActivity, QuestionsActivity::class.java)
        intent.putExtra("categoryId", selectedModel?.id)
        intent.putExtra("anganwadiId", anganwadiId)
        startActivity(intent)
        mainActivity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
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
            WSUtils.REQ_FOR_GET_CATEGORIES -> {
                parseGetCategoriesResponse(response as JsonObject)
            }
        }
    }

    private fun parseGetCategoriesResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("categories")) {
                categoriesListArrayList!!.clear()
                val centersList = jsonObject.get("categories").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        categoriesListArrayList!!.add(CategoriesModel(jsonObjects!! as JsonObject))
                    }
                }
//                Collections.sort(categoriesListArrayList,
//                    { o1, o2 -> o1.rank!!.compareTo(o2.rank!!) })

//                categoriesListArrayList =
//                        categoriesListArrayList!!.sortedWith(compareBy { it.rank }) as ArrayList<CategoriesModel>
                categoriesListArrayList!!.sortedWith(compareBy { it.rank })
                categoriesListAdapter!!.notifyDataSetChanged()
            } else StaticUtils.showIndefiniteToast(
                rootView!!,
                getString(R.string.something_went_wrong),
                getString(R.string.retry),
                View.OnClickListener { requestForGetCategoriesListWS() })
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
                    View.OnClickListener { requestForGetCategoriesListWS() })
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
                    View.OnClickListener { requestForGetCategoriesListWS() })
            }
        }
    }

}
