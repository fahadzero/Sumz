package com.ecommerce.user.activity

import android.util.Log
import android.view.View
import android.widget.Toast
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActCardInfoBinding
import com.ecommerce.user.utils.CallBackSuccess
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.NewStripeDataController
import com.ecommerce.user.utils.SharePreference
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActCardInfo : BaseActivity(), CallBackSuccess {
    private lateinit var actCardInfoBinding: ActCardInfoBinding
    var newStripeDataController: NewStripeDataController? = null
    private var publicKey = ""
    override fun setLayout(): View = actCardInfoBinding.root

    override fun initView() {
        actCardInfoBinding = ActCardInfoBinding.inflate(layoutInflater)
        publicKey = intent.getStringExtra("public_key") ?: ""
        actCardInfoBinding.btnProccedtoPayment.setOnClickListener {
            if (actCardInfoBinding.cvStripe.card != null) {
                startStripPayment(actCardInfoBinding.cvStripe.card!!)
            }
        }
        newStripeDataController =
            NewStripeDataController(
                this@ActCardInfo,
                publicKey
            )
        actCardInfoBinding.ivClose.setOnClickListener {
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
            newStripeDataController!!.CreateToken(card, this@ActCardInfo)
            Toast.makeText(this@ActCardInfo, "Payment was successful", Toast.LENGTH_LONG).show()
        }
    }

    override fun onstart() {
        Common.showLoadingProgress(this@ActCardInfo)
    }

    override fun success(token: Token?) {
        try {
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] = SharePreference.getStringPref(
                this@ActCardInfo,
                SharePreference.userId
            )!!
            hasmap["email"] = SharePreference.getStringPref(
                this@ActCardInfo,
                SharePreference.userEmail
            )!!
            hasmap["full_name"] =
                intent.getStringExtra("fname")!!
            hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
            hasmap["mobile"] = intent.getStringExtra("mobile")!!
            hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
            hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
            hasmap["stripeToken"] = token!!.id
            hasmap["stripeEmail"] = SharePreference.getStringPref(
                this@ActCardInfo,
                SharePreference.userEmail
            )!!
            hasmap["payment_type"] = "4"
            hasmap["pincode"] = intent.getStringExtra("pincode")!!
            hasmap["street_address"] = intent.getStringExtra("street_address")!!
            hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
            hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
            callApiOrder(hasmap)
        } catch (e: Exception) {
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    override fun failer(error: Exception?) {
        Common.dismissLoadingProgress()
        Common.getToast(this@ActCardInfo, error?.message.toString())
        Log.e("error", error?.message.toString())
    }

    private fun callApiOrder(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActCardInfo)
        val call = ApiClient.getClient.setOrderPayment(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    Common.isAddOrUpdated = true

                    if (response.body()?.status == 1) {
                        Common.showSuccessFullMsg(
                            this@ActCardInfo,
                            response.body()?.message.toString()
                        )
                        openActivity(ActPaymentSuccessFull::class.java)
                    } else {
                        Common.showErrorFullMsg(
                            this@ActCardInfo,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActCardInfo,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActCardInfo,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}