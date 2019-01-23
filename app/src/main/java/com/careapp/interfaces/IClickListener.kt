package com.careapp.interfaces

import android.view.View

interface IClickListener {

    fun onClick(view: View, position: Int)

    fun onLongClick(view: View, position: Int)

}
