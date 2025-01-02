package com.yesitlabs.mykaapp.commonworkutils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.basedata.AppConstant
import java.util.*

class CommonWorkUtils(var context: Context) {

    private var dialog: Dialog? = null
    private val dialog1: Dialog? = null
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init{
        pref=context.getSharedPreferences(AppConstant.LOGIN_SESSION, Context.MODE_PRIVATE)
        editor=pref?.edit()
    }


    fun hideStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView = activity.window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }
    }


    @SuppressLint("MissingInflatedId", "LocalSuppress")
    fun alertDialog(context: Context?, error: String?, finish: Boolean) {
        if (context != null) {
            val inflator = LayoutInflater.from(context)
            val dialogView: View = inflator.inflate(R.layout.alert_dialog_box_error, null)
            val alert = Dialog(context, R.style.BottomSheetDialog)
            alert.setCancelable(false)
            alert.setContentView(dialogView)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alert.window!!.attributes)
            //            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            alert.window!!.attributes = layoutParams
            val tvMessage = dialogView.findViewById<TextView>(R.id.tv_text) as TextView
            tvMessage.text = error
            val tvOk = dialogView.findViewById<RelativeLayout>(R.id.btn_okay)
            tvOk.setOnClickListener { view: View? -> alert.dismiss() }
            alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.show()
        }
    }


    fun getAddress(context: Context,lat: Double, longi: Double): String {
        var address = ""
        try {
            val geocoder: Geocoder
            val addresses: List<Address>?
            geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                lat,
                longi,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses!![0].getAddressLine(0)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return address
    }


    fun isOnline(context: Context?): Boolean {
        if (context != null) {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (networkInfo in info) if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false
    }

}