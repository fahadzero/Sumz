package com.ecommerce.user.activity

import android.util.Log
import android.view.View
import android.widget.Toast
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActStripePaymentBinding
import com.ecommerce.user.utils.CallBackSuccess
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.NewStripeDataController
import com.ecommerce.user.utils.SharePreference
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.HashMap

class ActStripePayment : BaseActivity(), CallBackSuccess {
    private lateinit var _binding: ActStripePaymentBinding
    private var publicKey = ""
    var newStripeDataController: NewStripeDataController? = null
    override fun setLayout(): View = _binding.root

    override fun initView() {
        _binding = ActStripePaymentBinding.inflate(layoutInflater)
        _binding.ivClose.setOnClickListener {
            finish()
        }
        publicKey = intent.getStringExtra("stripeKey") ?: ""
        Log.d("publicKey",publicKey)
        _binding.btnProccedtoPayment.setOnClickListener {
            if (_binding.cvStripe.card != null) {
                startStripPayment(_binding.cvStripe.card!!)
            }
        }
        newStripeDataController =
            NewStripeDataController(
                this@ActStripePayment,
                publicKey
            )
        _binding.ivClose.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
    }

    private fun startStripPayment(card: Card) {
        if (!card.validateCard()) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.invalid_card),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            newStripeDataController!!.CreateToken(card, this@ActStripePayment)
            Toast.makeText(this@ActStripePayment, "Add Money successful", Toast.LENGTH_LONG).show()
        }
    }

    override fun onstart() {
        Common.showLoadingProgress(this@ActStripePayment)
    }

    override fun success(token: Token?) {
        try {
            val hashMap = HashMap<String, String>()
            hashMap["user_id"] =
                SharePreference.getStringPref(this@ActStripePayment, SharePreference.userId) ?: ""
            hashMap["recharge_amount"] = intent.getStringExtra("amount") ?: "0.00"
            hashMap["stripeToken"] = token!!.id
            hashMap["payment_type"] = "4"
            Log.d("AddmoneyWallet", hashMap.toString())
            callAddMoneyToWalletApi(hashMap)
        } catch (e: Exception) {
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    override fun failer(error: Exception?) {
        Common.dismissLoadingProgress()
        Common.getToast(this@ActStripePayment, error?.message.toString())
        Log.e("StripeError", error?.message.toString())
    }

    private fun callAddMoneyToWalletApi(hashMap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActStripePayment)
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
                        openActivity(ActWallet::class.java)
                    } else {
                        Common.showErrorFullMsg(
                            this@ActStripePayment,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActStripePayment,
                        resources.getString(R.string.error_msg)
                    )
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActStripePayment,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}