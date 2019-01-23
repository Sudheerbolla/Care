package com.careapp.fragments

import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.InputType
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
import com.careapp.dbutils.DbHelper
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

class QuestionFragmentSingleGroup : BaseFragment(), View.OnClickListener, IParserListener<JsonObject> {

    companion object {
        fun newInstance(questionsModel: QuestionsModel, anganwadiId: String): QuestionFragmentSingleGroup {
            val fragment = QuestionFragmentSingleGroup()
            val args = Bundle()
            args.putString("anganwadiId", anganwadiId)
            args.putSerializable("questionsModel", questionsModel)
            fragment.setArguments(args)
            return fragment
        }
    }

    var answerObject: JSONObject? = null

    private var dialog: Dialog? = null
    private var rootView: View? = null

    private lateinit var questionsActivity: QuestionsActivity
    private lateinit var binding: FragmentQuestionBinding
    private lateinit var questionsModel: QuestionsModel

    private val CHECKBOX: Int = 1
    private val RADIO: Int = 2
    private val TEXTFIELD: Int = 3
    private val TEXTAREA: Int = 4

    private lateinit var answerGroup: AnswerGroupsModel
    private lateinit var anganwadiId: String

    private var padding10: Int = 0
    private var padding6: Int = 0

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
        padding10 = StaticUtils.pxFromDp(questionsActivity, 10.0f).toInt()
        padding6 = StaticUtils.pxFromDp(questionsActivity, 6.0f).toInt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    private fun initComponents() {
        try {
            if (arguments != null) {
                val bundle: Bundle = arguments!!
                if (bundle.containsKey("questionsModel")) {
                    questionsModel = bundle.getSerializable("questionsModel") as QuestionsModel
                }
                if (bundle.containsKey("anganwadiId")) {
                    anganwadiId = bundle.getString("anganwadiId")!!
                } else anganwadiId = ""
            }
            questionsActivity.binding.imgViewNext.setOnClickListener(this)
            binding.txtQuestion.text = questionsModel.body
            if (TextUtils.isEmpty(questionsModel.hint)) {
                binding.txtHint.visibility = View.GONE
            } else {
                binding.txtHint.text = questionsModel.hint
                binding.txtHint.visibility = View.VISIBLE
            }
            addGroupLayout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addGroupLayout() {
        if (questionsModel.answerGroupsArrayList!!.size <= 0) return
        answerGroup = questionsModel.answerGroupsArrayList!!.get(0)
        val parentView = getParentView(answerGroup.answerInputsArrayList!!.get(0).inputType)
        val answerGroupHeadingTextView = AppTextView(questionsActivity)
        answerGroupHeadingTextView.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        answerGroupHeadingTextView.text = answerGroup.label
        answerGroupHeadingTextView.setPadding(padding10, padding10, padding10, padding10)
        answerGroupHeadingTextView.gravity = Gravity.CENTER_VERTICAL
        answerGroupHeadingTextView.textSize = 14.0f
        binding.linOptions.addView(answerGroupHeadingTextView)
        for (answerInput in answerGroup.answerInputsArrayList!!) {
            when (answerInput.inputType) {
                CHECKBOX -> {
                    parentView.addView(createCheckBox(answerInput))
                }
                RADIO -> {
                    parentView.addView(createRadioButton(answerInput))
                }
                TEXTFIELD -> {
                    parentView.addView(createEditText(answerInput.answerInputId, answerInput.label))
                }
                TEXTAREA -> {
                }
            }
        }
        binding.linOptions.addView(parentView)
    }

    private fun getParentView(inputType: Int?): ViewGroup {
        when (inputType) {
            RADIO -> {
                val radioGroup = RadioGroup(questionsActivity)
                radioGroup.orientation = LinearLayout.VERTICAL
                radioGroup.setBackgroundColor(ContextCompat.getColor(questionsActivity, R.color.colorWhite))
                radioGroup.setPadding(padding10, padding10, padding10, padding10)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(padding6, padding6, padding6, padding6)
                radioGroup.layoutParams = layoutParams
                return radioGroup
            }
            else -> {
                val linearLayout = LinearLayout(questionsActivity)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.setBackgroundColor(ContextCompat.getColor(questionsActivity, R.color.colorWhite))
                linearLayout.setPadding(padding10, padding10, padding10, padding10)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(padding6, padding6, padding6, padding6)
                linearLayout.layoutParams = layoutParams
                return linearLayout
            }
        }
    }

    private fun createEditText(id: String?, label: String?): AppEditText {
        val editText = AppEditText(questionsActivity)
        editText.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        editText.setHint(label)
        editText.id = editText.hashCode()
        editText.tag = id
        editText.inputType = InputType.TYPE_CLASS_TEXT
//        editText.imeOptions
        editText.setPadding(padding10, padding6, padding10, padding6)
        editText.textSize = 13.0f
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(padding6, padding6, padding6, padding6)
        editText.layoutParams = layoutParams
        return editText
    }

    private fun createRadioButton(answerInputModel: AnswerInputModel): RadioButton {
        val margin = StaticUtils.pxFromDp(questionsActivity, 3.0f).toInt()
        val radioButton = RadioButton(questionsActivity)
        radioButton.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        radioButton.text = answerInputModel.label
        radioButton.tag = answerInputModel.answerInputId
        radioButton.isChecked = answerInputModel.isSelected!!
        radioButton.id = radioButton.hashCode()
        radioButton.setPadding(padding6, padding6, padding6, padding6)
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.MATCH_PARENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(margin, margin, margin, margin)
        radioButton.layoutParams = layoutParams
        return radioButton
    }

    private fun createAnswerObjectEditText(id: String, value: String): JSONObject {
        val answerObject = JSONObject()
        try {
            answerObject.put("answerGroupId", questionsModel.answerGroupsArrayList!!.get(0).answerGroupId)
            answerObject.put("answerInputId", id)
            answerObject.put("answerValue", value)
            questionsModel.answerGroupsArrayList!!.get(0).answer = value
            questionsModel.answerGroupsArrayList!!.get(0).answeredInputId = id
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return answerObject
    }

    private fun createCheckBox(answerInput: AnswerInputModel): CheckBox {
        val margin = StaticUtils.pxFromDp(questionsActivity, 3.0f).toInt()
        val checkBox = CheckBox(questionsActivity)
        checkBox.setTextColor(ContextCompat.getColor(questionsActivity, R.color.colorBlack))
        checkBox.text = answerInput.label!!
        checkBox.tag = answerInput.answerInputId!!
        checkBox.setPadding(padding6, padding6, padding6, padding6)
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
//        if (questionsModel.houseSelectable!!) {
//            DbHelper(questionsActivity).addHouseToList()
//        }
//        questionsActivity.goToNextQuestion()
        try {
            val call = BaseApplication.wsInterface!!.postAnswerToQuestion(
                questionsModel.questionId!!,
                StaticUtils.getRequestBody(answerObject!!),
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
            R.id.imgViewNext -> {
                if (answerGroup == null || answerGroup.answerInputsArrayList == null || answerGroup.answerInputsArrayList!!.size <= 0) {
                    questionsActivity.goToNextQuestion()
                    return
                }
                when (answerGroup.answerInputsArrayList!!.get(0).inputType) {
                    CHECKBOX -> {
                        val jsonObject = JSONObject()
                        val answersArray = JSONArray()

                        for (i in 0..(binding.linOptions.getChildAt(1) as LinearLayout).childCount) {
                            if ((binding.linOptions.getChildAt(1) as LinearLayout).getChildAt(i) is CheckBox) {
                                val checkBox: CheckBox =
                                    (binding.linOptions.getChildAt(1) as LinearLayout).getChildAt(i) as CheckBox
                                if (checkBox.isChecked) {
                                    val answerObject = createAnswerObjectEditText(
                                        checkBox.tag as String,
                                        checkBox.text.toString().trim()
                                    )
                                    answersArray.put(answerObject)
                                }
                            }
                        }
                        jsonObject.put("questionId", questionsModel.questionId)
                        jsonObject.put("anganwadiCenterId", anganwadiId)
                        jsonObject.put("houseId", "")
                        jsonObject.put("answers", answersArray)
                        answerObject = jsonObject
                        requestForAnswerWS()
                    }
                    RADIO -> {
                        val jsonObject = JSONObject()
                        val answersArray = JSONArray()
                        val parent = binding.linOptions.getChildAt(1) as RadioGroup
                        for (i in 0..parent.childCount) {
                            if (parent.checkedRadioButtonId == -1) {
                                StaticUtils.showToast(rootView!!, "Please select any one option.")
                                return
                            }
                            val child = parent.getChildAt(i)
                            if (child is RadioButton) {
                                if (child.isChecked) {
                                    val answerObject = createAnswerObjectEditText(
                                        child.tag as String,
                                        child.text.toString().trim()
                                    )
                                    answersArray.put(answerObject)
                                }
                            }
                        }

                        jsonObject.put("questionId", questionsModel.questionId)
                        jsonObject.put("anganwadiCenterId", anganwadiId)
                        jsonObject.put("houseId", "")
                        jsonObject.put("answers", answersArray)
                        answerObject = jsonObject
                        requestForAnswerWS()
                    }
                    TEXTFIELD -> {
                        val jsonObject = JSONObject()
                        val answersArray = JSONArray()

                        for (i in 0..(binding.linOptions.getChildAt(1) as LinearLayout).childCount) {
                            if ((binding.linOptions.getChildAt(1) as LinearLayout).getChildAt(i) is AppEditText) {
                                val editText: AppEditText =
                                    (binding.linOptions.getChildAt(1) as LinearLayout).getChildAt(i) as AppEditText
                                val id = editText.tag as String
                                if (TextUtils.isEmpty(editText.text.toString().trim())) {
                                    StaticUtils.showToast(rootView!!, "Please enter your answer.")
                                    return
                                }
                                answersArray.put(createAnswerObjectEditText(id, editText.text.toString().trim()))
                            }
                        }

                        jsonObject.put("questionId", questionsModel.questionId)
                        jsonObject.put("anganwadiCenterId", anganwadiId)
                        jsonObject.put("houseId", "")
                        jsonObject.put("answers", answersArray)
                        answerObject = jsonObject
                        requestForAnswerWS()
                    }
                    TEXTAREA -> {
                    }
                }
            }
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
            val questionsModelOld = questionsModel
            questionsModelOld.isAnswered = true
            DbHelper(questionsActivity).addQuestion(questionsModelOld)
            questionsActivity.goToNextQuestion()
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
