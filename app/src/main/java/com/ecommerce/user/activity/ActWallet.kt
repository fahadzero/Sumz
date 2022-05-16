package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActWalletBinding
import com.ecommerce.user.databinding.RowTransactionhistoryBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActWallet : BaseActivity() {
    private lateinit var actWalletBinding: ActWalletBinding
    private var walletList = ArrayList<WalletDataItem>()
    private var viewAllDataAdapter: BaseAdaptor<WalletDataItem, RowTransactionhistoryBinding>? =
        null
    private var manager: LinearLayoutManager? = null
    private var currentPage = 1
    var total_pages: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisibleItems = 0
    var currency: String = ""
    var currencyPosition: String = ""
    var walletAmount: String = ""


    override fun setLayout(): View = actWalletBinding.root

    override fun initView() {
        actWalletBinding = ActWalletBinding.inflate(layoutInflater)
        actWalletBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        currency =
            SharePreference.getStringPref(this@ActWallet, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActWallet,
                SharePreference.CurrencyPosition
            )!!
        actWalletBinding.tvAddMoney.text = currency

        actWalletBinding.tvAddMoney.setOnClickListener {
            openActivity(ActAddMoney::class.java)
        }
        manager =
            LinearLayoutManager(this@ActWallet, LinearLayoutManager.VERTICAL, false)
        loadWalletDetails(walletList)
        if (Common.isCheckNetwork(this@ActWallet)) {
            if (SharePreference.getBooleanPref(this@ActWallet, SharePreference.isLogin)) {
                callApiWallet()
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActWallet,
                resources.getString(R.string.no_internet)
            )
        }
        actWalletBinding.rvtransactionhistory.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager!!.childCount
                    totalItemCount = manager!!.itemCount
                    pastVisibleItems = manager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            callApiWallet()
                        }
                    }
                }
            }
        })
    }

    //TODO CALL WALLET API
    private fun callApiWallet() {
        Common.showLoadingProgress(this@ActWallet)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActWallet, SharePreference.userId)!!
        val call = ApiClient.getClient.getWallet(currentPage.toString(), hasmap)
        call.enqueue(object : Callback<WalletResponse> {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onResponse(
                call: Call<WalletResponse>,
                response: Response<WalletResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == 1) {
                            walletList.clear()
                        }
                        restResponce.data?.data?.let {
                            walletList.addAll(it)
                        }
                        this@ActWallet.currentPage = restResponce.data?.currentPage!!.toInt()
                        this@ActWallet.total_pages = restResponce.data.lastPage!!.toInt()
                        walletAmount = restResponce.walletamount.toString()
                        if (walletAmount.isEmpty()) {
                            if (currencyPosition == "left") {
                                actWalletBinding.tvwalletamount.text =
                                    currency.plus(
                                        String.format(
                                            Locale.US,
                                            "%,.2f",
                                            0.toDouble()
                                        )
                                    )
                            } else {
                                actWalletBinding.tvwalletamount.text =
                                    (String.format(
                                        Locale.US,
                                        "%,.2f",
                                        0.toDouble()
                                    )) + "" + currency
                            }
                        } else {
                            if (currencyPosition == "left") {
                                actWalletBinding.tvwalletamount.text =
                                    currency.plus(
                                        String.format(
                                            Locale.US,
                                            "%,.2f",
                                            walletAmount.toDouble()
                                        )
                                    )
                            } else {
                                actWalletBinding.tvwalletamount.text =
                                    (String.format(
                                        Locale.US,
                                        "%,.2f",
                                        walletAmount.toDouble()
                                    )) + "" + currency
                            }
                        }
                        loadWalletDetails(walletList)
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActWallet,
                            restResponce.message.toString()
                        )
                    }
                }
            }
            override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActWallet,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO WALLET DATA SET
    private fun loadWalletDetails(walletList: ArrayList<WalletDataItem>) {
        lateinit var binding: RowTransactionhistoryBinding
        viewAllDataAdapter =
            object : BaseAdaptor<WalletDataItem, RowTransactionhistoryBinding>(
                this@ActWallet,
                walletList
            ) {
                @SuppressLint(
                    "NewApi", "ResourceType", "SetTextI18n",
                    "UseCompatLoadingForDrawables"
                )
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: WalletDataItem,
                    position: Int
                ) {
                    when (walletList[position].transactionType) {
                        "2" -> {
                            binding.tvwalletDetails.text = getString(R.string.order_confirmed)
                            binding.ivWallet.setImageResource(R.drawable.ic_orderconfirmed)
                            binding.clorder.background = getDrawable(R.drawable.round_green)
                            binding.tvwalletName.text="Order id: "+walletList[position].orderNumber
                        }
                        "1" -> {
                            binding.tvwalletDetails.text = getString(R.string.order_cancelled)
                            binding.ivWallet.setImageResource(R.drawable.ordercancelledpackage)
                            binding.clorder.background = getDrawable(R.drawable.round_red)
                            binding.tvwalletName.text="Order id: "+walletList[position].orderNumber
                        }
                        "4" -> {
                            binding.tvwalletName.text = getString(R.string.waller_recharge)
                            binding.tvwalletDetails.text = getString(R.string.order_cancelled)
                            binding.ivWallet.setImageResource(R.drawable.ic_wallet)
                            binding.clorder.background = getDrawable(R.drawable.round_darkpink)
                        }
                    }
                    when (walletList[position].type) {
                        "3" -> {
                            binding.tvwalletDetails.text=getString(R.string.razorpay)
                        }"4" -> {
                            binding.tvwalletDetails.text=getString(R.string.stirpe)
                        }"5" -> {
                            binding.tvwalletDetails.text=getString(R.string.flutterwave)
                        }"6" -> {
                            binding.tvwalletDetails.text=getString(R.string.paystack)
                        }
                    }
                    binding.tvwalletdate.text = walletList[position].date?.let {
                        Common.getDate(
                            it
                        )
                    }
                    if (currencyPosition == "left") {

                        binding.tvDeliveryprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    walletList[position].wallet!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvDeliveryprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                walletList[position].wallet!!.toDouble()
                            )) + "" + currency
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_notification
                }

                override fun getBinding(parent: ViewGroup): RowTransactionhistoryBinding {
                    binding = RowTransactionhistoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        actWalletBinding.rvtransactionhistory.apply {
            if (walletList.size > 0) {
                actWalletBinding.rvtransactionhistory.visibility = View.VISIBLE
                actWalletBinding.tvNoDataFound.visibility = View.GONE
                layoutManager = manager
                itemAnimator = DefaultItemAnimator()
                adapter = viewAllDataAdapter
            } else {
                actWalletBinding.rvtransactionhistory.visibility = View.GONE
                actWalletBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActWallet, false)
        callApiWallet()
    }
}