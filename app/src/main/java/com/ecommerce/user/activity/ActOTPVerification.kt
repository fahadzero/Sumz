package com.ecommerce.user.activity


import android.os.CountDownTimer
import android.view.View
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActOtpverificationBinding
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class ActOTPVerification : BaseActivity() {
    private lateinit var otpverificationBinding: ActOtpverificationBinding
    var strEmail: String = ""
    var strToken = ""
    var loginUserType = ""

    override fun setLayout(): View = otpverificationBinding.root

    override fun initView() {
        otpverificationBinding = ActOtpverificationBinding.inflate(layoutInflater)
        strEmail = intent.getStringExtra("email")!!
        strToken = FirebaseInstanceId.getInstance().token.toString()
        otpverificationBinding.tvCheckout.setOnClickListener {
            Common.closeKeyBoard(this@ActOTPVerification)
            if (otpverificationBinding.edOTP.text.toString().length != 6) {
                Common.alertErrorOrValidationDialog(
                    this@ActOTPVerification, resources.getString(
                        R.string.validation_otp
                    )
                )
            } else {
                if (loginUserType == "1") {
                    val map = HashMap<String, String>()
                    map["mobile"] = strEmail
                    map["otp"] = otpverificationBinding.edOTP.text.toString()
                    map["token"] = strToken
                    if (Common.isCheckNetwork(this@ActOTPVerification)) {
                        callApiOTP(map)
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActOTPVerification,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else {
                    val map = HashMap<String, String>()
                    map["email"] = strEmail
                    map["otp"] = otpverificationBinding.edOTP.text.toString()
                    map["token"] = strToken
                    if (Common.isCheckNetwork(this@ActOTPVerification)) {
                        callApiOTP(map)
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActOTPVerification,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
            }
        }
        timer()
        otpverificationBinding.tvResendOtp.setOnClickListener {
            val map = HashMap<String, String>()
            map["email"] = strEmail
            map["otp"] = otpverificationBinding.edOTP.text.toString()
            map["token"] = strToken
            if (Common.isCheckNetwork(this@ActOTPVerification)) {
                callApiResendOTP(map)
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActOTPVerification,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    //TODO CALL OTP API
    private fun callApiOTP(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActOTPVerification)
        val call: Call<JsonObject> = ApiClient.getClient.setEmailVerify(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val mainObject = JSONObject(response.body().toString())
                    val statusType = mainObject.getInt("status")
                    val statusMessage = mainObject.getString("message")
                    if (statusType == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@ActOTPVerification, statusMessage)
                    } else if (statusType == 1) {
                        Common.dismissLoadingProgress()
                        SharePreference.setStringPref(
                            this@ActOTPVerification,
                            SharePreference.userId,
                            mainObject.getJSONObject("data").getString("id")
                        )
                        SharePreference.setStringPref(
                            this@ActOTPVerification,
                            SharePreference.userName,
                            mainObject.getJSONObject("data").getString("name")
                        )
                        SharePreference.setStringPref(
                            this@ActOTPVerification,
                            SharePreference.userEmail,
                            mainObject.getJSONObject("data").getString("email")
                        )
                        SharePreference.setStringPref(
                            this@ActOTPVerification,
                            SharePreference.userMobile,
                            mainObject.getJSONObject("data").getString("mobile")
                        )
                        SharePreference.setStringPref(
                            this@ActOTPVerification,
                            SharePreference.userProfile,
                            mainObject.getJSONObject("data").getString("profile_pic")
                        )
                        SharePreference.setStringPref(
                            this@ActOTPVerification,
                            SharePreference.userRefralCode,
                            mainObject.getJSONObject("data").getString("referral_code")
                        )
                        SharePreference.setBooleanPref(
                            this@ActOTPVerification,
                            SharePreference.isLogin,
                            true
                        )
                        openActivity(ActMain::class.java)
                        finish()
                    }
                } else {
                    if (response.code() == 500) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActOTPVerification,
                            resources.getString(R.string.error_msg)
                        )
                    } else {
                        val mainErrorObject = JSONObject(response.errorBody()!!.string())
                        val strMessage = mainErrorObject.getString("message")
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@ActOTPVerification, strMessage)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActOTPVerification,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiResendOTP(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActOTPVerification)
        val call: Call<SingleResponse> = ApiClient.getClient.setResendEmailVerification(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: SingleResponse = response.body()!!
                    if (registrationResponse.status == 1) {
                        otpverificationBinding.edOTP.text.clear()
                        Common.dismissLoadingProgress()
                        Common.showSuccessFullMsg(
                            this@ActOTPVerification,
                            registrationResponse.message!!
                        )
                    } else if (registrationResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(
                            this@ActOTPVerification,
                            registrationResponse.message!!
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(
                        this@ActOTPVerification,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActOTPVerification,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onBackPressed() {
        Common.closeKeyBoard(this@ActOTPVerification)
        openActivity(ActLogin::class.java)
        finish()
        finishAffinity()
    }

    private fun timer() {
        object : CountDownTimer(120000, 1000) {
            override fun onTick(millis: Long) {
                otpverificationBinding.llOTP.visibility = View.GONE
                otpverificationBinding.tvResendOtp.visibility = View.GONE
                otpverificationBinding.tvTimer.visibility = View.VISIBLE
                val timer = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            millis
                        )
                    ), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millis)
                    )
                )
                otpverificationBinding.tvTimer.text = timer
                if (timer == "00:00") {
                    otpverificationBinding.tvTimer.visibility = View.GONE
                    otpverificationBinding.llOTP.visibility = View.VISIBLE
                    otpverificationBinding.tvResendOtp.visibility = View.VISIBLE
                }
            }

            override fun onFinish() {
                otpverificationBinding.tvTimer.visibility = View.GONE
                otpverificationBinding.llOTP.visibility = View.VISIBLE
                otpverificationBinding.tvResendOtp.visibility = View.VISIBLE
            }
        }.start()

    }
}
    
