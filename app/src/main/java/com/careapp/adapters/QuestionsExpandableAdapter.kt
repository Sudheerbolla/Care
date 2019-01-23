package com.careapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.careapp.R
import com.careapp.models.AnswerGroupsModel
import com.careapp.models.QuestionsModel
import com.careapp.views.AppTextView

class QuestionsExpandableAdapter(
    private val context: Context,
    private val listDataHeader: ArrayList<QuestionsModel>
) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosititon: Int): AnswerGroupsModel {
        return listDataHeader[groupPosition].answerGroupsArrayList!!.get(childPosititon)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView

        if (convertView == null) {
            val infalInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.list_group_child, null)
        }
        val subCat = getChild(groupPosition, childPosition)
        val txtListChild = convertView!!.findViewById<TextView>(R.id.lblListItem)
        txtListChild.text = subCat.label
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listDataHeader[groupPosition].answerGroupsArrayList!!.size
    }

    override fun getGroup(groupPosition: Int): QuestionsModel {
        return listDataHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return listDataHeader.size

    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.list_group_header,
                null
            )
        }

        val lblListHeader = convertView!!.findViewById<AppTextView>(R.id.lblListHeader)
        val cat = getGroup(groupPosition)
        if (cat.answerGroupsArrayList!!.isNotEmpty()) {
            if (isExpanded) {
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_black, 0)
            } else {
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_black, 0)
            }
        } else {
            lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.transparent_bg, 0)
        }

        lblListHeader.text = cat.body

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}