package com.careapp.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.careapp.R
import com.careapp.databinding.ItemCategoriesListBinding
import com.careapp.interfaces.IClickListener
import com.careapp.models.HomesModel
import java.util.*

class HomesListAdapter(
    itemsData: ArrayList<HomesModel>,
    private var iClickListener: IClickListener?
) : RecyclerView.Adapter<HomesListAdapter.ViewHolder>() {

    private var homesArrayList: ArrayList<HomesModel>? = itemsData

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
        viewHolder.bind(homesArrayList!![position], iClickListener)
    }

    override fun getItemCount(): Int {
        return homesArrayList!!.size
    }

    class ViewHolder(var binding: ItemCategoriesListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoriesModel: HomesModel, iClickListener: IClickListener?) {
            binding.txtName.text = categoriesModel.name
            binding.txtName.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
        }

    }

}
