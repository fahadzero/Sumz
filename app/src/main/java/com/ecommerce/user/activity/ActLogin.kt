package com.ecommerce.user.activity


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.RestResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActLoginBinding
import com.ecommerce.user.model.LoginModel
import com.ecommerce.user.model.RegistrationModel
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActLogin : BaseActivity() {
    private lateinit var loginBinding: ActLoginBinding

    //:::::::::::::::Google Login::::::::::::::::://
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 1

    //:::::::::::::::Facebook Login::::::::::::::::://
    private var callbackManager: CallbackManager? = null
    var callback: FacebookCallback<LoginResult>? = null
    var strToken = ""
    var getUserLoginType = "0"
    override fun setLayout(): View = loginBinding.root
    override fun initView() {
        loginBinding = ActLoginBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this@ActLogin)
        strToken = FirebaseInstanceId.getInstance().token.toString()
        Log.d("Token-->", strToken)
        Common.getLog("Token== ", strToken)
        getUserLoginType =
            SharePreference.getStringPref(this@ActLogin, SharePreference.UserLoginType) ?: ""
        loginBinding.btnLogin.setOnClickListener { login() }
        loginBinding.tvSignUp.setOnClickListener {
            openActivity(ActSignUp::class.java)
        }
        //:::::::::::::::Google Login::::::::::::::::://
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        loginBinding.googleSignUp.setOnClickListener(View.OnClickListener {
            if (Common.isCheckNetwork(this@ActLogin)) {
                mGoogleSignInClient!!.signOut()
                    .addOnCompleteListener(this, object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            signInGoogle()
                        }
                    })
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActLogin,
                    resources.getString(R.string.no_internet)
                )
            }
        })
        loginBinding.facebookSignUp.setOnClickListener(View.OnClickListener {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut()
            }
            LoginManager
                .getInstance()
                .logInWithReadPermissions(
                    this,
                    getFacebookPermissions()
                )
        })
        loginBinding.tvForgetPassword.setOnClickListener(View.OnClickListener {
            openActivity(
                ActForgotPassword::class.java
            )
        })
        loginBinding.tvSkip.setOnClickListener(View.OnClickListener {
            openActivity(ActMain::class.java)

        })
        //::::::::::::::Facebook Login::::::::::::::::://
        FacebookSdk.setApplicationId(resources.getString(R.string.facebook_id));
        FacebookSdk.sdkInitialize(this@ActLogin)
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    updateFacebookUI(loginResult)
                }

                override fun onCancel() {}
                override fun onError(error: FacebookException) {
                    Toast.makeText(applicationContext, "" + error.message, Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            nextGmailActivity(account)
        } catch (e: ApiException) {
            Log.e("Google Login", "signInResult:failed code=" + e.statusCode)
        }
    }


    @SuppressLint("HardwareIds")
    private fun nextGmailActivity(profile: GoogleSignInAccount?) {
        if (profile != null) {
            val loginType = "google"
            val FristName = profile.displayName
            val profileEmail = profile.email
            val profileId = profile.id
            loginApiCall(FristName!!, profileEmail!!, profileId!!, loginType, strToken)
        }
    }

    fun getFacebookPermissions(): List<String> {
        return listOf("email")
    }

    //::::::::::::::FacebookLogin:::::::::::::://
    private fun updateFacebookUI(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(
                    `object`: JSONObject,
                    response: GraphResponse?
                ) {
                    getFacebookData(`object`)
                }
            })
        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, last_name, email,age_range, gender, birthday, location"
        ) // Parámetros que pedimos a facebook
        request.parameters = parameters
        request.executeAsync()
    }

    private fun getFacebookData(`object`: JSONObject) {
        try {
            val profileId = `object`.getString("id")
            var name = ""
            if (`object`.has("first_name")) {
                name = `object`.getString("first_name")
            }
            if (`object`.has("last_name")) {
                name += " " + `object`.getString("last_name")
            }
            var email = ""
            if (`object`.has("email")) {
                email = `object`.getString("email")
            }
            val loginType = "facebook"
            loginApiCall(name, email, profileId, loginType, strToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun loginApiCall(
        name: String,
        email: String,
        profileId: String,
        loginType: String,
        strToken: String
    ) {
        val hasmap = HashMap<String, String>()
        hasmap["name"] = name
        hasmap["email"] = email
        hasmap["mobile"] = ""
        hasmap["token"] = strToken
        hasmap["login_type"] = loginType
        if (loginType == "google") {
            hasmap["google_id"] = profileId
            hasmap["facebook_id"] = ""
        } else {
            hasmap["facebook_id"] = profileId
            hasmap["google_id"] = ""
        }
        Common.showLoadingProgress(this@ActLogin)
        val call = ApiClient.getClient.setRegistration(hasmap)
        call.enqueue(object : Callback<RestResponse<RegistrationModel>> {
            override fun onResponse(
                call: Call<RestResponse<RegistrationModel>>,
                response: Response<RestResponse<RegistrationModel>>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: RestResponse<RegistrationModel> = response.body()!!
                    if (registrationResponse.getStatus().toString() == "1") {
                        Common.dismissLoadingProgress()
                        setProfileData(
                            registrationResponse.getData(),
                            registrationResponse.getMessage()
                        )
                    } else if (registrationResponse.getStatus().toString() == "0") {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActLogin,
                            registrationResponse.getMessage()
                        )
                    } else if (registrationResponse.getStatus().toString() == "2") {
                        Common.dismissLoadingProgress()
                        mGoToRegistration(name, email, profileId, loginType, strToken)
                    } else if (registrationResponse.getStatus().toString() == "3") {
                        Common.dismissLoadingProgress()
                        if (getUserLoginType == "1") {
                            startActivity(
                                Intent(
                                    this@ActLogin,
                                    ActOTPVerification::class.java
                                ).putExtra("email", registrationResponse.getMobile().toString())
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@ActLogin,
                                    ActOTPVerification::class.java
                                ).putExtra("email", email)
                            )
                        }
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status") == "3") {
                        Common.dismissLoadingProgress()
                        if (getUserLoginType == "1") {
                            startActivity(
                                Intent(
                                    this@ActLogin,
                                    ActOTPVerification::class.java
                                ).putExtra("email", error.getString("mobile").toString())
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@ActLogin,
                                    ActOTPVerification::class.java
                                ).putExtra("email", email)
                            )
                        }
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActLogin,
                            error.getString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<RegistrationModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActLogin,
                    resources.getString(R.string.error_msg)
                )
            }
        })

    }

    private fun login() {
        if (loginBinding.edtEmail.text.toString() == "") {
            Common.showErrorFullMsg(this@ActLogin, resources.getString(R.string.validation_all))
        } else if (!Common.isValidEmail(loginBinding.edtEmail.text.toString())) {
            Common.showErrorFullMsg(
                this@ActLogin,
                resources.getString(R.string.validation_valid_email)
            )
        } else if (loginBinding.edtPassword.text.toString() == "") {
            Common.showErrorFullMsg(this@ActLogin, resources.getString(R.string.validation_all))
        } else {
            val hasmap = HashMap<String, String>()
            hasmap["email"] = loginBinding.edtEmail.text.toString()
            hasmap["password"] = loginBinding.edtPassword.text.toString()
            hasmap["token"] = strToken
            if (Common.isCheckNetwork(this@ActLogin)) {
                callApiLogin(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActLogin,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    private fun callApiLogin(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActLogin)
        val call = ApiClient.getClient.getLogin(hasmap)
        call.enqueue(object : Callback<RestResponse<LoginModel>> {
            override fun onResponse(
                call: Call<RestResponse<LoginModel>>,
                response: Response<RestResponse<LoginModel>>
            ) {
                if (response.code() == 200) {
                    val loginResponce: RestResponse<LoginModel> = response.body()!!
                    if (loginResponce.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        val loginModel: LoginModel = loginResponce.getData()!!
                        SharePreference.setBooleanPref(this@ActLogin, SharePreference.isLogin, true)
                        SharePreference.setStringPref(
                            this@ActLogin,
                            SharePreference.userId,
                            loginModel.getId()!!
                        )
                        SharePreference.setStringPref(
                            this@ActLogin,
                            SharePreference.userMobile,
                            loginModel.getMobile()!!
                        )
                        SharePreference.setStringPref(
                            this@ActLogin,
                            SharePreference.userEmail,
                            loginModel.getEmail()!!
                        )
                        SharePreference.setStringPref(
                            this@ActLogin,
                            SharePreference.userName,
                            loginModel.getName()!!
                        )
                        SharePreference.setStringPref(
                            this@ActLogin,
                            SharePreference.userRefralCode,
                            loginModel.getReferralCode()!!
                        )
                        SharePreference.setStringPref(
                            this@ActLogin,
                            SharePreference.userProfile,
                            loginModel.getProfile()!!
                        )
                        val intent = Intent(this@ActLogin, ActMain::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent);
                        finish()
                        finishAffinity()
                    } else if (loginResponce.getStatus().equals("2")) {
                        Common.dismissLoadingProgress()

                        val otpController = if (SharePreference.getStringPref(
                                this@ActLogin,
                                SharePreference.UserLoginType
                            ) == "1"
                        ) {
                            loginBinding.edtEmail.text.toString()
                        } else {
                            loginBinding.edtEmail.text.toString()
                        }
                        startActivity(
                            Intent(this@ActLogin, ActOTPVerification::class.java).putExtra(
                                "email",
                                otpController
                            )
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    val status = error.getInt("status")
                    if (status == 2) {
                        Common.dismissLoadingProgress()
                        val otpController = if (SharePreference.getStringPref(
                                this@ActLogin,
                                SharePreference.UserLoginType
                            ) == "1"
                        ) {
                            loginBinding.edtEmail.text.toString()
                        } else {
                            loginBinding.edtEmail.text.toString()
                        }
                        startActivity(
                            Intent(
                                this@ActLogin,
                                ActOTPVerification::class.java
                            ).putExtra("email", otpController)
                        )
                    } else {
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(this@ActLogin, error.getString("message"))
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<LoginModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActLogin,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun mGoToRegistration(
        name: String,
        profileEmail: String,
        profileId: String,
        loginType: String,
        strToken: String
    ) {
        val intent = Intent(this@ActLogin, ActSignUp::class.java)
        intent.putExtra("name", name)
        intent.putExtra("profileEmail", profileEmail)
        intent.putExtra("profileId", profileId)
        intent.putExtra("loginType", loginType)
        intent.putExtra("strToken", strToken)
        startActivity(intent)
    }

    private fun setProfileData(dataResponse: RegistrationModel?, message: String?) {
        SharePreference.setBooleanPref(this@ActLogin, SharePreference.isLogin, true)
        SharePreference.setStringPref(
            this@ActLogin,
            SharePreference.userId,
            dataResponse?.getId().toString()
        )
        SharePreference.setStringPref(
            this@ActLogin,
            SharePreference.loginType,
            dataResponse?.getLoginType().toString()
        )
        SharePreference.setStringPref(
            this@ActLogin,
            SharePreference.userName,
            dataResponse?.getName().toString()
        )
        SharePreference.setStringPref(
            this@ActLogin,
            SharePreference.userMobile,
            dataResponse?.getMobile().toString()
        )
        SharePreference.setStringPref(
            this@ActLogin,
            SharePreference.userEmail,
            dataResponse?.getEmail().toString()
        )
        SharePreference.setStringPref(
            this@ActLogin,
            SharePreference.userProfile,
            dataResponse?.getProfile_image().toString()
        )
        SharePreference.setStringPref(
            this@ActLogin,
            SharePreference.userRefralCode,
            dataResponse?.getReferral_code().toString()
        )
        startActivity(Intent(this@ActLogin, ActMain::class.java))
        finish()
        finishAffinity()
    }

    override fun onBackPressed() {
        mExitDialog()
    }

    private fun mExitDialog() {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(this@ActLogin, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(this@ActLogin)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            val tvYes: TextView = mView.findViewById(R.id.tvYes)
            val tvNo: TextView = mView.findViewById(R.id.tvNo)
            val finalDialog: Dialog = dialog
            tvYes.setOnClickListener {
                finalDialog.dismiss()
                ActivityCompat.finishAfterTransition(this@ActLogin)
                ActivityCompat.finishAffinity(this@ActLogin);
                finish()
            }
            tvNo.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}