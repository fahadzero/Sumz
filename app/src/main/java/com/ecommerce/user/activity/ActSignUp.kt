package com.ecommerce.user.activity

import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.View
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.RestResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActSignUpBinding
import com.ecommerce.user.model.RegistrationModel
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.showErrorFullMsg
import com.ecommerce.user.utils.SharePreference
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActSignUp : BaseActivity() {
    private lateinit var signUpBinding: ActSignUpBinding
    var strToken = ""
    override fun setLayout(): View = signUpBinding.root

    override fun initView() {
        signUpBinding = ActSignUpBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this@ActSignUp)
        signUpBinding.chbTermsCondition.setOnClickListener {
            startActivity(
                Intent(this@ActSignUp, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Terms Condition"
                )
            )
        }

        if (intent.getStringExtra("loginType") != null) {
            signUpBinding.edtFullname.setText(intent.getStringExtra("name")!!)
            signUpBinding.edtEmail.setText(intent.getStringExtra("profileEmail")!!)
            signUpBinding.edtPassword.visibility = View.GONE
            signUpBinding.edtEmail.isActivated = false
            signUpBinding.edtEmail.inputType = InputType.TYPE_NULL
        } else {
            signUpBinding.edtPassword.visibility = View.VISIBLE
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            strToken = task.result

        })
        signUpBinding.btnSignUp.setOnClickListener {
            signup()
            setResult(RESULT_OK)
        }
        signUpBinding.tvSkip.setOnClickListener { openActivity(ActMain::class.java) }
        signUpBinding.tvLogin.setOnClickListener { openActivity(ActLogin::class.java) }
        signUpBinding.tvBecomeavendors.setOnClickListener { openActivity(ActSignUpVendor::class.java) }
    }

    private fun signup() {
        if (intent.getStringExtra("loginType") != null) {
            if (signUpBinding.edtMobile.text.toString().equals("")) {
                showErrorFullMsg(this@ActSignUp, resources.getString(R.string.validation_all))
            } else if (intent.getStringExtra("loginType") == "facebook" || intent.getStringExtra("loginType") == "google") {
                val hasmap = HashMap<String, String>()
                hasmap["name"] = intent.getStringExtra("name")!!
                hasmap["email"] = intent.getStringExtra("profileEmail")!!
                hasmap["mobile"] = signUpBinding.edtMobile.text.toString()
                hasmap["referral_code"] = signUpBinding.edtRefrrelCode.text.toString()
                hasmap["token"] = intent.getStringExtra("strToken")!!
                hasmap["register_type"] = "email"
                hasmap["login_type"] = intent.getStringExtra("loginType")!!
                if (intent.getStringExtra("loginType") == "google") {
                    hasmap["google_id"] = intent.getStringExtra("profileId")!!
                    hasmap["facebook_id"] = ""
                } else {
                    hasmap["facebook_id"] = intent.getStringExtra("profileId")!!
                    hasmap["google_id"] = ""
                }
                if (Common.isCheckNetwork(this@ActSignUp)) {
                    if (signUpBinding.chbTermsCondition.isChecked) {
                        callApiRegistration(hasmap)
                    } else {
                        showErrorFullMsg(
                            this@ActSignUp,
                            resources.getString(R.string.terms_condition_error)
                        )
                    }
                } else {
                    alertErrorOrValidationDialog(
                        this@ActSignUp,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        } else {
            Log.d("token", strToken)

            if (signUpBinding.edtFullname.text.toString().equals("")) {
                showErrorFullMsg(this@ActSignUp, resources.getString(R.string.validation_all))
            } else if (signUpBinding.edtEmail.text.toString().equals("")) {
                showErrorFullMsg(this@ActSignUp, resources.getString(R.string.validation_all))
            } else if (!Common.isValidEmail(signUpBinding.edtEmail.text.toString())) {
                showErrorFullMsg(
                    this@ActSignUp,
                    resources.getString(R.string.validation_valid_email)
                )
            } else if (signUpBinding.edtMobile.text.toString().equals("")) {
                showErrorFullMsg(this@ActSignUp, resources.getString(R.string.validation_all))
            } else if (signUpBinding.edtPassword.text.toString().equals("")) {
                showErrorFullMsg(this@ActSignUp, resources.getString(R.string.validation_all))
            } else {
                if (signUpBinding.chbTermsCondition.isChecked) {
                    val hasmap = HashMap<String, String>()
                    hasmap["name"] = signUpBinding.edtFullname.text.toString()
                    hasmap["email"] = signUpBinding.edtEmail.text.toString()
                    hasmap["mobile"] = signUpBinding.edtMobile.text.toString()
                    hasmap["password"] = signUpBinding.edtPassword.text.toString()
                    hasmap["token"] = strToken
                    hasmap["login_type"] = "email"
                    hasmap["register_type"] = "email"
                    hasmap["referral_code"] = signUpBinding.edtRefrrelCode.text.toString()
                    if (Common.isCheckNetwork(this@ActSignUp)) {
                        callApiRegistration(hasmap)
                    } else {
                        alertErrorOrValidationDialog(
                            this@ActSignUp,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else {
                    showErrorFullMsg(
                        this@ActSignUp,
                        resources.getString(R.string.terms_condition_error)
                    )
                }

            }
        }
    }

    private fun callApiRegistration(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActSignUp)
        val call = ApiClient.getClient.setRegistration(hasmap)
        call.enqueue(object : Callback<RestResponse<RegistrationModel>> {
            override fun onResponse(
                call: Call<RestResponse<RegistrationModel>>,
                response: Response<RestResponse<RegistrationModel>>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: RestResponse<RegistrationModel> = response.body()!!
                    if (registrationResponse.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        var otpController = ""
                        otpController = if (SharePreference.getStringPref(
                                this@ActSignUp,
                                SharePreference.UserLoginType
                            ) == "1"
                        ) {
                            signUpBinding.edtMobile.text.toString()
                        } else {
                            signUpBinding.edtEmail.text.toString()
                        }
                        startActivity(
                            Intent(
                                this@ActSignUp,
                                ActOTPVerification::class.java
                            ).putExtra("email", otpController)
                        )
                    } else if (registrationResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActSignUp,
                            registrationResponse.getMessage()
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    val status = error.getInt("status")
                    if (status == 2) {
                        Common.dismissLoadingProgress()
                        val otpController = if (SharePreference.getStringPref(
                                this@ActSignUp,
                                SharePreference.UserLoginType
                            ) == "1"
                        ) {
                            signUpBinding.edtMobile.text.toString()
                        } else {
                            signUpBinding.edtEmail.text.toString()
                        }
                        startActivity(
                            Intent(
                                this@ActSignUp,
                                ActOTPVerification::class.java
                            ).putExtra("email", otpController)
                        )
                    } else {
                        Common.dismissLoadingProgress()
                        showErrorFullMsg(this@ActSignUp, error.getString("message"))
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<RegistrationModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActSignUp,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}