package com.careapp.fragments

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.activities.MainActivity
import com.careapp.adapters.QuestionsExpandableAdapter
import com.careapp.databinding.FragmentQuestionsListBinding
import com.careapp.interfaces.IClickListener
import com.careapp.interfaces.IParserListener
import com.careapp.models.QuestionsModel
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException

class QuestionsListFragment : BaseFragment(), IClickListener, View.OnClickListener, IParserListener<JsonObject> {

    private var rootView: View? = null
    private lateinit var binding: FragmentQuestionsListBinding
    private var questionsArrayList: ArrayList<QuestionsModel>? = null
    private lateinit var mainActivity: MainActivity
    private var dialog: Dialog? = null
    private var questionsListAdapter: QuestionsExpandableAdapter? = null
    private var offset = 0

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        dialog = PopUtils.SimpleProgressDialog(mainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_questions_list, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    private fun initComponents() {
        questionsArrayList = ArrayList()
        setQuestionsListAdapter()
        requestForGetQuestionsListWS()
    }

    private fun requestForGetQuestionsListWS() {
        try {
            showProgress()
            val call =
                BaseApplication.wsInterface!!.getQuestionsList(
                    AppLocalStorage.getInstance(mainActivity).getString(
                        AppLocalStorage.PREF_USER_TOKEN
                    ), offset.toString(), "20"
                )
            WSCallBacksListener.requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_QUESTIONS, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            hideProgress()
        }
    }

    private fun setQuestionsListAdapter() {
        questionsListAdapter = QuestionsExpandableAdapter(mainActivity, questionsArrayList!!)
        binding.expandableListView.setAdapter(questionsListAdapter)

//        StaticUtils.setExpandableListViewHeight(binding.expandableListView, -1)

        binding.expandableListView.setOnGroupClickListener({ parent, v, position, id ->
            //            StaticUtils.setExpandableListViewHeight(parent, position)
            false
        })

        binding.expandableListView.setOnChildClickListener({ parent, v, groupPosition, childPosition, id ->
            val groupItem = questionsArrayList!!.get(groupPosition)
//            when (childPosition) {
//                0 -> {
//                    mainActivity.replaceFragment(CategoriesListFragment(), true)
//                }
//                1 -> {
//                    mainActivity.replaceFragment(ManditoryQuestionFragment(), true)
//                }
//                2 -> {
//                    mainActivity.replaceFragment(MultipleAnswerQuestionFragment(), true)
//                }
//                3 -> {
//                    mainActivity.replaceFragment(MultipleSelectionFragment(), true)
//                }
//                4 -> {
//                    mainActivity.replaceFragment(SingleSelectionFragment(), true)
//                }
//                5 -> {
//                    mainActivity.replaceFragment(FinishedFragment(), true)
//                }
//                else -> {
//                    mainActivity.replaceFragment(CategoriesListFragment(), true)
//                }
//            }
            mainActivity.replaceFragment(SingleSelectionFragment(), true)
            false
        })
    }

    override fun onClick(view: View, position: Int) {
        val selectedModel = questionsArrayList?.get(position)

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
            WSUtils.REQ_FOR_GET_QUESTIONS -> {
                parseGetQuestionsResponse(response as JsonObject)
            }
        }
    }

    private fun parseGetQuestionsResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("questions")) {
                val centersList = jsonObject.get("questions").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        questionsArrayList!!.add(QuestionsModel(jsonObjects!! as JsonObject))
                    }
                }
                questionsListAdapter!!.notifyDataSetChanged()
            } else StaticUtils.showIndefiniteToast(
                rootView!!,
                getString(R.string.something_went_wrong),
                getString(R.string.retry),
                View.OnClickListener { requestForGetQuestionsListWS() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        Log.e("error: ", error)
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_QUESTIONS -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetQuestionsListWS() })
            }
        }
    }

    override fun noInternetConnection(requestCode: Int) {
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_QUESTIONS -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetQuestionsListWS() })
            }
        }
    }

}
