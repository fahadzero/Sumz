package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActReviewsBinding
import com.ecommerce.user.databinding.RowReviewsBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActReviews : BaseActivity() {

    private lateinit var reviewsBinding: ActReviewsBinding
    var productDetailsReviews: Reviews? = null

    override fun setLayout(): View = reviewsBinding.root

    override fun initView() {
        reviewsBinding = ActReviewsBinding.inflate(layoutInflater)
        if (Common.isCheckNetwork(this@ActReviews)) {
            callApiProductReview(intent.getStringExtra("product_id")!!)
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActReviews,
                resources.getString(R.string.no_internet))
        }
        reviewsBinding.ivreviews.setOnClickListener { openActivity(ActWriteReview::class.java) }

        reviewsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
    }

    //TODO CALL PRODUCT REVIEW API
    private fun callApiProductReview(productId: String) {
        Common.showLoadingProgress(this@ActReviews)
        val hasmap = HashMap<String, String>()
        hasmap["product_id"] = productId
        val call = ApiClient.getClient.getProductReview(hasmap)
        call.enqueue(object : Callback<ProductReviewResponse> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<ProductReviewResponse>,
                response: Response<ProductReviewResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        productDetailsReviews = restResponce.reviews
                        loadProductReview(productDetailsReviews!!)
                        restResponce.allReview?.data?.let { loadProductReviewData(it) }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActReviews,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ProductReviewResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActReviews,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO PRODUCT REVIEW DATA SET
    private fun loadProductReviewData(reviewsUserdata: ArrayList<ReviewDataItem>) {
        lateinit var binding: RowReviewsBinding
        val viewAllUserReviews =
            object : BaseAdaptor<ReviewDataItem, RowReviewsBinding>(
                this@ActReviews,
                reviewsUserdata
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ReviewDataItem,
                    position: Int
                ) {
                    binding.tvreviewsname.text = reviewsUserdata[position].users?.name
                    binding.tvreviewsdate.text = reviewsUserdata[position].date?.let {
                        Common.getDate(
                            it
                        )
                    }
                    binding.tvreviewsdesc.text = reviewsUserdata[position].comment
                    if (reviewsUserdata[position].ratting == "0" || reviewsUserdata[position].ratting == "0.5") {
                        binding.ivRatting.setImageResource(R.drawable.ratting0)
                    } else if (reviewsUserdata[position].ratting == "1" || reviewsUserdata[position].ratting == "1.5") {
                        binding.ivRatting.setImageResource(R.drawable.ratting1)
                        binding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
                    } else if (reviewsUserdata[position].ratting == "2" || reviewsUserdata[position].ratting == "2.5") {
                        binding.ivRatting.setImageResource(R.drawable.ratting2)
                        binding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
                    } else if (reviewsUserdata[position].ratting == "3" || reviewsUserdata[position].ratting == "3.5") {
                        binding.ivRatting.setImageResource(R.drawable.ratting3)
                        binding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
                    } else if (reviewsUserdata[position].ratting == "4" || reviewsUserdata[position].ratting == "4.5") {
                        binding.ivRatting.setImageResource(R.drawable.ratting4)
                        binding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
                    } else if (reviewsUserdata[position].ratting == "5") {
                        binding.ivRatting.setImageResource(R.drawable.ratting5)
                        binding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
                    } else {
                        binding.ivRatting.setImageResource(R.drawable.ratting0)
                    }
                    Glide.with(this@ActReviews)
                        .load(reviewsUserdata[position].users?.imageUrl).placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.profile,
                                null
                            )
                        ).into(binding.ivusershop)
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_reviews
                }
                override fun getBinding(parent: ViewGroup): RowReviewsBinding {
                    binding = RowReviewsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        reviewsBinding.rvReviewUserData.apply {
            if (reviewsUserdata.size > 0) {
                reviewsBinding.rvReviewUserData.visibility = View.VISIBLE
                reviewsBinding.tvNoDataFound.visibility = View.GONE
                reviewsBinding.cvReviews.visibility = View.VISIBLE
                layoutManager =
                    LinearLayoutManager(this@ActReviews, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = viewAllUserReviews
            } else {
                reviewsBinding.rvReviewUserData.visibility = View.GONE
                reviewsBinding.tvNoDataFound.visibility = View.VISIBLE
                reviewsBinding.cvReviews.visibility = View.GONE
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private fun loadProductReview(productDetailsReviews: Reviews) {
        reviewsBinding.tvrate.text = productDetailsReviews.avgRatting + " /" + " 5"
        if (productDetailsReviews.avgRatting == "0.0" || productDetailsReviews.avgRatting == "0.5") {
            reviewsBinding.ivRatting.setImageResource(R.drawable.ratting0)
        } else if (productDetailsReviews.avgRatting == "1.0" || productDetailsReviews.avgRatting == "1.5") {
            reviewsBinding.ivRatting.setImageResource(R.drawable.ratting1)
            reviewsBinding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
        } else if (productDetailsReviews.avgRatting == "2.0" || productDetailsReviews.avgRatting == "2.5") {
            reviewsBinding.ivRatting.setImageResource(R.drawable.ratting2)
            reviewsBinding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
        } else if (productDetailsReviews.avgRatting == "3.0" || productDetailsReviews.avgRatting == "3.5") {
            reviewsBinding.ivRatting.setImageResource(R.drawable.ratting3)
            reviewsBinding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
        } else if (productDetailsReviews.avgRatting == "4.0" || productDetailsReviews.avgRatting == "4.5") {
            reviewsBinding.ivRatting.setImageResource(R.drawable.ratting4)
            reviewsBinding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
        } else if (productDetailsReviews.avgRatting == "5.0") {
            reviewsBinding.ivRatting.setImageResource(R.drawable.ratting5)
            reviewsBinding.ivRatting.setColorFilter(getColor(R.color.darkyellow))
        } else {
            reviewsBinding.ivRatting.setImageResource(R.drawable.ratting0)
        }
        reviewsBinding.tvReviews.text =
            "Based on" + " " + productDetailsReviews.total + " " + "Ratings" + "\n" + "& Reviews"
        reviewsBinding.progress1.max = productDetailsReviews.oneRatting.toString().toInt()
        reviewsBinding.progress2.max = productDetailsReviews.twoRatting.toString().toInt()
        reviewsBinding.progress3.max = productDetailsReviews.threeRatting.toString().toInt()
        reviewsBinding.progress4.max = productDetailsReviews.fourRatting.toString().toInt()
        reviewsBinding.progress5.max = productDetailsReviews.fiveRatting.toString().toInt()
    }

}