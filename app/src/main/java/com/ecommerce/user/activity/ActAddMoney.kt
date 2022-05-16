package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.user.R
import com.ecommerce.user.adapter.PaymentListAdapter
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActAddMoneyBinding
import com.ecommerce.user.model.FlutterWaveResponse
import com.ecommerce.user.model.PaymentListResponse
import com.ecommerce.user.model.PaymentlistItem
import com.ecommerce.user.model.WalletResponse
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Constants
import com.ecommerce.user.utils.SharePreference
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt

class ActAddMoney : BaseActivity(), PaymentResultListener {
    private var paymentName = ""
    private var paymentList = ArrayList<PaymentlistItem>()
    private var paymentAdapter: PaymentListAdapter? = null
    private var strRezorPayKey = ""
    private var payStackKey = ""
    private var flutterWaveKey = ""
    private var encryptionKey = ""
    private var logoimg = ""
    private var stripekey = ""
    var currency: String = ""
    var currencyPosition: String = ""
    private lateinit var addMoneyBinding: ActAddMoneyBinding
    override fun setLayout(): View = addMoneyBinding.root

    override fun initView() {
        addMoneyBinding = ActAddMoneyBinding.inflate(layoutInflater)
        currency =
            SharePreference.getStringPref(this@ActAddMoney, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActAddMoney,
                SharePreference.CurrencyPosition
            )!!
        Checkout.preload(this@ActAddMoney)
        initClickListeners()
        setupAdapter()
        callPaymentListApi()
    }

    private fun initClickListeners() {

        if (SharePreference.getStringPref(this@ActAddMoney, SharePreference.SELECTED_LANGUAGE)
                .equals(resources.getString(R.string.language_hindi))
        ) {
            addMoneyBinding.ivBack.rotation = 180F
        } else {
            addMoneyBinding.ivBack.rotation = 0F
        }
        addMoneyBinding.tvwallettype.text = currency
        addMoneyBinding.ivBack.setOnClickListener {
            finish()
        }
        addMoneyBinding.btnProccedtoPayment.setOnClickListener {

            if (addMoneyBinding.edAmount.text?.isEmpty() == true || addMoneyBinding.edAmount.text.toString() == ".") {
                Common.showErrorFullMsg(
                    this@ActAddMoney,
                    resources.getString(R.string.enter_amount)
                )
            } else if (!Common.isValidAmount(addMoneyBinding.edAmount.text.toString())) {
                Common.showErrorFullMsg(
                    this@ActAddMoney,
                    resources.getString(R.string.valid_amount)
                )
            } else if (addMoneyBinding.edAmount.text.toString().toDouble().toInt() < 1) {
                Common.showErrorFullMsg(this@ActAddMoney, resources.getString(R.string.one_amount))
            } else {
                when (paymentName) {
                    "RazorPay" -> {
                        Common.showLoadingProgress(this@ActAddMoney)
                        razorPayPayment()
                    }
                    "Paystack" -> {
                        val amount = addMoneyBinding.edAmount.text.toString().toDouble() ?: 0.00
                        val totalAmount = round(amount).times(100).roundToInt()
                        val i = Intent(this@ActAddMoney, ActPayStack::class.java)
                        i.putExtra(
                            "email",
                            SharePreference.getStringPref(
                                this@ActAddMoney,
                                SharePreference.userEmail
                            )
                        )
                        i.putExtra("public_key", payStackKey)
                        i.putExtra("amount", totalAmount.toString())
                        activityResult.launch(i)
                    }
                    "Flutterwave" -> {
                        flutterWavePayment()
                    }
                    "Stripe" -> {
                        val intent = Intent(this@ActAddMoney, ActStripePayment::class.java)
                        intent.putExtra("stripeKey", stripekey)
                        intent.putExtra("amount",addMoneyBinding.edAmount.text.toString())
                        activityResult.launch(intent)
                    }
                    "" -> {
                        Common.showErrorFullMsg(
                            this@ActAddMoney,
                            resources.getString(R.string.payment_type_selection_error)
                        )
                    }
                }
            }
        }
    }

    private fun setupAdapter() {

        paymentAdapter = PaymentListAdapter(this@ActAddMoney, paymentList) { i: Int, s: String ->
            if (s == Constants.ItemClick) {
                paymentName = paymentList[i].paymentName.toString()
            }
        }
        addMoneyBinding.rvpaymentlist.apply {
            layoutManager = LinearLayoutManager(this@ActAddMoney)
            itemAnimator = DefaultItemAnimator()
            adapter = paymentAdapter
        }
    }


