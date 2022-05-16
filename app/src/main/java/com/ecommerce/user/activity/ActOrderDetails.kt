package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActOrderDetailsBinding
import com.ecommerce.user.databinding.RemoveItemDialogBinding
import com.ecommerce.user.databinding.RowOrderdetailsproductBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActOrderDetails : BaseActivity() {
    private lateinit var orderDetailsBinding: ActOrderDetailsBinding
    var currency: String = ""
    var currencyPosition: String = ""
    var orderDetailsList: OrderInfo? = null
    var orderstatus: String = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun setLayout(): View = orderDetailsBinding.root

    override fun initView() {
        orderDetailsBinding = ActOrderDetailsBinding.inflate(layoutInflater)
        if (Common.isCheckNetwork(this@ActOrderDetails)) {
            if (SharePreference.getBooleanPref(this@ActOrderDetails, SharePreference.isLogin)) {
                callApiOrderDetail()
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActOrderDetails,
                resources.getString(R.string.no_internet)
            )
        }
        currency = SharePreference.getStringPref(this@ActOrderDetails, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActOrderDetails,
                SharePreference.CurrencyPosition
            )!!
        orderDetailsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
    }

    //TODO API ORDER DETAILS CALL
    private fun callApiOrderDetail() {
        Common.showLoadingProgress(this@ActOrderDetails)
        val hasmap = HashMap<String, String>()
        hasmap["order_number"] = intent.getStringExtra("order_number")!!
        val call = ApiClient.getClient.getOrderDetails(hasmap)
        call.enqueue(object : Callback<OrderDetailsResponse> {
            override fun onResponse(
                call: Call<OrderDetailsResponse>,
                response: Response<OrderDetailsResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        orderDetailsList = restResponce.orderInfo
                        loadOrderDetails(orderDetailsList!!)
                        restResponce.orderData?.let { loadOrderProductDetails(it) }


                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActOrderDetails,
                            restResponce.message.toString()
                        )
                    }
                }
            }


            override fun onFailure(call: Call<OrderDetailsResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActOrderDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET ORDER PRODUCT DETAILS DATA
    private fun loadOrderProductDetails(orderData: ArrayList<OrderDataItem>) {
        lateinit var binding: RowOrderdetailsproductBinding
        val viewAllDataAdapter =
            object : BaseAdaptor<OrderDataItem, RowOrderdetailsproductBinding>(
                this@ActOrderDetails,
                orderData
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n", "InflateParams",
                    "UseCompatLoadingForDrawables"
                )
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderDataItem,
                    position: Int
                ) {
                    Glide.with(this@ActOrderDetails)
                        .load(orderData[position].imageUrl).into(binding.ivCartitemm)
                    binding.ivCartitemm.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    binding.tvcateitemname.text = orderData[position].productName
                    binding.tvcartitemqty.text = "Qty: " + orderData[position].qty.toString()
                    val qty = orderData[position].qty
                    val price = orderData[position].price?.toDouble()
                    val totalpriceqty = price!! * qty!!
                    if (orderData[position].variation == null) {
                        binding.tvcartitemsize.text = "-"
                    } else {
                        binding.tvcartitemsize.text =
                            orderData[position].attribute + ": " + orderData[position].variation
                    }
                    if (orderstatus == "5" || orderstatus == "7") {
                        binding.swipe.isSwipeEnabled = false
                    }
                    when (orderstatus) {
                        "5" -> {
                            binding.swipe.isSwipeEnabled
                            binding.tvorderstatus.visibility = View.VISIBLE
                            binding.tvorderstatus.text = getString(R.string.order_cancelled)
                        }
                        "7" -> {
                            binding.swipe.isSwipeEnabled
                            binding.tvorderstatus.visibility = View.VISIBLE
                            binding.tvorderstatus.text = getString(R.string.return_request)
                        }
                        else -> {
                            binding.tvorderstatus.visibility = View.GONE
                        }
                    }

                    if (currencyPosition == "left") {

                        binding.tvcartitemprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    totalpriceqty
                                )
                            )
                        binding.tvshippingcost.text = currency.plus(
                            String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].shippingCost!!.toDouble()
                            )
                        )
                        binding.tvtax.text = currency.plus(
                            String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].tax!!.toString().toDouble()
                            )
                        )
                        if (orderData[position].discountAmount == null) {
                            binding.tvdiscout.text = currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    0.toDouble()
                                )
                            )
                        } else {
                            binding.tvdiscout.text = currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    orderData[position].discountAmount!!.toDouble()
                                )
                            )
                        }
                    } else {
                        binding.tvcartitemprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                totalpriceqty
                            )) + "" + currency
                        binding.tvshippingcost.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].shippingCost!!.toDouble()
                            )) + "" + currency
                        binding.tvtax.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].tax!!.toString().toDouble()
                            )) + "" + currency
                        if (orderData[position].discountAmount == null) {
                            binding.tvdiscout.text =
                                (String.format(
                                    Locale.US,
                                    "%,.2f",
                                    0.toDouble()
                                )) + "" + currency
                        } else {
                            binding.tvdiscout.text =
                                (String.format(
                                    Locale.US,
                                    "%,.2f",
                                    orderData[position].discountAmount!!.toDouble()
                                )) + "" + currency
                        }
                    }

                    binding.tvmore.setOnClickListener {
                        val dialog = BottomSheetDialog(this@ActOrderDetails)
                        if (Common.isCheckNetwork(this@ActOrderDetails)) {
                            val view = layoutInflater.inflate(R.layout.row_bottomsheetorderdetails, null)
                            dialog.window?.setBackgroundDrawable(getDrawable(R.color.tr))
                            val btnCancelorder = view.findViewById<TextView>(R.id.tvcancelorder)
                            val btnTrackorder = view.findViewById<TextView>(R.id.tvtrackorder)
                            val btnCancel = view.findViewById<TextView>(R.id.tvCancel)
                            val btnRetuenRequest = view.findViewById<TextView>(R.id.tvReturnReq)
                            val viewreturnrequest = view.findViewById<View>(R.id.view2)
                            if (orderstatus == "4") {
                                btnRetuenRequest.visibility = View.VISIBLE
                                viewreturnrequest.visibility = View.VISIBLE
                            }else{
                                btnRetuenRequest.visibility = View.GONE
                                viewreturnrequest.visibility = View.GONE
                            }
                            if (orderstatus == "7") {
                                btnRetuenRequest.visibility = View.GONE
                                viewreturnrequest.visibility = View.GONE
                            }
                            btnCancel.setOnClickListener {
                                dialog.dismiss()
                                onResume()
                            }
                            btnCancelorder.setOnClickListener {
                                cancelOrderDialog(orderData[position].id?:0)
                                dialog.dismiss()
                            }
                            btnRetuenRequest.setOnClickListener {
                                Log.e(
                                    "order_id--->",
                                    orderData[position].id.toString()
                                )
                                val intent =
                                    Intent(this@ActOrderDetails, ActRetuenRequest::class.java)
                                intent.putExtra(
                                    "order_id",
                                    orderData[position].id.toString()
                                )
                                intent.putExtra(
                                    "att",
                                    orderData[position].attribute.toString()
                                )
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            btnTrackorder.setOnClickListener {
                                Log.e(
                                    "order_id--->",
                                    orderData[position].id.toString()
                                )
                                val intent = Intent(this@ActOrderDetails, ActTrackOrder::class.java)
                                intent.putExtra(
                                    "order_id",
                                    orderData[position].id.toString()
                                )
                                intent.putExtra(
                                    "att",
                                    orderData[position].attribute.toString()
                                )
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            dialog.setCancelable(false)
                            dialog.setContentView(view)
                            dialog.show()
                        } else {
                            Common.alertErrorOrValidationDialog(
                                this@ActOrderDetails,
                                resources.getString(R.string.no_internet)
                            )
                            dialog.dismiss()
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_orderdetailsproduct
                }

                override fun getBinding(parent: ViewGroup): RowOrderdetailsproductBinding {
                    binding = RowOrderdetailsproductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        orderDetailsBinding.rvorderproduct.apply {
            if (orderData.size > 0) {
                orderDetailsBinding.rvorderproduct.visibility = View.VISIBLE
                orderDetailsBinding.tvNoDataFound.visibility = View.GONE
                layoutManager =
                    LinearLayoutManager(this@ActOrderDetails, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = viewAllDataAdapter
            } else {
                orderDetailsBinding.rvorderproduct.visibility = View.VISIBLE
                orderDetailsBinding.tvNoDataFound.visibility = View.GONE
            }
        }
    }


    fun cancelOrderDialog(orderId: Int) {
        val removeDialogBinding = RemoveItemDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this@ActOrderDetails)
        dialog.setContentView(removeDialogBinding.root)
        removeDialogBinding.tvRemoveTitle.text = resources.getString(R.string.cancel_product)
        removeDialogBinding.tvAlertMessage.text = resources.getString(R.string.cancel_product_desc)
        removeDialogBinding.btnProceed.setOnClickListener {
            if (Common.isCheckNetwork(this@ActOrderDetails)) {
                dialog.dismiss()
                cancelorder(orderId)
                callApiOrderDetail()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActOrderDetails,
                    resources.getString(R.string.no_internet)
                )
            }
        }
        removeDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //TODO API ORDER CANCEL CALL
    private fun cancelorder(id: Int?) {
        Common.showLoadingProgress(this@ActOrderDetails)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActOrderDetails, SharePreference.userId)!!
        hasmap["order_id"] = id.toString()
        hasmap["status"] = "5"
        val call = ApiClient.getClient.getCancelOrder(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        Common.isAddOrUpdated = true
                    } else {
                        Common.showErrorFullMsg(
                            this@ActOrderDetails,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@ActOrderDetails,
                        resources.getString(R.string.error_msg)
                    )
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActOrderDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET ORDER DETAILS DATA
    @SuppressLint("SetTextI18n")
    private fun loadOrderDetails(orderDetailsList: OrderInfo) {
        if (!orderDetailsList.equals(0)) {
            orderDetailsBinding.tvorderid.visibility = View.VISIBLE
            orderDetailsBinding.tvpaymenttype.visibility = View.VISIBLE
            orderDetailsBinding.tvorderdate.visibility = View.VISIBLE
            orderDetailsBinding.tvusermailid.visibility = View.VISIBLE
            orderDetailsBinding.tvphone.visibility = View.VISIBLE
            orderDetailsBinding.tvareaaddress.visibility = View.VISIBLE
            orderDetailsBinding.tvUserName.visibility = View.VISIBLE
            orderDetailsBinding.tvsubtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvtaxtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvshippingtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvdiscounttotal.visibility = View.VISIBLE
            orderDetailsBinding.tvorderid.text = orderDetailsList.orderNumber
            when (orderDetailsList.paymentType) {
                1 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Cash"
                }
                2 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Wallet"
                }
                3 -> {
                    orderDetailsBinding.tvpaymenttype.text = "RazorPay"
                }
                4 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Stripe"
                }
                5 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Flutterwave"
                }
                6 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Paystack"
                }
            }
            orderDetailsBinding.tvorderdate.text = orderDetailsList.date?.let { Common.getDate(it) }
            orderDetailsBinding.tvusermailid.text = orderDetailsList.email
            orderDetailsBinding.tvphone.text = orderDetailsList.mobile
            when {
                orderDetailsList.landmark == null -> {
                    orderDetailsBinding.tvareaaddress.text =
                        "" + orderDetailsList.streetAddress + "-" + orderDetailsList.pincode
                }
                orderDetailsList.streetAddress == null -> {
                    orderDetailsList.landmark + "-" + orderDetailsList.pincode
                }
                orderDetailsList.pincode == null -> {
                    orderDetailsList.landmark + " " + orderDetailsList.streetAddress
                }
                else -> {
                    orderDetailsBinding.tvareaaddress.text =
                        orderDetailsList.landmark + " " + orderDetailsList.streetAddress + "-" + orderDetailsList.pincode
                }
            }
            if (orderDetailsList.orderNotes == null) {
                orderDetailsBinding.clNote.visibility = View.GONE
            } else {
                orderDetailsBinding.edNote.setText(orderDetailsList.orderNotes)
            }
            orderstatus = orderDetailsList.status.toString()
            orderDetailsBinding.tvUserName.text = orderDetailsList.fullName
            if (currencyPosition == "left") {
                orderDetailsBinding.tvsubtotal.text =
                    currency.plus(
                        orderDetailsList.subtotal?.let {
                            String.format(
                                Locale.US,
                                "%,.2f",
                                it.toDouble()
                            )
                        }
                    )
                orderDetailsBinding.tvtaxtotal.text =
                    currency.plus(orderDetailsList.tax?.let {
                        String.format(
                            Locale.US,
                            "%,.2f",
                            it.toDouble()
                        )
                    })
                orderDetailsBinding.tvshippingtotal.text = currency.plus(
                    orderDetailsList.shippingCost?.let {
                        String.format(
                            Locale.US, "%,.2f",
                            it.toDouble()
                        )
                    }
                )
                if (orderDetailsList.discountAmount != null) {
                    orderDetailsBinding.tvdiscounttotal.text = currency.plus(
                        orderDetailsList.discountAmount.let {
                            String.format(
                                Locale.US, "%,.2f",
                                it.toDouble()
                            )
                        }
                    )
                } else {
                    orderDetailsBinding.tvdiscounttotal.text = currency + "0.00"
                }
                orderDetailsBinding.tvtotal.text = currency.plus(
                    orderDetailsList.grandTotal?.let {
                        String.format(
                            Locale.US, "%,.2f",
                            it.toDouble()
                        )
                    }
                )
            } else {
                orderDetailsBinding.tvsubtotal.text =
                    (String.format(
                        Locale.US,
                        "%,.2f",
                        orderDetailsList.subtotal!!.toDouble()
                    )) + "" + currency
                orderDetailsBinding.tvtaxtotal.text =
                    (String.format(
                        Locale.US,
                        "%,.2f",
                        orderDetailsList.tax!!.toString().toDouble()
                    )) + "" + currency
                orderDetailsBinding.tvshippingtotal.text = (orderDetailsList.shippingCost?.let {
                    String.format(
                        Locale.US,
                        "%,.2f",
                        it.toDouble()
                    )
                }) + "" + currency

                if (orderDetailsList.discountAmount != null) {
                    orderDetailsBinding.tvdiscounttotal.text =
                        (orderDetailsList.discountAmount.let {
                            String.format(
                                Locale.US,
                                "%,.2f",
                                it.toDouble()
                            )
                        }) + "" + currency
                } else {
                    orderDetailsBinding.tvdiscounttotal.text = "0.00$currency"
                }

                orderDetailsBinding.tvtotal.text = (orderDetailsList.grandTotal?.let {
                    String.format(
                        Locale.US,
                        "%,.2f",
                        it.toDouble()
                    )
                }) + "" + currency
            }
        } else {
            orderDetailsBinding.tvorderid.visibility = View.GONE
            orderDetailsBinding.tvpaymenttype.visibility = View.GONE
            orderDetailsBinding.tvorderdate.visibility = View.GONE
            orderDetailsBinding.tvusermailid.visibility = View.GONE
            orderDetailsBinding.tvphone.visibility = View.GONE
            orderDetailsBinding.tvareaaddress.visibility = View.GONE
            orderDetailsBinding.tvUserName.visibility = View.GONE
            orderDetailsBinding.tvsubtotal.visibility = View.GONE
            orderDetailsBinding.tvtaxtotal.visibility = View.GONE
            orderDetailsBinding.tvshippingtotal.visibility = View.GONE
            orderDetailsBinding.tvtotal.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        callApiOrderDetail()
    }
}