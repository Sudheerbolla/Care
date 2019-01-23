package com.careapp.fragments

import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.activities.HomesListActivity
import com.careapp.activities.QuestionsActivity
import com.careapp.databinding.FragmentQuestionBinding
import com.careapp.interfaces.IParserListener
import com.careapp.models.AnswerGroupsModel
import com.careapp.models.AnswerInputModel
import com.careapp.models.QuestionsModel
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.views.AppEditText
import com.careapp.views.AppTextView
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class QuestionFragment : BaseFragment(), View.OnClickListener, IParserListener<JsonObject> {

    companion object {
        fun newInstance(questionsModel: QuestionsModel): QuestionFragment {
            val fragment = QuestionFragment()
            val args = Bundle()
            args.putSerializable("questionsModel", questionsModel)
            fragment.setArguments(args)
            return fragment
        }
    }

    private var dialog: Dialog? = null
    private var rootView: View? = null

    private lateinit var questionsActivity: QuestionsActivity
    private lateinit var binding: FragmentQuestionBinding
    private lateinit var questionsModel: QuestionsModel

    private val CHECKBOX: Int = 1
    private val RADIO: Int = 2
    private val TEXTFIELD: Int = 3
    private val TEXTAREA: Int = 4

    private var viewslist: ArrayList<ViewGroup>? = null

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionsActivity = activity as QuestionsActivity
        dialog = PopUtils.SimpleProgressDialog(questionsActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    private fun initComponents() {
        viewslist = ArrayList()
        if (arguments != null) {
            val bundle: Bundle = arguments!!
            if (bundle.containsKey("questionsModel")) {
                questionsModel = bundle.getSerializable("questionsModel") as QuestionsModel
            }
        }

        binding.linOptions.removeAllViews()

        binding.txtQuestion.text = questionsModel.body
        if (TextUtils.isEmpty(questionsModel.hint)) {
            binding.txtHint.visibility = View.GONE
        } else {
            binding.txtHint.text = questionsModel.hint
            binding.txtHint.visibility = View.VISIBLE
        }
        addFirstLayout("")
    }

    private fun addFirstLayout(answerId: String) {
        if (questionsModel.answerGroupsArrayList!!.size <= 0) return
        val answerGroup = questionsModel.answerGroupsArrayList!!.get(0)
        val padding10 = StaticUtils.pxFromDp(questionsActivity, 10.0f).toInt()
        val padding6 = StaticUtils.pxFromDp(questionsActivity, 6.0f).toInt()
        val parentView = getParentView()
        val answerGroupHeadingTextView = AppTextView(questionsActivity)
        answerGroupHeadingTextView.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        answerGroupHeadingTextView.text = answerGroup.label
        answerGroupHeadingTextView.setPadding(padding10, padding10, padding10, padding10)
        answerGroupHeadingTextView.gravity = Gravity.CENTER_VERTICAL
        answerGroupHeadingTextView.textSize = 14.0f
        parentView.addView(answerGroupHeadingTextView)
        for (answerInput in answerGroup.answerInputsArrayList!!) {
            when (answerInput.inputType) {
                CHECKBOX -> {
                    parentView.addView(createCheckBox(answerInput.label!!, answerInput.answerInputId!!))
                }
                RADIO -> {
                    if (!TextUtils.isEmpty(answerId) && answerInput.answerInputId.equals(answerId)) {
                        makeAllOptionsDeselected(answerGroup)
                        answerInput.isSelected = true
                    }
                    parentView.addView(createRadioButton(answerInput, 0))
                }
                TEXTFIELD -> {
                    val editText = AppEditText(questionsActivity)
                    editText.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
                    editText.setHint("Write your answer here")
                    editText.setPadding(padding10, padding6, padding10, padding6)
                    editText.textSize = 13.0f
                    parentView.addView(editText)
                }
                TEXTAREA -> {
                }
            }
        }
        binding.linOptions.addView(parentView)
    }

    private fun makeAllOptionsDeselected(answerGroup: AnswerGroupsModel) {
        for (allInputs in answerGroup.answerInputsArrayList!!) allInputs.isSelected = false
    }

    private fun addChildAtPosition(position: Int) {
        if (position >= questionsModel.answerGroupsArrayList!!.size)
            return

        val answerGroup = questionsModel.answerGroupsArrayList!!.get(position)
        val padding10 = StaticUtils.pxFromDp(questionsActivity, 10.0f).toInt()
        val padding6 = StaticUtils.pxFromDp(questionsActivity, 6.0f).toInt()
        val parentView = getParentView()
        val answerGroupHeadingTextView = AppTextView(questionsActivity)
        answerGroupHeadingTextView.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        answerGroupHeadingTextView.text = answerGroup.label
        answerGroupHeadingTextView.setPadding(padding10, padding10, padding10, padding10)
        answerGroupHeadingTextView.gravity = Gravity.CENTER_VERTICAL
        answerGroupHeadingTextView.textSize = 14.0f
        parentView.addView(answerGroupHeadingTextView)

        for (answerInput in answerGroup.answerInputsArrayList!!) {
            when (answerInput.inputType) {
                CHECKBOX -> {
                    parentView.addView(createCheckBox(answerInput.label!!, answerInput.answerInputId!!))
                }
                RADIO -> {
                    parentView.addView(createRadioButton(answerInput, position))
                }
                TEXTFIELD -> {
                    val editText = AppEditText(questionsActivity)
                    editText.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
                    editText.setHint("Write your answer here")
                    editText.setPadding(padding10, padding6, padding10, padding6)
                    editText.textSize = 13.0f
                    parentView.addView(editText)
                }
                TEXTAREA -> {
                }
            }
        }
        binding.linOptions.addView(parentView)
    }

    /*    private fun getParentView(inputType: Int?): ViewGroup {
        val padding = StaticUtils.pxFromDp(questionsActivity, 10.0f).toInt()
        when (inputType) {
            RADIO -> {
                val radioGroup = RadioGroup(questionsActivity)
                radioGroup.orientation = LinearLayout.VERTICAL
                radioGroup.setBackgroundColor(ContextCompat.getColor(questionsActivity, R.color.colorWhite))
                radioGroup.setPadding(padding, padding, padding, padding)
                val margin = StaticUtils.pxFromDp(questionsActivity, 5.0f).toInt()
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(margin, margin, margin, margin)
                return radioGroup
            }
            else -> {
                val linearLayout = LinearLayout(questionsActivity)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.setBackgroundColor(ContextCompat.getColor(questionsActivity, R.color.colorWhite))
                linearLayout.setPadding(padding, padding, padding, padding)
                val margin = StaticUtils.pxFromDp(questionsActivity, 5.0f).toInt()
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(margin, margin, margin, margin)

                return linearLayout
            }
        }
    }
*/
    private fun getParentView(): ViewGroup {
        val padding = StaticUtils.pxFromDp(questionsActivity, 10.0f).toInt()
        val linearLayout = LinearLayout(questionsActivity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setBackgroundColor(ContextCompat.getColor(questionsActivity, R.color.colorWhite))
        linearLayout.setPadding(padding, padding, padding, padding)
        val margin = StaticUtils.pxFromDp(questionsActivity, 5.0f).toInt()
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(margin, margin, margin, margin)
        return linearLayout
    }

    private fun createRadioButton(answerInputModel: AnswerInputModel, position: Int): RadioButton {
        val padding = StaticUtils.pxFromDp(questionsActivity, 7.0f).toInt()
        val margin = StaticUtils.pxFromDp(questionsActivity, 3.0f).toInt()
        val radioButton = RadioButton(questionsActivity)
        radioButton.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        radioButton.text = answerInputModel.label
        radioButton.tag = answerInputModel.answerInputId
        radioButton.isChecked = answerInputModel.isSelected!!
        radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            val answerId = buttonView.tag.toString()
            if (questionsModel.answerGroupsArrayList!!.size > 1) {
                if (answerInputModel.displayNextGroupOnSelect != null && answerInputModel.displayNextGroupOnSelect!!) {
                    makeAllOptionsDeselected(questionsModel.answerGroupsArrayList!!.get(position))
                    if (isChecked) {
                        removeLastChild(position, answerId)
                        addChildAtPosition(position + 1)
                    } else removeLastChild(position, answerId)
                } else {
                    if (position == 0) {
                        makeAllOptionsDeselected(questionsModel.answerGroupsArrayList!!.get(position))
                        removeLastChild(position, answerId)
                    }
                }
            }
        }
        radioButton.setPadding(padding, padding, padding, padding)
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.MATCH_PARENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(margin, margin, margin, margin)
        radioButton.layoutParams = layoutParams
        return radioButton
    }

    private fun removeLastChild(position: Int, answerId: String) {
        binding.linOptions.removeAllViews()
        binding.linOptions.invalidate()
        addFirstLayout(answerId)
        if (position > 0)
            for (i in 1..position) {
                addChildAtPosition(i)
            }
    }

    private fun createCheckBox(text: String, id: String): CheckBox {
        val padding = StaticUtils.pxFromDp(questionsActivity, 7.0f).toInt()
        val margin = StaticUtils.pxFromDp(questionsActivity, 3.0f).toInt()
        val checkBox = CheckBox(questionsActivity)
        checkBox.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        checkBox.text = text
        checkBox.tag = id
        checkBox.setPadding(padding, padding, padding, padding)
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.MATCH_PARENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(margin, margin, margin, margin)
        checkBox.layoutParams = layoutParams
        return checkBox
    }

    private fun navigateToHomesList() {
        val intent = Intent(questionsActivity, HomesListActivity::class.java)
//        intent.putExtra("categoryId", selectedModel?.id)
        startActivity(intent)
        questionsActivity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun requestForAnswerWS() {
        try {
            val jsonObject = JSONObject()
            val answersArray = JSONArray()
            val answerObject = JSONObject()
            answerObject.put("answerGroupId", questionsModel.answerGroupsArrayList!!.get(0).answerGroupId)
            answerObject.put(
                "answerType",
                questionsModel.answerGroupsArrayList!!.get(0).answerInputsArrayList!!.get(0).inputType
            )
            answerObject.put(
                "answerValue",
                questionsModel.answerGroupsArrayList!!.get(0).answerInputsArrayList!!.get(0).label
            )
            answersArray.put(answerObject)
            jsonObject.put("questionId", questionsModel.questionId)
            jsonObject.put("houseId", "")
            jsonObject.put("answers", answersArray.toString())
            val call = BaseApplication.wsInterface!!.postAnswerToQuestion(
                questionsModel.questionId!!,
                StaticUtils.getRequestBody(jsonObject),
                AppLocalStorage.getInstance(questionsActivity).getString(AppLocalStorage.PREF_USER_TOKEN)
            )
            WSCallBacksListener.requestForJsonObject(
                questionsActivity,
                WSUtils.REQ_FOR_ANSWER_QUESTION,
                call,
                this
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.imgViewNext -> {
//                requestForAnswerWS()
//            }
        }
    }

    override fun successResponse(requestCode: Int, response: JsonElement?) {
        Log.e("success: ", response.toString())
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_ANSWER_QUESTION -> {
                parseAnswerQuestionResponse(response as JsonObject)
            }
        }
    }

    private fun parseAnswerQuestionResponse(jsonObject: JsonObject) {
        try {
//            if (jsonObject.has("questions")) {
//                val centersList = jsonObject.get("questions").asJsonArray
//                if (!centersList.isJsonNull && centersList.size() > 0) {
//                    for (jsonObjects in centersList) {
//                        questionsArrayList!!.add(QuestionsModel(jsonObjects!! as JsonObject))
//                    }
////                    questionsArrayList?.sortedWith(compareBy { it.rank })
//                    questionsArrayList!!.sortWith(compareBy { it.rank })
//                }
//                if (!questionsArrayList.isNullOrEmpty())
//                    setViewPager() else StaticUtils.showSimpleToast(
//                    this,
//                    "No Questions available for this Category. Please check with different category."
//                )
//            } else StaticUtils.showIndefiniteToast(
//                window.decorView.rootView!!,
//                getString(R.string.something_went_wrong),
//                getString(R.string.retry),
//                View.OnClickListener { requestForGetQuestionsPerCategory() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        Log.e("error: ", error)
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_ANSWER_QUESTION -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForAnswerWS() })
            }
        }
    }

    override fun noInternetConnection(requestCode: Int) {
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_ANSWER_QUESTION -> {
                StaticUtils.showIndefiniteToast(
                    rootView!!,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForAnswerWS() })
            }
        }
    }

/*input_type values
    1: checkbox
    2: radio
    3: textfield
    4: textarea


field :house_selection_preference, type: Integer
  # 0: Random
  # 1: Referred daughter-in-law
  # 2: Referred daughter
  # 3: Unreferred daughter-in-law
  # 4: Unreferred daughter
  # 5: Home delivery of daughter-in-law's child
  # 6: Home delivery of daughter's child
  # 7: Hospital delivery of daughter-in-law's child
  # 8: Hospital delivery of daughter's child
  # 9: Daughter-in-law's child */

}
