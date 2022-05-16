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
import com.ecommerce.user.databinding.ActForgotPasswordBinding
import com.ecommerce.user.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ActForgotPassword : BaseActivity() {
    private lateinit var forgotPasswordBinding: ActForgotPasswordBinding
    override fun setLayout(): View = forgotPasswordBinding.root

    override fun initView() {
        forgotPasswordBinding = ActForgotPasswordBinding.inflate(layoutInflater)
        forgotPasswordBinding.ivBack.setOnClickListener { finish() }
        forgotPasswordBinding.btnForgotPassword.setOnClickListener {
            if (forgotPasswordBinding.edtForgetEmail.text.toString() == "") {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.validation_all)
                )
            } else if (!Common.isValidEmail(forgotPasswordBinding.edtForgetEmail.text.toString())) {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.validation_valid_email)
                )
            } else {
                val hasmap = HashMap<String, String>()
                hasmap["email"] = forgotPasswordBinding.edtForgetEmail.text.toString()
                if (Common.isCheckNetwork(this@ActForgotPassword)) {
                    callApiForgetpassword(hasmap)
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@ActForgotPassword,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        }
    }

    //TODO API FORGET PASSWORD CALL
    private fun callApiForgetpassword(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActForgotPassword)
        val call = ApiClient.getClient.setforgotPassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status==1) {
                        successfulDialog(
                            this@ActForgotPassword,
                            restResponse.message
                        )
                    } else {
                        successfulDialog(
                            this@ActForgotPassword,
                            restResponse.message
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(
                        this@ActForgotPassword,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO FORGET PASSWORD SUCCESS DIALOG
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
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                finish()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActForgotPassword, false)
    }
}