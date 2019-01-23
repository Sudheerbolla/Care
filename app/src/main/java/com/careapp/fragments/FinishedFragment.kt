package com.careapp.fragments

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.careapp.R
import com.careapp.activities.MainActivity
import com.careapp.databinding.FragmentFinishedBinding
import com.careapp.utils.PopUtils

class FinishedFragment : BaseFragment(), View.OnClickListener {

    private var dialog: Dialog? = null
    private var rootView: View? = null

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentFinishedBinding

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finished, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    private fun initComponents() {
    }

    override fun onClick(v: View?) {

    }

}
