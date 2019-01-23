package com.careapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import com.careapp.BaseApplication
import com.careapp.R
import com.careapp.dbutils.DbHelper
import com.careapp.interfaces.IParserListener
import com.careapp.models.AnganwadiModel
import com.careapp.models.CategoriesModel
import com.careapp.models.QuestionsModel
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.Constants
import com.careapp.utils.StaticUtils
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException
import java.util.*

class UpdateDataToDBInBackground : Service(), IParserListener<JsonObject> {

    private val binder = ServiceBinder()
    private lateinit var dbHelper: DbHelper
    private lateinit var categoryId: String
    private var categoriesListArrayList: ArrayList<CategoriesModel>? = null
    private var notificationManager: NotificationManager? = null
    private var counter: Int = 0

    inner class ServiceBinder : Binder() {
        val service: UpdateDataToDBInBackground
            get() = this@UpdateDataToDBInBackground
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (!AppLocalStorage.getInstance(this).getBoolean(AppLocalStorage.PREF_IS_DATA_UPDATING, false)) {
            categoriesListArrayList = ArrayList()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(12345678, getNotification())
            } else {
                startForeground(12345678, getPreOreoNotification())
            }
            requestForGetAnganwadiCentersWS()
        }
        return Service.START_STICKY
    }

    private fun requestForGetAnganwadiCentersWS() {
        try {
            val call = BaseApplication.wsInterface!!.getAnganwadiCenters(
                AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)
            )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_GET_ANGANWADI_CENTERS, call, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        dbHelper = DbHelper(this)
    }

    private fun requestForGetCategoriesWS() {
        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED, false)
        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATING, true)
        try {
            val call =
                BaseApplication.wsInterface!!.getCategories(
                    AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)
                )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_GET_CATEGORIES, call, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestForGetQuestionsForCategory() {
        try {
            val call = BaseApplication.wsInterface!!.getQuestionsOfCategory(
                categoryId,
                AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)
            )
            WSCallBacksListener.requestForJsonObject(this, WSUtils.REQ_FOR_GET_QUESTIONS_FOR_CATEGORY, call, this)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun parseGetQuestionsResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("questions")) {
                val centersList = jsonObject.get("questions").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        dbHelper.addQuestion(QuestionsModel(jsonObjects!! as JsonObject))
                    }
                    if (counter == categoriesListArrayList?.size) {
                        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED, true)
                        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATING, false)
                        updateAppAboutDataSync(true)
                        stopForeground(true)
                        stopSelf()
                    }
                }
            } else {
                StaticUtils.showSimpleToast(this, getString(R.string.something_went_wrong))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseGetCategoriesResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("categories")) {
                categoriesListArrayList!!.clear()
                val centersList = jsonObject.get("categories").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        val categoriesModel = CategoriesModel(jsonObjects!! as JsonObject)
                        categoriesListArrayList!!.add(categoriesModel)
                        dbHelper.addCategory(categoriesModel)
                    }
                }
                if (categoriesListArrayList!!.isNotEmpty()) {
                    categoriesListArrayList!!.sortedWith(compareBy { it.rank })
                    for (categoriesModel in categoriesListArrayList!!) {
                        counter++
                        categoryId = categoriesModel.id!!
                        requestForGetQuestionsForCategory()
                    }
                }
            } else StaticUtils.showSimpleToast(this, getString(R.string.something_went_wrong))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateAppAboutDataSync(isSuccess: Boolean) {
        try {
            val localIntent = Intent(Constants.BROADCAST_DATA_SYNC).apply {
                putExtra(Constants.DATA_STATUS, isSuccess)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseGetAnganwadiCentersResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("centers")) {
                val centersList = jsonObject.get("centers").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        dbHelper.addAnganwadiCenter(AnganwadiModel(jsonObjects!! as JsonObject))
                    }
                }
                requestForGetCategoriesWS()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun successResponse(requestCode: Int, response: JsonElement?) {
        Log.e("success: ", response.toString())
        when (requestCode) {
            WSUtils.REQ_FOR_GET_CATEGORIES -> {
                parseGetCategoriesResponse(response as JsonObject)
            }
            WSUtils.REQ_FOR_GET_QUESTIONS_FOR_CATEGORY -> {
                parseGetQuestionsResponse(response as JsonObject)
            }
            WSUtils.REQ_FOR_GET_ANGANWADI_CENTERS -> {
                parseGetAnganwadiCentersResponse(response as JsonObject)
            }
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        Log.e("error: ", error)
        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED, false)
        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATING, false)
        StaticUtils.showSimpleToast(this, error)
        updateAppAboutDataSync(false)
        stopForeground(true)
        stopSelf()
    }

    override fun noInternetConnection(requestCode: Int) {
        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED, false)
        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATING, false)
        StaticUtils.showSimpleToast(this, getString(R.string.no_internet_connection))
        updateAppAboutDataSync(false)
        stopForeground(true)
        stopSelf()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getNotification(): Notification {
        val channel =
            NotificationChannel("careapp_01", "CARE APP LOCATION CHANNEL", NotificationManager.IMPORTANCE_DEFAULT)
        channel.name = "CARE APP LOCATION"
        channel.lockscreenVisibility = View.VISIBLE
        channel.description = "Syncing your server data to local."
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
        val builder = Notification.Builder(applicationContext, "careapp_01").setAutoCancel(true)
        builder.setSettingsText("Settings")
        builder.setSubText("Syncing your server data to local.")
        builder.setContentTitle("Care App")
        builder.setContentText(Calendar.getInstance().get(Calendar.MINUTE).toString() + "")
        return builder.build()
    }

    private fun getPreOreoNotification(): Notification {
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(getApplicationContext(), "careapp_01")
            .setContentTitle("Care App").setContentText("Syncing your server data to local.")
            .setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true)
        val not: Notification = notification.build()
        return not
    }

}

