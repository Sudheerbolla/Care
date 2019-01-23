package com.careapp.wsutils

import android.content.Context
import android.util.Log
import com.careapp.interfaces.IParserListener
import com.careapp.utils.StaticUtils
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

object WSCallBacksListener {

    fun requestForJsonObject(
        context: Context,
        requestCode: Int,
        call: Call<JsonElement>,
        iParserListener: IParserListener<JsonObject>
    ) {
        try {
            if (!StaticUtils.checkInternetConnection(context)) {
                iParserListener.noInternetConnection(requestCode)
            } else {
                val callback = object : Callback<JsonElement> {
                    override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                        if (response.body() != null && /*response.body() instanceof JsonObject && */ (response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED)) {
                            if (response.body() is JsonObject) iParserListener.successResponse(
                                requestCode,
                                response.body()!!.asJsonObject
                            )
                            else if (response.body() is JsonArray) iParserListener.successResponse(
                                requestCode,
                                response.body()!!.asJsonArray
                            )
                        } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && response.message() == "Unauthorized") {
                            iParserListener.errorResponse(requestCode, response.message() + "Please relogin")
                            StaticUtils.showSimpleToast(context, "Please relogin")
//                            context.startActivity(Intent(context, LoginActivity::class.java))
                        } else iParserListener.errorResponse(requestCode, response.message())
                    }

                    override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                        iParserListener.errorResponse(requestCode, throwable.toString())
                    }
                }
                call.enqueue(callback)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun requestForJsonArray(
        context: Context,
        requestCode: Int,
        call: Call<JsonElement>,
        iParserListener: IParserListener<JsonElement>
    ) {
        try {
            if (!StaticUtils.checkInternetConnection(context)) {
                iParserListener.noInternetConnection(requestCode)
            } else {
                val callback = object : Callback<JsonElement> {
                    override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                        if (response.body() != null && (response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED)) {
                            if (response.body() is JsonObject)
                                iParserListener.successResponse(requestCode, response.body()!!.asJsonObject)
                            else if (response.body() is JsonArray)
                                iParserListener.successResponse(requestCode, response.body()!!.asJsonArray)
                            else
                                iParserListener.successResponse(requestCode, response.body()!!)
                        } else if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                            Log.e("response : ", response.message())
                            iParserListener.successResponse(requestCode, null)
//                        } else if (WSUtils.REQ_FOR_LOGIN === requestCode && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                            iParserListener.unSuccessResponse(requestCode, null)
                        } else {
                            var errorText: String? = null
                            try {
                                errorText =
                                        if (response.errorBody() != null && response.errorBody()!!.string() != null) response.errorBody()!!.string() else response.message()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            iParserListener.errorResponse(requestCode, errorText!!)
                        }

                    }

                    override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                        iParserListener.errorResponse(requestCode, throwable.toString())
                    }
                }
                call.enqueue(callback)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun requestForEmptyBody(
        context: Context,
        requestCode: Int,
        call: Call<Void>,
        iParserListener: IParserListener<JsonElement>
    ) {
        try {
            if (!StaticUtils.checkInternetConnection(context)) {
                iParserListener.noInternetConnection(requestCode)
            } else {
                val callback = object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED) {
                            iParserListener.successResponse(requestCode, null)
                        } else if (response.errorBody() != null) {
                            try {
                                iParserListener.errorResponse(requestCode, response.errorBody()!!.string())
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } else if (response.body() != null && response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                            iParserListener.successResponse(requestCode, null)
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        iParserListener.errorResponse(requestCode, t.toString())
                    }
                }
                call.enqueue(callback)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
