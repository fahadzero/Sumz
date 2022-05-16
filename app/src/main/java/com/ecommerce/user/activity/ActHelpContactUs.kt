package com.ecommerce.user.activity

import android.view.View
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActHelpContactUsBinding
import com.ecommerce.user.model.ContactInfo
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ActHelpContactUs : BaseActivity() {
    var contactInfo = ContactInfo()
    private lateinit var actHelpContactUsBinding: ActHelpContactUsBinding

    override fun setLayout(): View = actHelpContactUsBinding.root

    override fun initView() {
        actHelpContactUsBinding = ActHelpContactUsBinding.inflate(layoutInflater)
        val bundle = intent.extras
        contactInfo = bundle?.getSerializable("contact") as ContactInfo
        actHelpContactUsBinding.tvPhoneNumber.text = contactInfo.contact
        actHelpContactUsBinding.tvAddress.text = contactInfo.address
        actHelpContactUsBinding.tvEmailAddress.text = contactInfo.email
        actHelpContactUsBinding.ivBack.setOnClickListener { finish() }
        actHelpContactUsBinding.btnSubmit.setOnClickListener {
            when {
                actHelpContactUsBinding.tvFirstName.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                        this@ActHelpContactUs,
                        resources.getString(R.string.validation_all)
                    )
                }
                actHelpContactUsBinding.tvLastName.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                        this@ActHelpContactUs,
                        resources.getString(R.string.validation_all)
                    )
                }
                actHelpContactUsBinding.edMobile.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                        this@ActHelpContactUs,
                        resources.getString(R.string.validation_all)
                    )
                }
                actHelpContactUsBinding.edEmail.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                        this@ActHelpContactUs,
                        resources.getString(R.string.validation_all)
                    )
                }

                actHelpContactUsBinding.edSubject.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                        this@ActHelpContactUs,
                        resources.getString(R.string.validation_all)
                    )
                }
                actHelpContactUsBinding.edMessage.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                        this@ActHelpContactUs,
                        resources.getString(R.string.validation_all)
                    )
                }
                else -> {
                    val map = HashMap<String, String>()
                    map["user_id"] =
                        SharePreference.getStringPref(this@ActHelpContactUs, SharePreference.userId)
                            ?: ""
                    map["first_name"] = actHelpContactUsBinding.tvFirstName.text.toString()
                    map["last_name"] = actHelpContactUsBinding.tvLastName.text.toString()
                    map["mobile"] = actHelpContactUsBinding.edMobile.text.toString()
                    map["email"] = actHelpContactUsBinding.edEmail.text.toString()
                    map["subject"] = actHelpContactUsBinding.edSubject.text.toString()
                    map["message"] = actHelpContactUsBinding.edMessage.text.toString()
                    callHelp(map)
                }
            }
        }
    }

    private fun callHelp(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActHelpContactUs)
        val call = ApiClient.getClient.help(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    Common.isAddOrUpdated = true
                    if (response.body()?.status == 1) {
                        Common.showSuccessFullMsg(
                            this@ActHelpContactUs,
                            response.body()?.message.toString()
                        )
                        actHelpContactUsBinding.tvFirstName.text.clear()
                        actHelpContactUsBinding.tvLastName.text.clear()
                        actHelpContactUsBinding.edEmail.text.clear()
                        actHelpContactUsBinding.edMobile.text.clear()
                        actHelpContactUsBinding.edSubject.text.clear()
                        actHelpContactUsBinding.edMessage.text.clear()
                    } else {
                        Common.showErrorFullMsg(
                            this@ActHelpContactUs,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActHelpContactUs,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActHelpContactUs,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}