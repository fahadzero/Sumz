package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActReturnTrackOrderBinding
import com.ecommerce.user.model.TrackOrderInfo
import com.ecommerce.user.model.TrackOrderResponse
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.getDateTime
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActReturnTrackOrder : BaseActivity() {
    private lateinit var returntrackOrderBinding: ActReturnTrackOrderBinding
    var trackOrderDetailsList: TrackOrderInfo? = null
    var currency: String = ""
    var currencyPosition: String = ""
    private var pos = 0
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun setLayout(): View = returntrackOrderBinding.root

    override fun initView() {
        returntrackOrderBinding = ActReturnTrackOrderBinding.inflate(layoutInflater)
        returntrackOrderBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        if (Common.isCheckNetwork(this@ActReturnTrackOrder)) {
            if (SharePreference.getBooleanPref(this@ActReturnTrackOrder, SharePreference.isLogin)) {
                callApiReturnTrackDetail()
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActReturnTrackOrder,
                resources.getString(R.string.no_internet)
            )
        }
        currency =
            SharePreference.getStringPref(this@ActReturnTrackOrder, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActReturnTrackOrder,
                SharePreference.CurrencyPosition
            )!!
    }

    //TODO API ORDER RETURN TRACK CALL
    private fun callApiReturnTrackDetail() {
        Common.showLoadingProgress(this@ActReturnTrackOrder)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActReturnTrackOrder, SharePreference.userId)!!
        hasmap["order_id"] = intent.getStringExtra("orderId")!!
        val call = ApiClient.getClient.getTrackOrder(hasmap)
        call.enqueue(object : Callback<TrackOrderResponse> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<TrackOrderResponse>,
                response: Response<TrackOrderResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        trackOrderDetailsList = restResponce.orderInfo
                        loadTrackOrderDetails(trackOrderDetailsList!!)
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActReturnTrackOrder,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<TrackOrderResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActReturnTrackOrder,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadTrackOrderDetails(trackOrderDetailsList: TrackOrderInfo) {
        if (!trackOrderDetailsList.equals(0)) {
            returntrackOrderBinding.clordertrack.visibility = View.VISIBLE
            returntrackOrderBinding.ivOrderTrack.visibility = View.VISIBLE
            returntrackOrderBinding.tvTrackOrderqty.visibility = View.VISIBLE
            returntrackOrderBinding.tvordertrackname.visibility = View.VISIBLE
            returntrackOrderBinding.tvOrderProductSize.visibility = View.VISIBLE
            returntrackOrderBinding.tvcartitemprice.visibility = View.VISIBLE
            returntrackOrderBinding.tvcartqtytitle.visibility = View.VISIBLE
            returntrackOrderBinding.tvcartitemprice.visibility = View.VISIBLE
            Glide.with(this@ActReturnTrackOrder)
                .load(trackOrderDetailsList.imageUrl).into(returntrackOrderBinding.ivOrderTrack)
            returntrackOrderBinding.ivOrderTrack.setBackgroundColor(Color.parseColor(colorArray[pos % 6]))
            Log.e("Date", getDateTime(trackOrderDetailsList.createdAt.toString()))
            when (trackOrderDetailsList.status) {
                7 -> {
                    returntrackOrderBinding.ivReturnRequest.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.ivReturnRejected.setImageResource(R.drawable.ic_round)
                    returntrackOrderBinding.ivReturnCompleted.setImageResource(R.drawable.ic_round)
                    returntrackOrderBinding.view.setBackgroundColor(getColor(R.color.medium_gray))
                    returntrackOrderBinding.view1.setBackgroundColor(getColor(R.color.medium_gray))
                    returntrackOrderBinding.tvOrderReturnRequestDate.visibility = View.VISIBLE
                    returntrackOrderBinding.tvOrderReturnRequestDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderRejected.text = "Order Return accepted"
                    returntrackOrderBinding.tvOrderRejectedDateDesc.text =
                        "You order has been accepted"
                    returntrackOrderBinding.tvOrderRejectedDate.visibility = View.GONE
                    returntrackOrderBinding.tvOrderCompletedDate.visibility = View.GONE
                    returntrackOrderBinding.btnAddReview.visibility = View.GONE
                }
                8 -> {
                    returntrackOrderBinding.ivReturnRequest.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.ivReturnRejected.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.ivReturnCompleted.setImageResource(R.drawable.ic_round)
                    returntrackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                    returntrackOrderBinding.view1.setBackgroundColor(getColor(R.color.medium_gray))
                    returntrackOrderBinding.tvOrderReturnRequestDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderRejected.text = "Order Return accepted"
                    returntrackOrderBinding.tvOrderRejectedDateDesc.text =
                        "You order has been accepted"
                    returntrackOrderBinding.tvOrderRejectedDate.visibility = View.VISIBLE
                    returntrackOrderBinding.tvOrderRejectedDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderRejectedDate.visibility = View.VISIBLE
                    returntrackOrderBinding.tvOrderCompletedDate.visibility = View.GONE
                    returntrackOrderBinding.btnAddReview.visibility = View.GONE
                }
                9 -> {
                    returntrackOrderBinding.ivReturnRequest.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.ivReturnRejected.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.ivReturnCompleted.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                    returntrackOrderBinding.view1.setBackgroundColor(getColor(R.color.green))
                    returntrackOrderBinding.tvOrderRejectedDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderReturnRequestDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderCompletedDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderRejected.text = "Order Return accepted"
                    returntrackOrderBinding.tvOrderRejectedDateDesc.text =
                        "You order has been accepted"
                    returntrackOrderBinding.tvOrderReturnRequestDate.visibility = View.VISIBLE
                    returntrackOrderBinding.tvOrderCompletedDate.visibility = View.VISIBLE
                    returntrackOrderBinding.btnAddReview.visibility = View.GONE
                }
                10 -> {
                    returntrackOrderBinding.ivReturnRequest.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.ivReturnRejected.setImageResource(R.drawable.ic_green_round)
                    returntrackOrderBinding.ivReturnCompleted.setImageResource(R.drawable.ic_round)
                    returntrackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                    returntrackOrderBinding.view1.setBackgroundColor(getColor(R.color.medium_gray))
                    returntrackOrderBinding.tvOrderReturnRequestDate.visibility = View.VISIBLE
                    returntrackOrderBinding.tvOrderReturnRequestDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderRejected.text = "Order Return Rejected"
                    returntrackOrderBinding.tvOrderRejected.setTextColor(getColor(R.color.red))
                    returntrackOrderBinding.tvOrderRejectedDateDesc.text =
                        "You order has been Rejected"
                    returntrackOrderBinding.tvOrderRejectedDate.visibility = View.VISIBLE
                    returntrackOrderBinding.tvOrderRejectedDate.text =
                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
                    returntrackOrderBinding.tvOrderCompletedDate.visibility = View.GONE
                    returntrackOrderBinding.btnAddReview.visibility = View.GONE
                    returntrackOrderBinding.tvvendorscomment.visibility = View.VISIBLE
                    returntrackOrderBinding.tvcommentsdata.visibility = View.VISIBLE
                }
            }
            returntrackOrderBinding.tvTrackOrderqty.text = trackOrderDetailsList.qty.toString()
            returntrackOrderBinding.tvordertrackname.text = trackOrderDetailsList.productName
            if (trackOrderDetailsList.vendorComment == null) {
                returntrackOrderBinding.tvvendorscomment.visibility = View.GONE
                returntrackOrderBinding.tvcommentsdata.visibility = View.GONE
            } else {
                returntrackOrderBinding.tvvendorscomment.visibility = View.VISIBLE
                returntrackOrderBinding.tvcommentsdata.visibility = View.VISIBLE
                returntrackOrderBinding.tvcommentsdata.text = trackOrderDetailsList.vendorComment
            }
            returntrackOrderBinding.tvReturnOrderId.text =
                "Return Order id #" + trackOrderDetailsList.returnNumber
            if (trackOrderDetailsList.variation?.isEmpty() == true) {
                returntrackOrderBinding.tvOrderProductSize.text = "-"
            } else {
                returntrackOrderBinding.tvOrderProductSize.text =
                    "Size : " + trackOrderDetailsList.variation
            }
            if (currencyPosition == "left") {

                returntrackOrderBinding.tvcartitemprice.text =
                    currency.plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            trackOrderDetailsList.price!!.toDouble()
                        )
                    )
            } else {
                returntrackOrderBinding.tvcartitemprice.text =
                    (String.format(
                        Locale.US,
                        "%,.2f",
                        trackOrderDetailsList.price!!.toDouble()
                    )) + "" + currency
            }
        } else {
            returntrackOrderBinding.clordertrack.visibility = View.GONE
            returntrackOrderBinding.ivOrderTrack.visibility = View.GONE
            returntrackOrderBinding.tvTrackOrderqty.visibility = View.GONE
            returntrackOrderBinding.tvordertrackname.visibility = View.GONE
            returntrackOrderBinding.tvOrderProductSize.visibility = View.GONE
            returntrackOrderBinding.tvcartitemprice.visibility = View.GONE
            returntrackOrderBinding.tvcartqtytitle.visibility = View.GONE
            returntrackOrderBinding.btnAddReview.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        callApiReturnTrackDetail()
    }

}