package com.ecommerce.user.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActEditProfileBinding
import com.ecommerce.user.model.GetProfileResponse
import com.ecommerce.user.model.UserData
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.isCheckNetwork
import com.ecommerce.user.utils.Common.isProfileEdit
import com.ecommerce.user.utils.Common.isProfileMainEdit
import com.ecommerce.user.utils.Common.setImageUpload
import com.ecommerce.user.utils.Common.setRequestBody
import com.ecommerce.user.utils.Common.showErrorFullMsg
import com.ecommerce.user.utils.Common.showLoadingProgress
import com.ecommerce.user.utils.SharePreference
import com.github.dhaval2404.imagepicker.ImagePicker
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class ActEditProfile : BaseActivity() {

    private lateinit var editProfileBinding: ActEditProfileBinding
    private var imageFile: File? = null
    override fun setLayout(): View = editProfileBinding.root

    override fun initView() {
        editProfileBinding = ActEditProfileBinding.inflate(layoutInflater)
        if (isCheckNetwork(this@ActEditProfile)) {
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] =
                SharePreference.getStringPref(this@ActEditProfile, SharePreference.userId)!!
            callApiProfile(hasmap)
        } else {
            alertErrorOrValidationDialog(
                this@ActEditProfile,
                resources.getString(R.string.no_internet)
            )
        }
        initClickListeners()
    }

    private fun initClickListeners() {
        editProfileBinding.ivGellary.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .saveDir(
                    File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "ServiceProvider"
                    )
                )
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        editProfileBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        editProfileBinding.btnsave.setOnClickListener {
            if (editProfileBinding.edtName.text.toString() == "") {
                showErrorFullMsg(
                    this@ActEditProfile,
                    resources.getString(R.string.validation_all)
                )
            } else {
                if (isCheckNetwork(this@ActEditProfile)) {
                    callApiEditProfile()
                } else {
                    alertErrorOrValidationDialog(
                        this@ActEditProfile,
                        resources.getString(R.string.no_internet)
                    )
                }
            }

        }
    }

    //TODO API EDIT PROFILE CALL
    private fun callApiEditProfile() {
        showLoadingProgress(this@ActEditProfile)
        var call: Call<SingleResponse>? = null
        call = if (imageFile != null) {
            ApiClient.getClient.setProfile(
                setRequestBody(
                    SharePreference.getStringPref(
                        this@ActEditProfile,
                        SharePreference.userId
                    )!!
                ),
                setRequestBody(editProfileBinding.edtName.text.toString()),
                setImageUpload("image", imageFile!!)
            )
        } else {
            ApiClient.getClient.setProfile(
                setRequestBody(
                    SharePreference.getStringPref(
                        this@ActEditProfile,
                        SharePreference.userId
                    )!!
                ), setRequestBody(editProfileBinding.edtName.text.toString()), null
            )
        }
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val editProfileResponce: SingleResponse = response.body()!!
                    successfulDialog(this@ActEditProfile, editProfileResponce.message)
                    if (editProfileResponce.status?.equals("1") == true) {
                        dismissLoadingProgress()
                        isProfileEdit = true
                        isProfileMainEdit = true
                    } else if (editProfileResponce.status?.equals("0") == true) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActEditProfile,
                            editProfileResponce.message
                        )
                    }
                } else {
                    val restResponse = response.errorBody()!!.string()
                    val jsonObject = JSONObject(restResponse)
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@ActEditProfile,
                        jsonObject.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActEditProfile,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    Log.e("FilePath", fileUri.path.toString())
                    fileUri.path.let { imageFile = File(it) }
                    Log.e("imageFileLength", imageFile!!.length().toString())
                    Glide.with(this@ActEditProfile).load(fileUri.path)
                        .into(editProfileBinding.ivProfile)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        this@ActEditProfile,
                        ImagePicker.getError(data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    //TODO API PROFILE CALL
    private fun callApiProfile(hasmap: HashMap<String, String>) {
        showLoadingProgress(this@ActEditProfile)
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<GetProfileResponse> {
            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: GetProfileResponse = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        val dataResponse: UserData = restResponce.data!!
                        setProfileData(dataResponse)
                    } else if (restResponce.data!!.equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActEditProfile,
                            restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActEditProfile,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET PROFILE DATA
    private fun setProfileData(dataResponse: UserData) {
        editProfileBinding.edtEmail.setText(dataResponse.email)
        editProfileBinding.edtName.setText(dataResponse.name)
        editProfileBinding.edMobileNumber.setText(dataResponse.mobile)
        Glide.with(this@ActEditProfile).load(dataResponse.profilePic).placeholder(
            ResourcesCompat.getDrawable(resources, R.drawable.profile, null)
        ).into(editProfileBinding.ivProfile)
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActEditProfile, false)
    }

    //TODO PRODFILE UPDATE SUCCESS DIALOG
    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
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
                setResult(RESULT_OK)
                finish()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}