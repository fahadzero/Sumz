package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActRetuenRequestBinding
import com.ecommerce.user.databinding.RowReturnconditionsBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActRetuenRequest : BaseActivity() {
    private lateinit var retuenRequestBinding: ActRetuenRequestBinding
    var orderReturnrequestDetailsList: OrderRetuenRequestOrderInfo? = null
    var currency: String = ""
    private var pos = 0
    private var retuencon = ""

    var currencyPosition: String = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun setLayout(): View = retuenRequestBinding.root

    override fun initView() {
        retuenRequestBinding = ActRetuenRequestBinding.inflate(layoutInflater)
        retuenRequestBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        if (Common.isCheckNetwork(this@ActRetuenRequest)) {
            if (SharePreference.getBooleanPref(this@ActRetuenRequest, SharePreference.isLogin)) {
                callApiOrderReturnRequestDetail()
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActRetuenRequest,
                resources.getString(R.string.no_internet)
            )
        }
        currency = SharePreference.getStringPref(this@ActRetuenRequest, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActRetuenRequest,
                SharePreference.CurrencyPosition
            )!!

        retuenRequestBinding.btnreturnrequest.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActRetuenRequest, SharePreference.isLogin)) {
                callApireturnRequest()
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        }
    }

    //TODO API RETURN ORDER REQUEST CALL
    private fun callApireturnRequest() {
        if (retuencon == "") {
            Common.showErrorFullMsg(
                this@ActRetuenRequest,
                resources.getString(R.string.return_conditions_selection_error)
            )
        } else {
            Common.showLoadingProgress(this@ActRetuenRequest)
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] =
                SharePreference.getStringPref(this@ActRetuenRequest, SharePreference.userId)!!
            hasmap["order_id"] = intent.getStringExtra("order_id")!!
            hasmap["return_reason"] = retuencon
            hasmap["comment"] = retuenRequestBinding.edtReturnComments.text.toString()
            hasmap["status"] = "7"
            val call = ApiClient.getClient.returnRequest(hasmap)
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
                                this@ActRetuenRequest,
                                response.body()?.message.toString()
                            )
                        }
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActRetuenRequest,
                            resources.getString(R.string.error_msg)
                        )
                    }
                }

                override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActRetuenRequest,
                        resources.getString(R.string.error_msg)
                    )

                }
            })
        }
    }

    //TODO API ORDER RETURN REQUEST DETAILS CALL
    private fun callApiOrderReturnRequestDetail() {
        Common.showLoadingProgress(this@ActRetuenRequest)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActRetuenRequest, SharePreference.userId)!!
        hasmap["order_id"] = intent.getStringExtra("order_id")!!
        val call = ApiClient.getClient.getOrderReturnRequest(hasmap)
        call.enqueue(object : Callback<OrderRetuenRequestResponse> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<OrderRetuenRequestResponse>,
                response: Response<OrderRetuenRequestResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        orderReturnrequestDetailsList = restResponce.orderInfo
                        loadOrderReturnRequestDetails(orderReturnrequestDetailsList!!)
                        restResponce.data?.let { loadReturnConditions(it) }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActRetuenRequest,
                            restResponce.message.toString()
                        )
                    }
                }
            }
            override fun onFailure(call: Call<OrderRetuenRequestResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActRetuenRequest,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET ORDER RETURN DATA
    private fun loadReturnConditions(orderReturnList: ArrayList<OrderRetuenRequestDataItem>) {
        lateinit var binding: RowReturnconditionsBinding
        val returnAdapter = object :
            BaseAdaptor<OrderRetuenRequestDataItem, RowReturnconditionsBinding>(
                this@ActRetuenRequest,
                orderReturnList
            ) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: OrderRetuenRequestDataItem,
                position: Int
            ) {
                binding.tvreturncon.text = orderReturnList[position].returnConditions
                holder?.itemView?.setOnClickListener {
                    for (item in orderReturnList) {
                        item.isSelect = false
                    }
                    retuencon = orderReturnList[position].returnConditions.toString()
                    orderReturnList[position].isSelect = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadReturnConditions(
                            orderReturnList
                        )
                    }, 10L)
                }
                if (orderReturnList[position].isSelect == true) {
                    binding.ivCheck.visibility = View.VISIBLE
                } else {
                    binding.ivCheck.visibility = View.GONE
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_returnconditions
            }

            override fun getBinding(parent: ViewGroup): RowReturnconditionsBinding {
                binding = RowReturnconditionsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }
        retuenRequestBinding.rvreturntext.apply {
            layoutManager =
                LinearLayoutManager(this@ActRetuenRequest, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = returnAdapter

        }
    }

    //TODO SET ORDER RETURN REQUEST DETAILS DATA
    @SuppressLint("SetTextI18n")
    private fun loadOrderReturnRequestDetails(orderReturnrequestDetailsList: OrderRetuenRequestOrderInfo) {
        Glide.with(this@ActRetuenRequest)
            .load(orderReturnrequestDetailsList.imageUrl).into(retuenRequestBinding.ivCartitemm)
        retuenRequestBinding.ivCartitemm.setBackgroundColor(Color.parseColor(colorArray[pos % 6]))
        retuenRequestBinding.tvcartitemqty.text =
            "Qty: " + orderReturnrequestDetailsList.qty.toString()
        retuenRequestBinding.tvcateitemname.text = orderReturnrequestDetailsList.productName
        if (orderReturnrequestDetailsList.variation?.isEmpty() == true) {
            retuenRequestBinding.tvcartitemsize.text = "-"
        } else {
            retuenRequestBinding.tvcartitemsize.text =
                intent.getStringExtra("att")!! + ": " + orderReturnrequestDetailsList.variation
        }
        if (currencyPosition == "left") {

            retuenRequestBinding.tvcartitemprice.text =
                currency.plus(
                    String.format(
                        Locale.US,
                        "%,.2f",
                        orderReturnrequestDetailsList.price!!.toDouble()
                    )
                )
        } else {
            retuenRequestBinding.tvcartitemprice.text =
                (String.format(
                    Locale.US,
                    "%,.2f",
                    orderReturnrequestDetailsList.price!!.toDouble()
                )) + "" + currency
        }
    }
}