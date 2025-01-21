package com.mykameal.planner.basedata

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.mykameal.planner.R
import com.mykameal.planner.activity.AuthActivity
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BaseApplication {

    private var dialog: Dialog? = null


    fun getColoredSpanned(text: String, color: String): String {
        return "<font color=" + color.toString() + ">" + text.toString() + "</font>"
    }

    fun  alertError(context: Context?, msg:String?,status:Boolean){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.alert_dialog_box_error)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams
        val tvTitle: TextView =dialog.findViewById(R.id.tv_text)
        val btnOk: RelativeLayout =dialog.findViewById(R.id.btn_okay)
        tvTitle.text=msg
        btnOk.setOnClickListener {
            if (status){
                dialog.dismiss()
                // Clear user session
                val sessionManagement = SessionManagement(context)
                sessionManagement.sessionClear()
                // Redirect to LoginActivity
                val intent = Intent(context, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context.startActivity(intent)
            }else{
                dialog.dismiss()
            }

        }
        dialog.show()
    }


    fun getToken() :String{
        var fcmToken=""
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (!token.isNullOrEmpty()) {
                        fcmToken = token
                        // Token retrieval successful, you can use the token here
                        Log.d("FCMToken", "Token retrieved successfully: $token")
                    } else {
                        fcmToken = "Token is null or empty"
                        Log.e("FCM Token", "Token is null or empty")
                    }
                } else {
                    fcmToken = "Fetching FCM registration token failed"
                    Log.e("FCM Token", "Fetching FCM registration token failed", task.exception)
                }
            }
        }catch (e:Exception){
            fcmToken = "Token is null or empty"
        }
        return fcmToken
    }

    suspend fun fetchFcmToken(): String {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            if (!token.isNullOrEmpty()) {
                Log.d("FCMToken", "Token retrieved successfully: $token")
                token
            } else {
                Log.e("FCMToken", "Token is null or empty")
                "Token is null or empty"
            }
        } catch (e: Exception) {
            Log.e("FCMToken", "Exception while fetching token", e)
            "Fetching FCM registration token failed"
        }
    }



    fun isOnline(context: Context?): Boolean {
        context ?: return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun formatOnlyDate(date: String): String {
        try {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Define input format
            val outputDateFormat = SimpleDateFormat("dd", Locale.getDefault()) // Define output format

            val parsedDate = inputDateFormat.parse(date) // Parse the input string into a Date object
            return outputDateFormat.format(parsedDate!!) // Format the Date object to "dd"
        } catch (e: Exception) {
            e.printStackTrace()
            return "" // Return an empty string in case of an error
        }
    }

    fun formatonlyMonthYear(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }


    fun getFirstLetterOfDay(day: String): String {
        return day.take(1)  // Get the first character of the string
    }


    fun showMe(context: Context?) {
        dialog = Dialog(context!!,R.style.CustomDialog)
        dialog!!.setContentView(R.layout.my_progess)
        dialog!!.setCancelable(false)
        dialog!!.window!!.setDimAmount(0f)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
    }

    fun dismissMe() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun changeDateFormat(dt: String?): String {
        var outputDateStr = ""
        val inputFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        //        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        val outputFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        // String inputDateStr="2013-06-24";
        var date: Date? = null
        try {
            date = inputFormat.parse(dt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        outputDateStr = outputFormat.format(date)
        return outputDateStr
    }

    fun changeDateFormatHealth(dt: String?): String {
        var outputDateStr = ""
        val inputFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        //        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        val outputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        // String inputDateStr="2013-06-24";
        var date: Date? = null
        try {
            date = inputFormat.parse(dt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        outputDateStr = outputFormat.format(date)
        return outputDateStr
    }




    fun snackBarShow(context: Context, view: View?, msg: String?,status:Boolean) {
        if (status){
            val snackbar = Snackbar.make(view!!, msg!!, Snackbar.LENGTH_INDEFINITE)
                .setAction("Settings") { view1: View? ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }

            val snackbarView = snackbar.view
            // Change the background color
            snackbarView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            val snackbarTextView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            // Center the text
            snackbarTextView.gravity = Gravity.CENTER
            snackbarTextView.setTextColor(Color.BLACK)
            snackbarTextView.maxLines = 4
            snackbar.show()
        }else{
            val snackbar = Snackbar.make(view!!, msg!!, Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            // Change the background color
            snackbarView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            val snackbarTextView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            // Center the text
            snackbarTextView.gravity = Gravity.CENTER
            snackbarTextView.setTextColor(Color.BLACK)
            snackbarTextView.maxLines = 4
            snackbar.show()
        }
    }



    fun getPath(context: Context, uri: Uri): String? {
        var uri = uri
        val needToCheckUri = Build.VERSION.SDK_INT >= 19
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor =
                    context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


}