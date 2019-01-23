package com.careapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import com.careapp.BaseApplication
import com.careapp.BuildConfig
import com.careapp.R
import com.careapp.adapters.ViewPagerAdapter
import com.careapp.databinding.ActivityQuestionsBinding
import com.careapp.dbutils.DbHelper
import com.careapp.interfaces.IParserListener
import com.careapp.models.QuestionsModel
import com.careapp.utils.*
import com.careapp.views.CustomViewPagerSingleLoad
import com.careapp.wsutils.WSCallBacksListener
import com.careapp.wsutils.WSUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONException

class QuestionsActivity : BaseActivity(), View.OnClickListener, IParserListener<JsonObject>, LocationListener {

    public lateinit var binding: ActivityQuestionsBinding
    private lateinit var categoryId: String
    private lateinit var anganwadiId: String
    private var dialog: Dialog? = null
    private var questionsArrayList: ArrayList<QuestionsModel>? = null
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private var isGPS: Boolean? = false
    private var isNetwork: Boolean? = false
    private var canGetLocation: Boolean? = true

    public lateinit var lastKnownLocation: Location
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10.toFloat()
    private val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 10.toLong()
    private lateinit var locationManager: LocationManager

    override fun onResume() {
        super.onResume()
        if (isLocationEnabled) {
            if (RuntimePermissionUtils.checkPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getLocation()
            } else {
                requestPermissions()
            }
        } else {
            showAlert()
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            StaticUtils.showIndefiniteToast(window.decorView.rootView,
                "Location permission is manditory to use the application.",
                "Allow",
                View.OnClickListener {
                    RuntimePermissionUtils.requestForPermission(
                        this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        Constants.LOCATION_REQUEST_CODE
                    )
                })
        } else {
            StaticUtils.showIndefiniteToast(window.decorView.rootView,
                "Location permission is manditory to use the application.",
                "Allow",
                View.OnClickListener {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                })
        }
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Location Settings is set to 'Off'.\nPlease Enable Location to use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
        dialog.show()
    }

    private val isLocationEnabled: Boolean
        get() {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            if (StaticUtils.isAllPermissionsGranted(grantResults)) {
                getLocation()
            } else {
                StaticUtils.showIndefiniteToast(window.decorView.rootView,
                    "Location permission is manditory to use the application.",
                    "Allow",
                    View.OnClickListener {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            )
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            )
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            locationManager.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            )
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showProgress() {
        if (dialog != null && !dialog!!.isShowing) dialog!!.show()
    }

    fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_questions)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        binding.txtHeading.text = "Questions"
        getBundleData()
        initComponents()
    }

    private fun getBundleData() {
        try {
            val bundle: Bundle = intent.extras!!
            if (bundle.containsKey("categoryId")) {
                categoryId = bundle.getString("categoryId")!!
            } else categoryId = ""
            if (bundle.containsKey("anganwadiId")) {
                anganwadiId = bundle.getString("anganwadiId")!!
            } else anganwadiId = ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initComponents() {
        questionsArrayList = ArrayList()
        dialog = PopUtils.SimpleProgressDialog(this)
        questionsArrayList?.addAll(DbHelper(this).getQuestionsForACategory(categoryId))
        setListeners()
        if (!questionsArrayList.isNullOrEmpty())
            setViewPager() else StaticUtils.showSimpleToast(
            this,
            "No Questions available for this Category. Please check with different category."
        )
    }

    private fun requestForGetQuestionsPerCategory() {
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

    private fun setViewPager() {
        binding.txtCategory.text = questionsArrayList!!.get(0).categoryName
        binding.txtQuestionCount.text = "1/${questionsArrayList!!.size}"

        binding.imgViewPrevious.isEnabled = false
        binding.imgViewPrevious.isClickable = false

        viewPagerAdapter = ViewPagerAdapter(this, questionsArrayList, anganwadiId)

        binding.viewPager.setAdapter(viewPagerAdapter)
//        binding.viewPager.setPagingEnabled(false)
        binding.viewPager.setOffscreenPageLimit(0)

        binding.viewPager.setOnPageChangeListener(object : CustomViewPagerSingleLoad.OnPageChangeListener {

            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                enableAllButtons()
                if (p0 <= 0) {
                    binding.imgViewPrevious.isEnabled = false
                    binding.imgViewPrevious.isClickable = false
                }
                binding.txtQuestionCount.text = "${p0 + 1}/${questionsArrayList!!.size}"
            }
        })

        binding.progressBar.visibility = View.GONE
        binding.viewPager.visibility = View.VISIBLE
    }

    private fun enableAllButtons() {
        binding.imgViewNext.isEnabled = true
        binding.imgViewNext.isClickable = true
        binding.imgViewPrevious.isEnabled = true
        binding.imgViewPrevious.isClickable = true
    }

    private fun setListeners() {
        binding.imgViewNext.setOnClickListener(this)
        binding.imgViewPrevious.setOnClickListener(this)
    }

    public fun setHeading(heading: String) {
        binding.txtHeading.text = heading
    }

    private fun navigateToHomesList() {
        val intent = Intent(this, HomesListActivity::class.java)
//        intent.putExtra("categoryId", selectedModel?.id)
        startActivity(intent)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgViewNext -> {
                goToNextQuestion()
            }
            R.id.imgViewPrevious -> {
                goToPrevQuestion()
            }
        }
    }

    public fun goToNextQuestion() {
        if (binding.viewPager.currentItem < questionsArrayList!!.size - 1) {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1)
        } else if (binding.viewPager.currentItem == questionsArrayList!!.size - 1) {
            navigateToHomesList()
        }
    }

    public fun goToPrevQuestion() {
        if (binding.viewPager.currentItem > 0) {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1)
        }
    }

    override fun successResponse(requestCode: Int, response: JsonElement?) {
        Log.e("success: ", response.toString())
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_QUESTIONS_FOR_CATEGORY -> {
                parseGetQuestionsResponse(response as JsonObject)
            }
        }
    }

    private fun parseGetQuestionsResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.has("questions")) {
                val centersList = jsonObject.get("questions").asJsonArray
                if (!centersList.isJsonNull && centersList.size() > 0) {
                    for (jsonObjects in centersList) {
                        questionsArrayList!!.add(QuestionsModel(jsonObjects!! as JsonObject))
                    }
//                    questionsArrayList?.sortedWith(compareBy { it.rank })
                    questionsArrayList!!.sortWith(compareBy { it.rank })
                }
                if (!questionsArrayList.isNullOrEmpty())
                    setViewPager() else StaticUtils.showSimpleToast(
                    this,
                    "No Questions available for this Category. Please check with different category."
                )
            } else StaticUtils.showIndefiniteToast(
                window.decorView.rootView!!,
                getString(R.string.something_went_wrong),
                getString(R.string.retry),
                View.OnClickListener { requestForGetQuestionsPerCategory() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun errorResponse(requestCode: Int, error: String) {
        Log.e("error: ", error)
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_QUESTIONS_FOR_CATEGORY -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView!!,
                    error,
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetQuestionsPerCategory() })
            }
        }
    }

    override fun noInternetConnection(requestCode: Int) {
        hideProgress()
        when (requestCode) {
            WSUtils.REQ_FOR_GET_QUESTIONS_FOR_CATEGORY -> {
                StaticUtils.showIndefiniteToast(
                    window.decorView.rootView!!,
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    View.OnClickListener { requestForGetQuestionsPerCategory() })
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        lastKnownLocation.set(location)
        Log.e("loc: ", lastKnownLocation.latitude.toString() + " " + lastKnownLocation.longitude)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }


}
