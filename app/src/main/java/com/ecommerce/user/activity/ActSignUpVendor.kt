package com.ecommerce.user.activity


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.RestResponse
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActSignUpVendorBinding
import com.ecommerce.user.model.RegistrationModel
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.showErrorFullMsg
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnCompleteListener

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActSignUpVendor : BaseActivity() {
    private lateinit var signUpVendorBinding: ActSignUpVendorBinding
    var strToken = ""

    override fun setLayout(): View = signUpVendorBinding.root

    override fun initView() {
        signUpVendorBinding = ActSignUpVendorBinding.inflate(layoutInflater)
        init()
    }

    private fun init() {
        FirebaseApp.initializeApp(this@ActSignUpVendor)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            strToken = task.result

        })
        signUpVendorBinding.btnSignUpVendor.setOnClickListener {
            signupvendor()
            setResult(RESULT_OK)
        }
        signUpVendorBinding.ivBack.setOnClickListener { finish() }
    }

    private fun signupvendor() {
        Log.d("token", strToken)

        if (signUpVendorBinding.edtFullname.text.toString().equals("")) {
            Common.showErrorFullMsg(this@ActSignUpVendor, resources.getString(R.string.validation_all))
        } else if (signUpVendorBinding.edtEmail.text.toString().equals("")) {
            Common.showErrorFullMsg(this@ActSignUpVendor, resources.getString(R.string.validation_all))
        } else if (!Common.isValidEmail(signUpVendorBinding.edtEmail.text.toString())) {
            Common.showErrorFullMsg(
                this@ActSignUpVendor,
                resources.getString(R.string.validation_valid_email)
            )
        } else if (signUpVendorBinding.edtMobile.text.toString().equals("")) {
            Common.showErrorFullMsg(this@ActSignUpVendor, resources.getString(R.string.validation_all))
        } else if (signUpVendorBinding.edtPassword.text.toString().equals("")) {
            Common.showErrorFullMsg(this@ActSignUpVendor, resources.getString(R.string.validation_all))
        } else {
            if (signUpVendorBinding.chbTermsCondition.isChecked) {
                val hasmap = HashMap<String, String>()
                hasmap["name"] = signUpVendorBinding.edtFullname.text.toString()
                hasmap["email"] = signUpVendorBinding.edtEmail.text.toString()
                hasmap["mobile"] = signUpVendorBinding.edtMobile.text.toString()
                hasmap["password"] = signUpVendorBinding.edtPassword.text.toString()
                hasmap["token"] = strToken
                if (Common.isCheckNetwork(this@ActSignUpVendor)) {
                    callApiRegistrationVendore(hasmap)
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@ActSignUpVendor,
                        resources.getString(R.string.no_internet)
                    )
                }
            } else {
                Common.showErrorFullMsg(
                    this@ActSignUpVendor,
                    resources.getString(R.string.terms_condition_error)
                )
            }

        }
    }

    private fun callApiRegistrationVendore(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActSignUpVendor)
        val call: Call<SingleResponse> = ApiClient.getClient.setVendorsRegister(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        openSuccessDialog()

                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(
                            this@ActSignUpVendor,
                            restResponse.message!!
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(
                        this@ActSignUpVendor,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActSignUpVendor,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun openSuccessDialog() {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(this@ActSignUpVendor, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(this@ActSignUpVendor)
            val mView = mInflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvMessage)
            textDesc.text = "Vendor account registered successfully. \nPlease wait for approval process"
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                openActivity(ActMain::class.java)
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}