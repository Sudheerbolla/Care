package com.careapp.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.careapp.R
import com.careapp.databinding.ActivityMainBinding
import com.careapp.fragments.AnganwadiCentersListFragment

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        replaceHomeFragment()
    }

    public fun setHeading(heading: String) {
        binding.txtHeading.text = heading
    }

    private fun replaceHomeFragment() {
        replaceFragment(AnganwadiCentersListFragment(), false)
    }

}
