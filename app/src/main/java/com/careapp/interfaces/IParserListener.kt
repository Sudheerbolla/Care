package com.careapp.interfaces

import com.google.gson.JsonElement

interface IParserListener<T> {

    fun successResponse(requestCode: Int, response: JsonElement?)

    fun errorResponse(requestCode: Int, error: String)

    fun noInternetConnection(requestCode: Int)

}
