package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActTrackOrderBinding
import com.ecommerce.user.model.TrackOrderInfo
import com.ecommerce.user.model.TrackOrderResponse
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.getDateTime
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActTrackOrder : BaseActivity() {
    private lateinit var trackOrderBinding: ActTrackOrderBinding
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

    override fun setLayout(): View = trackOrderBinding.root

    override fun initView() {
        trackOrderBinding = ActTrackOrderBinding.inflate(layoutInflater)
        trackOrderBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        if (Common.isCheckNetwork(this@ActTrackOrder)) {
            if (SharePreference.getBooleanPref(this@ActTrackOrder, SharePreference.isLogin)) {
                callApiTrackDetail()
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActTrackOrder,
                resources.getString(R.string.no_internet)
            )
        }
        currency = SharePreference.getStringPref(this@ActTrackOrder, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActTrackOrder,
                SharePreference.CurrencyPosition
            )!!
    }


    //TODO CALL TRACK ORDER DETAIL API
    private fun callApiTrackDetail() {
        Common.showLoadingProgress(this@ActTrackOrder)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActTrackOrder, SharePreference.userId)!!
        hasmap["order_id"] = intent.getStringExtra("order_id")!!
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
                            this@ActTrackOrder,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<TrackOrderResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActTrackOrder,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO TRACK ORDER DETAILS DATA SET
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun loadTrackOrderDetails(trackOrderDetailsList: TrackOrderInfo) {
        if (!trackOrderDetailsList.equals(0)) {
            trackOrderBinding.clordertrack.visibility = View.VISIBLE
            trackOrderBinding.ivOrderTrack.visibility = View.VISIBLE
            trackOrderBinding.tvTrackOrderqty.visibility = View.VISIBLE
            trackOrderBinding.tvordertrackname.visibility = View.VISIBLE
            trackOrderBinding.tvOrderProductSize.visibility = View.VISIBLE
            trackOrderBinding.tvcartitemprice.visibility = View.VISIBLE
            trackOrderBinding.tvcartqtytitle.visibility = View.VISIBLE
            trackOrderBinding.tvcartitemprice.visibility = View.VISIBLE
            Glide.with(this@ActTrackOrder)
                .load(trackOrderDetailsList.imageUrl).into(trackOrderBinding.ivOrderTrack)
            trackOrderBinding.ivOrderTrack.setBackgroundColor(Color.parseColor(colorArray[pos % 6]))
            if (trackOrderDetailsList.status == 1) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.tvOrderPlacedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderPlacedDate.text =
                    getDateTime(trackOrderDetailsList.createdAt.toString())
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.GONE
                trackOrderBinding.tvOrderShippedDate.visibility = View.GONE
                trackOrderBinding.btnAddReview.visibility = View.GONE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.GONE
            } else if (trackOrderDetailsList.status == 2) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.tvOrderPlacedDate.text =
                    getDateTime(trackOrderDetailsList.createdAt.toString())
                trackOrderBinding.tvOrderConfirmedDate.text =
                    getDateTime(trackOrderDetailsList.confirmedAt.toString())
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderShippedDate.visibility = View.GONE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.GONE
                trackOrderBinding.btnAddReview.visibility = View.GONE
            } else if (trackOrderDetailsList.status == 3) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.tvOrderPlacedDate.text =
                    getDateTime(trackOrderDetailsList.createdAt.toString())
                trackOrderBinding.tvOrderShippedDate.text =
                    getDateTime(trackOrderDetailsList.shippedAt.toString())
                trackOrderBinding.tvOrderConfirmedDate.text =
                    getDateTime(trackOrderDetailsList.confirmedAt.toString())
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderShippedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.GONE
                trackOrderBinding.btnAddReview.visibility = View.GONE
            } else if (trackOrderDetailsList.status == 4 || trackOrderDetailsList.status == 7 ||
                trackOrderDetailsList.status == 8 || trackOrderDetailsList.status == 9 || trackOrderDetailsList.status == 10
            ) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.tvOrderPlacedDate.text =
                    getDateTime(trackOrderDetailsList.createdAt.toString())
                trackOrderBinding.tvOrderShippedDate.text =
                    getDateTime(trackOrderDetailsList.shippedAt.toString())
                trackOrderBinding.tvOrderConfirmedDate.text =
                    getDateTime(trackOrderDetailsList.confirmedAt.toString())
                trackOrderBinding.tvOrderDeliveryDate.text =
                    getDateTime(trackOrderDetailsList.deliveredAt.toString())
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderShippedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.VISIBLE
                trackOrderBinding.btnAddReview.visibility = View.VISIBLE
                trackOrderBinding.btnAddReview.setOnClickListener {
                    val intent = Intent(this@ActTrackOrder, ActWriteReview::class.java)
                    intent.putExtra("proName", trackOrderDetailsList.productName.toString())
                    intent.putExtra("proID", trackOrderDetailsList.productId.toString())
                    intent.putExtra("vendorsID", trackOrderDetailsList.vendorId.toString())
                    intent.putExtra("proImage", trackOrderDetailsList.imageUrl.toString())
                    startActivity(intent)
                }
            }
            trackOrderBinding.tvTrackOrderqty.text = trackOrderDetailsList.qty.toString()
            trackOrderBinding.tvordertrackname.text = trackOrderDetailsList.productName
            if (trackOrderDetailsList.variation?.isEmpty() == true) {
                trackOrderBinding.tvOrderProductSize.text = "-"
            } else {
                trackOrderBinding.tvOrderProductSize.text =
                    intent.getStringExtra("att") ?: "Size" + ": " + trackOrderDetailsList.variation
            }
            if (currencyPosition == "left") {

                trackOrderBinding.tvcartitemprice.text =
                    currency.plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            trackOrderDetailsList.price!!.toDouble()
                        )
                    )
            } else {
                trackOrderBinding.tvcartitemprice.text =
                    (String.format(
                        Locale.US,
                        "%,.2f",
                        trackOrderDetailsList.price!!.toDouble()
                    )) + "" + currency
            }
        } else {
            trackOrderBinding.clordertrack.visibility = View.GONE
            trackOrderBinding.ivOrderTrack.visibility = View.GONE
            trackOrderBinding.tvTrackOrderqty.visibility = View.GONE
            trackOrderBinding.tvordertrackname.visibility = View.GONE
            trackOrderBinding.tvOrderProductSize.visibility = View.GONE
            trackOrderBinding.tvcartitemprice.visibility = View.GONE
            trackOrderBinding.tvcartqtytitle.visibility = View.GONE
            trackOrderBinding.btnAddReview.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        callApiTrackDetail()
    }
}

