package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActChangePasswordBinding
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.getCurrentLanguage
import com.ecommerce.user.utils.Common.isCheckNetwork
import com.ecommerce.user.utils.Common.showErrorFullMsg
import com.ecommerce.user.utils.Common.showLoadingProgress
import com.ecommerce.user.utils.SharePreference
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActChangePassword : BaseActivity() {
    private lateinit var changePasswordBinding: ActChangePasswordBinding

    override fun setLayout(): View = changePasswordBinding.root

    override fun initView() {
        changePasswordBinding = ActChangePasswordBinding.inflate(layoutInflater)
        changePasswordBinding.ivBack.setOnClickListener { finish() }
        changePasswordBinding.btnreset.setOnClickListener {
            if (changePasswordBinding.edOldPass.text.toString() == "") {
                showErrorFullMsg(
                    this@ActChangePassword,
                    resources.getString(R.string.validation_oldpassword)
                )
            } else if (changePasswordBinding.edNewPassword.text.toString() == "") {
                showErrorFullMsg(
                    this@ActChangePassword,
                    resources.getString(R.string.validation_password)
                )
            } else if (changePasswordBinding.edNewPassword.text.toString().length < 7) {
                showErrorFullMsg(
                    this@ActChangePassword,
                    resources.getString(R.string.validation_valid_password)
                )
            } else if (changePasswordBinding.edConfirmPassword.text.toString() == "") {
                showErrorFullMsg(
                    this@ActChangePassword,
                    resources.getString(R.string.validation_cpassword)
                )
            } else if (changePasswordBinding.edConfirmPassword.text.toString() != changePasswordBinding.edNewPassword.text.toString()
            ) {
                showErrorFullMsg(
                    this@ActChangePassword,
                    resources.getString(R.string.validation_valid_cpassword)
                )
            } else {
                val hasmap = HashMap<String, String>()
                hasmap["user_id"] = SharePreference.getStringPref(
                    this@ActChangePassword,
                    SharePreference.userId
                )!!
                hasmap["old_password"] = changePasswordBinding.edOldPass.text.toString()
                hasmap["new_password"] = changePasswordBinding.edNewPassword.text.toString()
                if (isCheckNetwork(this@ActChangePassword)) {
                    callApiChangepassword(hasmap)
                } else {
                    alertErrorOrValidationDialog(
                        this@ActChangePassword,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        }
    }

    //TODO API CHANGE PASSWORD CALL
    private fun callApiChangepassword(hasmap: HashMap<String, String>) {
        showLoadingProgress(this@ActChangePassword)
        val call = ApiClient.getClient.setChangePassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        dismissLoadingProgress()
                        successfulDialog(
                            this@ActChangePassword,
                            restResponse.message
                        )
                    }
                } else {
                    dismissLoadingProgress()
                    val error = JSONObject(response.errorBody()!!.string())
                    alertErrorOrValidationDialog(
                        this@ActChangePassword,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActChangePassword,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO PASSWORD CHANGE SUCCESS DIALOG
    @SuppressLint("InflateParams")
    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = m_view.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = m_view.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                finish()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this@ActChangePassword, false)
    }

    override fun onPause() {
        super.onPause()
        getCurrentLanguage(this@ActChangePassword, false)
    }
}