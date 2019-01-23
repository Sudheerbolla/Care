package com.careapp.models


class SelectAnganwadiModel {

    var groupText: String? = null
    var childItems: ArrayList<String>? = null

    constructor(position: Int) {
        when (position) {
            0 -> {
                groupText = "Select From List 1"
                childItems = ArrayList()
                childItems?.add("Option 1")
                childItems?.add("Option 2")
                childItems?.add("Option 3")
                childItems?.add("Option 4")
                childItems?.add("Option 5")
                childItems?.add("Option 6")
                childItems?.add("Option 7")
            }
            1 -> {
                groupText = "Select From List 2"
                childItems = ArrayList()
                childItems?.add("Option 1")
                childItems?.add("Option 2")
                childItems?.add("Option 3")
            }
            2 -> {
                groupText = "Select From List 3"

                childItems = ArrayList()
                childItems?.add("Option 1")
                childItems?.add("Option 2")
                childItems?.add("Option 3")
                childItems?.add("Option 4")
            }
            3 -> {
                groupText = "Select From List 4"

                childItems = ArrayList()
                childItems?.add("Option 1")
                childItems?.add("Option 2")
                childItems?.add("Option 3")
                childItems?.add("Option 4")
                childItems?.add("Option 5")
            }
            else -> {
                groupText = "Select From List"
                childItems = ArrayList()
                childItems?.add("Option 1")
                childItems?.add("Option 2")
            }
        }
    }
}