    private fun callPaymentListApi() {
        Common.showLoadingProgress(this@ActAddMoney)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActAddMoney, SharePreference.userId)!!
        val call = ApiClient.getClient.getPaymentList(hasmap)
        call.enqueue(object : Callback<PaymentListResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<PaymentListResponse>,
                response: Response<PaymentListResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        addMoneyBinding.rvpaymentlist.visibility = View.VISIBLE
                        addMoneyBinding.tvNoDataFound.visibility = View.GONE


                        runOnUiThread {
                            restResponce.paymentlist?.let { paymentList.addAll(it) }

                            paymentList.removeAll {
                                it.paymentName=="COD"||it.paymentName=="Wallet"
                            }


                            paymentAdapter?.notifyDataSetChanged()
                            for (i in 0 until paymentList.size) {
                                when (paymentList[i].paymentName) {
                                    "RazorPay" -> {
                                        strRezorPayKey = if (paymentList[i].environment == 1) {
                                            paymentList[i].testPublicKey!!
                                        } else {
                                            paymentList[i].livePublicKey!!
                                        }

                                    }
                                    "Stripe" -> {
                                        stripekey = if (paymentList[i].environment == 1) {
                                            paymentList[i].testPublicKey.toString()
                                        } else {
                                            paymentList[i].livePublicKey.toString()
                                        }

                                    }
                                    "Paystack" -> {
                                        payStackKey = if (paymentList[i].environment == 1) {
                                            paymentList[i].testPublicKey!!

                                        } else {
                                            paymentList[i].livePublicKey!!
                                        }


                                    }
                                    "Flutterwave" -> {
                                        flutterWaveKey = if (paymentList[i].environment == 1) {
                                            paymentList[i].testPublicKey!!

                                        } else {
                                            paymentList[i].livePublicKey!!
                                        }

                                        encryptionKey = paymentList[i].encryptionKey ?: ""

                                    }
                                }
                            }

                        }

                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        addMoneyBinding.rvpaymentlist.visibility = View.GONE
                        addMoneyBinding.tvNoDataFound.visibility = View.VISIBLE
                        Common.alertErrorOrValidationDialog(
                            this@ActAddMoney,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<PaymentListResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActAddMoney,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActAddMoney, false)

    }

    private fun razorPayPayment() {
        val activity: Activity = this
        val co = Checkout()
        try {
            co.setKeyID(strRezorPayKey)
            val amount = addMoneyBinding.edAmount.text.toString().toDouble().times(100)
            Common.getLog("test", amount.toString())
            val options = JSONObject()
            options.put("name", resources.getString(R.string.app_name))
            options.put("description", resources.getString(R.string.order_payment))
            options.put("image", logoimg)
            options.put("currency", "INR")
            options.put("amount", String.format(Locale.US, "%d", amount.toLong()))
            val prefill = JSONObject()
            prefill.put(
                "email",
                SharePreference.getStringPref(this@ActAddMoney, SharePreference.userEmail)
            )
            prefill.put(
                "contact",
                SharePreference.getStringPref(this@ActAddMoney, SharePreference.userMobile)
            )
            options.put("prefill", prefill)
            val theme = JSONObject()
            theme.put("color", "#366ed4")
            options.put("theme", theme)
            co.open(activity, options)
        } catch (e: Exception) {
            Common.dismissLoadingProgress()

            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun flutterWavePayment() {
        RaveUiManager(this).setAmount(addMoneyBinding.edAmount.text.toString().toDouble() ?: 0.00)
            .setEmail(SharePreference.getStringPref(this@ActAddMoney, SharePreference.userEmail))
            .setfName(SharePreference.getStringPref(this@ActAddMoney, SharePreference.userName))
            .setlName(SharePreference.getStringPref(this@ActAddMoney, SharePreference.userName))
            .setPublicKey(flutterWaveKey)
            .setEncryptionKey(encryptionKey)
            .setCountry("NG")
            .setCurrency("NGN")
            .setTxRef(System.currentTimeMillis().toString() + "Ref")
            .setPhoneNumber(
                SharePreference.getStringPref(
                    this@ActAddMoney,
                    SharePreference.userMobile
                ), false
            )
            .acceptMpesaPayments(true)
            .acceptBankTransferPayments(true, true)
            .acceptAccountPayments(true)
            .acceptSaBankPayments(true)
            .acceptBankTransferPayments(true)
            .acceptCardPayments(true)
            .onStagingEnv(false)
            .withTheme(R.style.DefaultPayTheme)
            .allowSaveCardFeature(false, false)
            .initialize()
    }


    override fun onPaymentSuccess(razorPayId: String?) {
        Common.dismissLoadingProgress()
        razorPayId?.let { callAddMoneyToWalletApi(it, "3") }
    }

    override fun onPaymentError(error: Int, response: String?) {
        try {
            Toast.makeText(this, "$response", Toast.LENGTH_LONG).show()
            Common.dismissLoadingProgress()
        } catch (e: Exception) {
            Log.e("Exception", e.message, e)
        }
    }

    private var activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.e("code",result.resultCode.toString())
            Log.e("response",result.data.toString())
            if (result.resultCode == RESULT_OK) {
                val id = result.data?.getStringExtra("id") ?: ""
                callAddMoneyToWalletApi(id, "6")
                setResult(RESULT_OK)
            }
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {

            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                    Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show()
                    val message: String? = data.getStringExtra("response")
                    Log.e("message", message.toString())

                    val json = Gson().fromJson(message, FlutterWaveResponse::class.java)
                    val dataValue = json.data
                    val id = dataValue?.flwRef
                    callAddMoneyToWalletApi(id.toString(), "5")

                }
                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(this, "An Error Occur", Toast.LENGTH_SHORT).show()
                }
                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun callAddMoneyToWalletApi(transactionId: String, paymentType: String) {
        Common.showLoadingProgress(this@ActAddMoney)
        val hashMap = HashMap<String, String>()
        hashMap["user_id"] =
            SharePreference.getStringPref(this@ActAddMoney, SharePreference.userId) ?: ""
        hashMap["recharge_amount"] = addMoneyBinding.edAmount.text.toString()
        hashMap["payment_id"] = transactionId
        hashMap["payment_type"] = paymentType
        Log.d("addmoney", hashMap.toString())
        val call = ApiClient.getClient.addMoney(hashMap)
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
                            this@ActAddMoney,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActAddMoney,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActAddMoney,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}

