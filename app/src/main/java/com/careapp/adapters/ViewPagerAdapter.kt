package com.careapp.adapters

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.careapp.fragments.BaseFragment
import com.careapp.fragments.QuestionFragment
import com.careapp.fragments.QuestionFragmentSingleGroup
import com.careapp.models.QuestionsModel

class ViewPagerAdapter(
    activity: FragmentActivity,
    questionsArrayList: ArrayList<QuestionsModel>?,
    anganwadiId: String
) : FragmentStatePagerAdapter(activity.supportFragmentManager) {

    private val mFragmentList = ArrayList<BaseFragment>()
    private val mFragmentTitleList = ArrayList<String>()
    private var mContext: Context

    init {
        mContext = activity
        for (question in questionsArrayList!!) {
            if (question.answerGroupsArrayList != null &&
                question.answerGroupsArrayList?.isNotEmpty()!! &&
                question.answerGroupsArrayList?.size!! == 1
            ) {
                addFragment(QuestionFragmentSingleGroup.newInstance(question,anganwadiId), "")
            } else
                addFragment(QuestionFragment.newInstance(question), "")
        }
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    override fun getItem(position: Int): BaseFragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: BaseFragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

}

/*{
      "id": "5c28d2991331be249199787c",
      "rank": 1,
      "body": "क्या भ्रमण के समय केंद्र खुला है ?",
      "hint": "यदि नही तो यही समाप्त करे।",
      "categoryId": "5c28d2991331be249199787b",
      "categoryName": "संसाधन",
      "conditionalDisplay": false,
      "houseSelectable": false,
      "houseSelectionPreference": null,
      "parentQuestionId": null,
      "answerGroups": [
        {
          "id": "5c28d2991331be249199787d",
          "rank": 2,
          "label": "स्थिति ",
          "mandatory": true,
          "multiple": null,
          "answerInputs": [
            {
              "id": "5c28d2991331be249199787e",
              "label": "हाँ ",
              "inputType": 2,
              "displayNextGroupOnSelect": null,
              "displayChildQuestionsOnSelect": null
            },
            {
              "id": "5c28d2991331be249199787f",
              "label": " नही",
              "inputType": 2,
              "displayNextGroupOnSelect": null,
              "displayChildQuestionsOnSelect": null
            }
          ]
        }
      ]
    }*/