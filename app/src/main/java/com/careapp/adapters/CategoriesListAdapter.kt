package com.careapp.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.careapp.R
import com.careapp.databinding.ItemCategoriesListBinding
import com.careapp.interfaces.IClickListener
import com.careapp.models.CategoriesModel
import java.util.*

class CategoriesListAdapter(
    itemsData: ArrayList<CategoriesModel>,
    private var iClickListener: IClickListener?
) : RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    private var categoriesArrayList: ArrayList<CategoriesModel>? = itemsData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_categories_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(categoriesArrayList!![position], iClickListener)
    }

    override fun getItemCount(): Int {
        return categoriesArrayList!!.size
    }

    class ViewHolder(var binding: ItemCategoriesListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoriesModel: CategoriesModel, iClickListener: IClickListener?) {
            binding.txtName.text = categoriesModel.name
            binding.txtName.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
        }

    }

}
