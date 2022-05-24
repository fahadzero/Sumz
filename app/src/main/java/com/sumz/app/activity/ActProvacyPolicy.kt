package com.sumz.app.activity

import android.view.View
import com.sumz.app.R
import com.sumz.app.api.ApiClient
import com.sumz.app.base.BaseActivity
import com.sumz.app.databinding.ActProvacyPolicyBinding
import com.sumz.app.model.CmsPageResponse
import com.sumz.app.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActProvacyPolicy : BaseActivity() {
    private lateinit var actProvacyPolicyBinding: ActProvacyPolicyBinding


    override fun setLayout(): View = actProvacyPolicyBinding.root

    override fun initView() {
        actProvacyPolicyBinding = ActProvacyPolicyBinding.inflate(layoutInflater)
        actProvacyPolicyBinding.ivBack.setOnClickListener {
            finish()
        }
        when {
            intent.getStringExtra("Type") == "Policy" -> {
                actProvacyPolicyBinding.tvTitle.text = intent.getStringExtra("Type")
            }
            intent.getStringExtra("Type") == "About" -> {
                actProvacyPolicyBinding.tvTitle.text = intent.getStringExtra("Type")
            }
            intent.getStringExtra("Type") == "Terms Condition" -> {
                actProvacyPolicyBinding.tvTitle.text = intent.getStringExtra("Type")
            }
        }
        callCmsDataApi()
    }

    private fun callCmsDataApi() {
        Common.showLoadingProgress(this@ActProvacyPolicy)
        val call = ApiClient.getClient.getCmsData()
        call.enqueue(object : Callback<CmsPageResponse> {
            override fun onResponse(
                call: Call<CmsPageResponse>,
                response: Response<CmsPageResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    Common.dismissLoadingProgress()
                    if (restResponce.status == 1) {
                        when {
                            intent.getStringExtra("Type") == "Policy" -> {
                                actProvacyPolicyBinding.tvCmsData.text = restResponce.privacypolicy
                            }
                            intent.getStringExtra("Type") == "About" -> {
                                actProvacyPolicyBinding.tvCmsData.text = restResponce.about
                            }
                            intent.getStringExtra("Type") == "Terms Condition" -> {
                                actProvacyPolicyBinding.tvCmsData.text =
                                    restResponce.termsconditions
                            }
                        }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActProvacyPolicy,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<CmsPageResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActProvacyPolicy,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}