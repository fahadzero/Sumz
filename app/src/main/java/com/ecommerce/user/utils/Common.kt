@file:Suppress("DEPRECATION")

package com.ecommerce.user.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.androidadvance.topsnackbar.TSnackbar
import com.ecommerce.user.R
import com.ecommerce.user.activity.ActLogin
import com.ecommerce.user.activity.ActMain
import com.ecommerce.user.utils.SharePreference.Companion.SELECTED_LANGUAGE
import com.ecommerce.user.utils.SharePreference.Companion.getStringPref
import com.ecommerce.user.utils.SharePreference.Companion.setBooleanPref
import com.ecommerce.user.utils.SharePreference.Companion.setStringPref
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Common {
    var isProfileEdit: Boolean = false
    var isProfileMainEdit: Boolean = false
    var isAddOrUpdated: Boolean = false

    //toast messages
    fun getToast(activity: Activity, strTxtToast: String) {
        Toast.makeText(activity, strTxtToast, Toast.LENGTH_SHORT).show()
    }

    fun isValidAmount(strPattern: String): Boolean {
        return Pattern.compile(
            "^[0-9]+([.][0-9]{2})?\$"
        ).matcher(strPattern).matches();
    }
    fun getLog(strKey: String, strValue: String) {
        Log.e(">>>---  $strKey  ---<<<", strValue)
    }

    fun isValidEmail(strPattern: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(strPattern).matches();
    }


    fun isCheckNetwork(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun openActivity(activity: Activity, destinationClass: Class<*>?) {
        activity.startActivity(Intent(activity, destinationClass))
        activity.overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    var dialog: Dialog? = null

    fun dismissLoadingProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    fun showLoadingProgress(context: Activity) {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dlg_progress)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    fun alertErrorOrValidationDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showErrorFullMsg(activity: Activity, message: String) {
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            TSnackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView: View = snackbar.getView()
        snackbarView.setBackgroundColor(Color.RED)
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun showSuccessFullMsg(activity: Activity, message: String) {
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            TSnackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundColor(activity.resources.getColor(R.color.light_green))
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun setLogout(activity: Activity) {
        val getUserID: String? = SharePreference.getStringPref(activity, SharePreference.userId)
        val isTutorialsActivity: Boolean =
            SharePreference.getBooleanPref(activity, SharePreference.isTutorial)
        val preference = SharePreference(activity)
        preference.mLogout()
        setBooleanPref(activity, SharePreference.isTutorial, isTutorialsActivity)
        if (getUserID != null) {
            setStringPref(activity,SharePreference.userId,"")
        }
        val intent = Intent(activity, ActLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    fun setImageUpload(strParameter: String, mSelectedFileImg: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            strParameter,
            mSelectedFileImg.getName(),
            RequestBody.create("image/*".toMediaType(), mSelectedFileImg)
        )
    }

    fun setRequestBody(bodyData: String): RequestBody {
        return bodyData.toRequestBody("text/plain".toMediaType())
    }


    fun closeKeyBoard(activity: Activity) {
        val view: View? = activity.currentFocus
        if (view != null) {
            try {
                val imm: InputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    fun getCurrentLanguage(context: Activity, isChangeLanguage: Boolean) {
        if (getStringPref(context, SELECTED_LANGUAGE) == null || getStringPref(
                context,
                SELECTED_LANGUAGE
            ).equals("", true)) {
            setStringPref(
                context,
                SELECTED_LANGUAGE,
                context.resources.getString(R.string.language_english)
            )
        }
        val locale = if (getStringPref(context, SELECTED_LANGUAGE).equals(
                context.resources.getString(
                    R.string.language_english
                ), true
            )) {
            Locale("en-us")
        } else {
            Locale("ar")
        }

        //start
        val activityRes = context.resources
        val activityConf = activityRes.configuration
        if (getStringPref(context, SELECTED_LANGUAGE).equals(
                context.resources.getString(R.string.language_english),
                true
            )) {
            activityConf.setLocale(Locale("en-us")) // API 17+ only.
        } else {
            activityConf.setLocale(Locale("ar"))
        }
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)
        val applicationRes = context.applicationContext.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(locale)
        applicationRes.updateConfiguration(applicationConf, applicationRes.displayMetrics)

        if (isChangeLanguage) {
            val intent = Intent(context, ActMain::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            context.finish()
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    fun getDate(strDate: String): String {
        val curFormater = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val dateObj = curFormater.parse(strDate)
        val postFormater = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return postFormater.format(dateObj)
    }
    //"2021-10-26 05:05:56"
    fun getDateTime(strDate: String): String {
        val curFormater = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dateObj = curFormater.parse(strDate)
        val postFormater = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US)
        return postFormater.format(dateObj)
    }
}