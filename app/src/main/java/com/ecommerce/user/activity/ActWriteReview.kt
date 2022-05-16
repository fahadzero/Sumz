package com.ecommerce.user.activity

import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActWriteReviewBinding
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActWriteReview : BaseActivity() {

    private lateinit var writeReviewBinding: ActWriteReviewBinding
    var ratting = ""
    override fun setLayout(): View = writeReviewBinding.root

    override fun initView() {
        writeReviewBinding = ActWriteReviewBinding.inflate(layoutInflater)

        writeReviewBinding.ivBack.setOnClickListener { finish() }
        writeReviewBinding.tvproductname.text = intent.getStringExtra("proName")!!
        Glide.with(this@ActWriteReview)
            .load(intent.getStringExtra("proImage"))
            .into(writeReviewBinding.ivproduct)
        writeReviewBinding.btnsubmit.setOnClickListener {
            callApiAddRattingAndReview()
        }
    }

    private fun callApiAddRattingAndReview() {
        if (writeReviewBinding.edtreview.text.toString() == "") {
            Common.showErrorFullMsg(
                this@ActWriteReview,
                resources.getString(R.string.please_write_comment)
            )
        } else {
            Common.showLoadingProgress(this@ActWriteReview)
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] =
                SharePreference.getStringPref(this@ActWriteReview, SharePreference.userId)!!
            hasmap["ratting"] = ratting
            hasmap["comment"] = writeReviewBinding.edtreview.text.toString()
            hasmap["vendor_id"] = intent.getStringExtra("vendorsID")!!
            hasmap["product_id"] = intent.getStringExtra("proID")!!
            val call = ApiClient.getClient.addRatting(hasmap)
            call.enqueue(object : Callback<SingleResponse> {
                override fun onResponse(
                    call: Call<SingleResponse>,
                    response: Response<SingleResponse>
                ) {
                    if (response.code() == 200) {
                        Common.dismissLoadingProgress()
                        Common.isAddOrUpdated = true
                        if (response.body()?.status == 1) {
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Common.showErrorFullMsg(
                                this@ActWriteReview,
                                response.body()?.message.toString()
                            )
                        }
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActWriteReview,
                            resources.getString(R.string.error_msg)
                        )
                    }
                }
                override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActWriteReview,
                        resources.getString(R.string.error_msg)
                    )
                }
            })
        }
    }
}