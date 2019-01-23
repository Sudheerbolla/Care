package com.careapp.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.careapp.R
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class StaticUtils {

    companion object {

        var softKeyBarHeight = 60
        val serverTimeFormatOld = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        val serverTimeFormat = "dd-MM-yyyy hh:mm a"
        val displayDateFormat = "dd-MM-yyyy"
        val displayTimeFormat = "hh:mm a"
        var serverTimeSimpleDateFormat = SimpleDateFormat(serverTimeFormat, Locale.US)

        fun showSimpleToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun showSimpleToast(context: Context, message: Int) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun distanceBetweenPoints(fromLocation: Location, toLocation: Location): String {
            try {
                val distance = fromLocation.distanceTo(toLocation).div(1000)
                return String.format(Locale.getDefault(), "%.2f", distance) + " km Away"
            } catch (e: Exception) {
                return "Unknown"
            }
        }

        public fun setExpandableListViewHeight(listView: ExpandableListView, group: Int) {
            val listAdapter = listView.expandableListAdapter
            var totalHeight = 0
            val desiredWidth = View.MeasureSpec.makeMeasureSpec(
                listView.width,
                View.MeasureSpec.EXACTLY
            )
            for (i in 0 until listAdapter.groupCount) {
                val groupItem = listAdapter.getGroupView(i, false, null, listView)
                groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)

                totalHeight += groupItem.measuredHeight

                if (listView.isGroupExpanded(i) && i != group || !listView.isGroupExpanded(i) && i == group) {
                    for (j in 0 until listAdapter.getChildrenCount(i)) {
                        val listItem = listAdapter.getChildView(
                            i, j, false, null,
                            listView
                        )
                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)

                        totalHeight += listItem.measuredHeight

                    }
                }
            }

            val params = listView.layoutParams
            var height = totalHeight + listView.dividerHeight * (listAdapter.groupCount - 1)
            if (height < 10)
                height = 200
            params.height = height
            listView.layoutParams = params
            listView.requestLayout()
        }

        fun getRequestBody(jsonObject: JSONObject): RequestBody {
            return RequestBody.create(MediaType.parse(Constants.CONTENT_TYPE_TEXT_PLAIN), jsonObject.toString())
        }

        fun String.toSpanned(): Spanned {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                return Html.fromHtml(this)
            }
        }

        fun setTextHTML(html: String): Spanned {
            val result: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
            return result
        }

        @Throws(IOException::class)
        fun downloadUrl(strUrl: String): String {
            var data = ""
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(strUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connect()
                iStream = urlConnection.inputStream
                val br = BufferedReader(InputStreamReader(iStream!!))
                val sb = StringBuffer()

                var line: String?
                do {
                    line = br.readLine()
                    if (line != null)
                        sb.append(br.readLine()) else break
                } while (true)
                data = sb.toString()
                br.close()
            } catch (e: Exception) {
                Log.d("Exception", e.toString())
            } finally {
                iStream!!.close()
                urlConnection!!.disconnect()
            }
            return data
        }

        fun showToast(container: View, message: String) {
            try {
                val snackbar = snackbar(container, message, Snackbar.LENGTH_LONG)
                snackbar.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showToast(container: View, message: String, actionText: String, onClickListener: View.OnClickListener) {
            try {
                val snackbar = snackbar(container, message, Snackbar.LENGTH_LONG)
                snackbar.setAction(actionText, onClickListener)
                snackbar.setActionTextColor(Color.BLACK)
                snackbar.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showIndefiniteToast(
            container: View,
            message: String,
            actionText: String,
            onClickListener: View.OnClickListener
        ) {
            try {
                val snackbar = snackbar(container, message, Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction(actionText, onClickListener)
                snackbar.setActionTextColor(Color.BLACK)
                snackbar.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun snackbar(container: View, message: String, duration: Int): Snackbar {
            val snackbar = Snackbar.make(container, message, duration)
            val sbView = snackbar.view
            val params: FrameLayout.LayoutParams
            if (sbView is FrameLayout) {
                params = sbView.getLayoutParams() as FrameLayout.LayoutParams
                params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                params.setMargins(0, 0, 0, softKeyBarHeight)
                sbView.setLayoutParams(params)
            } else if (sbView is CoordinatorLayout) {
            }
            sbView.setBackgroundColor(container.resources.getColor(R.color.colorLightGrey))
            val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.DKGRAY)
            textView.maxLines = 5
            textView.gravity = Gravity.CENTER_HORIZONTAL
            snackbar.addCallback(object : Snackbar.Callback() {

                override fun onDismissed(snackbar: Snackbar?, event: Int) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        // Snackbar closed on its own
                    }
                }

                override fun onShown(snackbar: Snackbar?) {}
            })
            return snackbar
        }

        private fun hasSoftKeys(c: Context): Boolean {
            val windowManager = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val hasSoftwareKeys: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val d = windowManager.defaultDisplay
                val realDisplayMetrics = DisplayMetrics()
                d.getRealMetrics(realDisplayMetrics)
                val realHeight = realDisplayMetrics.heightPixels
                val realWidth = realDisplayMetrics.widthPixels
                val displayMetrics = DisplayMetrics()
                d.getMetrics(displayMetrics)
                val displayHeight = displayMetrics.heightPixels
                val displayWidth = displayMetrics.widthPixels
                hasSoftwareKeys = realWidth - displayWidth > 0 || realHeight - displayHeight > 0
                if (hasSoftwareKeys) {
                    softKeyBarHeight =
                            if (realWidth - displayWidth > 0) realWidth - displayWidth else if (realHeight - displayHeight > 0) realHeight - displayHeight else 0
                }
            } else {
                val hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey()
                val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
                hasSoftwareKeys = !hasMenuKey && !hasBackKey
                softKeyBarHeight = if (hasSoftwareKeys) 50 else 0
            }
            return hasSoftwareKeys
        }

        fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }

        fun parseCode(message: String): String {
            val p = Pattern.compile("\\b\\d{6}\\b")
            val m = p.matcher(message)
            var code = ""
            while (m.find()) {
                code = m.group(0)
            }
            Log.e("parse Code : ", code)
            return code
        }

        fun parseCodeArray(message: String): CharArray {
            return parseCode(message).toCharArray()
        }

        /*fun getRequestBody(jsonObject: JSONObject): RequestBody {
            val MEDIA_TYPE_TEXT = MediaType.parse(Constants.CONTENT_TYPE_TEXT_PLAIN)
            return RequestBody.create(MEDIA_TYPE_TEXT, jsonObject.toString())
        }*/
        fun takeScreenshot(context: Activity): ByteArray? {
            val now = Date()
            var byteArray: ByteArray? = null
            DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

            try {
                val v1 = context.window.decorView.rootView
                v1.isDrawingCacheEnabled = true
                val bitmap = Bitmap.createBitmap(v1.drawingCache)
                v1.isDrawingCacheEnabled = false

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                byteArray = stream.toByteArray()
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            return byteArray
        }

        fun getAppVersion(context: Context): Int {
            var version = 0
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                version = pInfo.longVersionCode.toInt()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return version
        }

        fun getAppVersionName(context: Context): String {
            var version = ""
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                version = pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return version
        }

        fun dpFromPx(context: Context, px: Float): Float {
            return px / context.resources.displayMetrics.density
        }

        fun pxFromDp(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

        fun getBytesFromBitMap(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
            return stream.toByteArray()
        }

        fun getImage(image: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(image, 0, image.size)
        }

        fun getBitMapFromBytes(imageBytes: String): Bitmap {
            return getImage(Base64.decode(imageBytes.substring(imageBytes.indexOf(",") + 1), Base64.DEFAULT))
        }

        fun getBytesFromString(imageBytes: String): ByteArray {
            return Base64.decode(imageBytes.substring(imageBytes.indexOf(",") + 1), Base64.DEFAULT)
        }

        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses: List<ActivityManager.RunningAppProcessInfo>
                if (am != null) {
                    runningProcesses = am.runningAppProcesses
                    for (processInfo in runningProcesses) {
                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            for (activeProcess in processInfo.pkgList) {
                                if (activeProcess == context.packageName) {
                                    isInBackground = false
                                }
                            }
                        }
                    }
                }
            } else {
                val taskInfo: List<ActivityManager.RunningTaskInfo>
                if (am != null) {
                    taskInfo = am.getRunningTasks(1)
                    val componentInfo = taskInfo[0].topActivity
                    if (componentInfo.packageName == context.packageName) {
                        isInBackground = false
                    }
                }
            }
            return isInBackground
        }

        fun copyTextToClipBoard(context: Context, currentUrl: String) {
            if (!TextUtils.isEmpty(currentUrl)) {
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip =
                        ClipData.newPlainText("URL", currentUrl)
                showSimpleToast(context, "Link Copied to Clipboard")
            }
        }

        fun checkInternetConnection(context: Context): Boolean {
            val _activeNetwork =
                (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            return _activeNetwork != null && _activeNetwork.isConnected
        }

        fun isInternetConnectionFast(context: Context): Boolean {
            return Connectivity.isConnectedFast(context)
        }

        fun getDateFromTimeStamp(timeStamp: String?): String {
            val date = serverTimeSimpleDateFormat.parse(timeStamp)
            val formatter = SimpleDateFormat(displayDateFormat, Locale.US)
            val dateStr = formatter.format(date)
            return dateStr
        }

        fun getDateFromCalender(timeStamp: Date?): String {
            val formatter = SimpleDateFormat(displayDateFormat, Locale.US)
            val dateStr = formatter.format(timeStamp!!)
            return dateStr
        }

        fun getTimeFromTimeStamp(timeStamp: String?): String {
            val date = serverTimeSimpleDateFormat.parse(timeStamp)
            val formatter = SimpleDateFormat(displayTimeFormat, Locale.US)
            val timeStr = formatter.format(date)
            return timeStr
        }

        fun openCallIntent(phoneNo: String, context: Context) {
            if (!TextUtils.isEmpty(phoneNo)) {
                val dial: String = "tel:" + phoneNo
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
            } else {
                Toast.makeText(context, "Enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }

        fun composeEmail(context: Context, addresses: Array<String>) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:"))
            intent.putExtra(Intent.EXTRA_EMAIL, addresses)
//            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent)
            }
        }

        fun openGoogleMap(toLatitude: String, toLongitude: String, context: Context) {
            val url = ("http://maps.google.com/maps?daddr=" + toLatitude + "," + toLongitude)
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(mapIntent)
        }

        fun openGoogleMap(toAddress: String, context: Context) {
            val url = ("http://maps.google.com/maps?daddr=" + toAddress)
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(mapIntent)
        }

    }

}