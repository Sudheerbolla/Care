package com.careapp.fragments

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.careapp.R
import com.careapp.activities.QuestionsActivity
import com.careapp.databinding.FragmentQuestionBinding
import com.careapp.models.AnswerInputModel
import com.careapp.models.AnswerGroupsModel
import com.careapp.models.QuestionsModel
import com.careapp.utils.PopUtils
import com.careapp.utils.StaticUtils
import com.careapp.views.AppEditText
import com.careapp.views.AppTextView

class QuestionFragmentDbt : BaseFragment(), View.OnClickListener {

    companion object {
        fun newInstance(questionsModel: QuestionsModel): QuestionFragmentDbt {
            val fragment = QuestionFragmentDbt()
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
        if (arguments != null) {
            val bundle: Bundle = arguments!!
            if (bundle.containsKey("questionsModel")) {
                questionsModel = bundle.getSerializable("questionsModel") as QuestionsModel
            }
        }
        setUpView()
    }

    private val CHECKBOX: Int = 1
    private val RADIO: Int = 2
    private val TEXTFIELD: Int = 3
    private val TEXTAREA: Int = 4

    private fun addFirstLayout(answerId: String) {
        if (questionsModel.answerGroupsArrayList!!.size <= 0) return
        val answerGroup = questionsModel.answerGroupsArrayList!!.get(0)
        val padding10 = StaticUtils.pxFromDp(questionsActivity, 10.0f).toInt()
        val padding6 = StaticUtils.pxFromDp(questionsActivity, 6.0f).toInt()
        val parentView = getParentView(answerGroup.answerInputsArrayList!!.get(0).inputType)
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
                        makeAllOptionsDeselected(answerGroup, answerInput)
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

    private fun makeAllOptionsDeselected(answerGroup: AnswerGroupsModel, answerInput: AnswerInputModel) {
        for (allInputs in answerGroup.answerInputsArrayList!!) allInputs.isSelected = false
        answerInput.isSelected = true
    }

    private fun addChildAtPosition(position: Int) {
        if (position >= questionsModel.answerGroupsArrayList!!.size)
            return

        val answerGroup = questionsModel.answerGroupsArrayList!!.get(position)
        val padding10 = StaticUtils.pxFromDp(questionsActivity, 10.0f).toInt()
        val padding6 = StaticUtils.pxFromDp(questionsActivity, 6.0f).toInt()
        val parentView = getParentView(answerGroup.answerInputsArrayList!!.get(0).inputType)
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

    private fun setUpView() {
        binding.txtQuestion.text = questionsModel.body
        if (TextUtils.isEmpty(questionsModel.hint)) {
            binding.txtHint.visibility = View.GONE
        } else {
            binding.txtHint.text = questionsModel.hint
            binding.txtHint.visibility = View.VISIBLE
        }
//        binding.linOptions.removeAllViews()
//        binding.linOptions.invalidate()
        addFirstLayout("")
    }

    private fun getParentView(inputType: Int?): ViewGroup {
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
                linearLayout.orientation = LinearLayout.HORIZONTAL
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
            if (answerInputModel.displayNextGroupOnSelect != null && answerInputModel.displayNextGroupOnSelect!!) {
                if (isChecked) {
                    addChildAtPosition(position + 1)
                } else removeLastChild(position, answerId)
            } else removeLastChild(position, answerId)
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

    override fun onClick(v: View?) {

    }
/*input_type values
    1: checkbox
    2: radio
    3: textfield
    4: textarea*/

}
