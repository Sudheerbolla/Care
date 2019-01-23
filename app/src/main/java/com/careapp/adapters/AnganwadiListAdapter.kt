package com.careapp.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.careapp.R
import com.careapp.databinding.ItemAnganwadisListBinding
import com.careapp.interfaces.IClickListener
import com.careapp.models.AnganwadiModel
import java.util.*

class AnganwadiListAdapter : RecyclerView.Adapter<AnganwadiListAdapter.ViewHolder> {

    private var context: Context? = null
    private var anganwadisList: ArrayList<AnganwadiModel>? = null
    private var iClickListener: IClickListener? = null

    constructor(context: Context, anganwadisList: ArrayList<AnganwadiModel>, iClickListener: IClickListener?) {
        this.anganwadisList = anganwadisList
        this.iClickListener = iClickListener
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_anganwadis_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(context!!, anganwadisList!![position], iClickListener)
    }

    override fun getItemCount(): Int {
        return anganwadisList!!.size
    }

    class ViewHolder(var binding: ItemAnganwadisListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, anganwadiModel: AnganwadiModel, iClickListener: IClickListener?) {
            binding.txtDescription.text =
                    "${anganwadiModel.completed} of ${anganwadiModel.total} houses survey completed"
            binding.txtName.text = anganwadiModel.name
//            binding.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_next))
            binding.txtDescription.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
            binding.txtName.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
            binding.imgStatus.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
            binding.cardBody.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
        }

    }

}
