package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActCheckoutBinding
import com.ecommerce.user.databinding.RowCheckoutBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActCheckout : BaseActivity() {
    private lateinit var checkoutBinding: ActCheckoutBinding
    private var checkOutDataList: ArrayList<CheckOutDataItem>? = null
    var currency: String = ""
    var currencyPosition: String = ""
    var subtotal: Int = 0
    var tax: Int = 0
    var checkoutprice: Int = 0
    var discountsum: String = ""
    var price: Double = 0.0
    var fname: String = ""
    var landmark: String = ""
    var lname: String = ""
    var email: String = ""
    var pincode: String = ""
    var mobile: String = ""
    var streetAddress: String = ""
    var coupon_name: String = ""
    var vendorid: String = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun setLayout(): View = checkoutBinding.root

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun initView() {
        checkoutBinding = ActCheckoutBinding.inflate(layoutInflater)
        checkoutBinding.tvapplycoupon.visibility = View.GONE
        checkoutBinding.clCoupon.visibility = View.GONE
        currency = SharePreference.getStringPref(this@ActCheckout, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(this@ActCheckout, SharePreference.CurrencyPosition)!!
        checkoutBinding.btnaddress.setOnClickListener { openActivity(ActAddress::class.java) }
        checkoutBinding.ivBack.setOnClickListener { finish() }
        if (SharePreference.getBooleanPref(this@ActCheckout, SharePreference.isLogin)) {
            if (Common.isCheckNetwork(this@ActCheckout)) {
                val map = HashMap<String, String>()
                map["user_id"] = SharePreference.getStringPref(
                    this@ActCheckout,
                    SharePreference.userId
                )!!
                callApiCheckoutitem(map)
            } else {
                alertErrorOrValidationDialog(
                    this@ActCheckout,
                    resources.getString(R.string.no_internet)
                )
            }
        } else {
            openActivity(ActLogin::class.java)
            finish()
            finishAffinity()
        }
        checkoutBinding.btnaddress.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActAddress::class.java)
            intent.putExtra("isComeFromSelectAddress", true)
            addressDataSet.launch(intent)
        }
        checkoutBinding.tveditaddress.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActAddress::class.java)
            intent.putExtra("isComeFromSelectAddress", true)
            addressDataSet.launch(intent)
        }
        checkoutBinding.clhaveacoupon.setOnClickListener {
            if (!SharePreference.getBooleanPref(this@ActCheckout, SharePreference.isCoupon)) {
                SharePreference.setBooleanPref(this@ActCheckout, SharePreference.isCoupon, true)
                checkoutBinding.tvapplycoupon.visibility = View.GONE
                checkoutBinding.clCoupon.visibility = View.GONE
            } else {
                checkoutBinding.tvapplycoupon.visibility = View.VISIBLE
                checkoutBinding.clCoupon.visibility = View.VISIBLE
            }
        }
        //TODO APPLY COUPON
        checkoutBinding.btnapplycoupon.setOnClickListener {
            if (checkoutBinding.btnapplycoupon.text.toString() == resources.getString(R.string.apply)
            ) {
                if (checkoutBinding.edtcouponcode.text.isNotEmpty()) {
                    val map = HashMap<String, String>()
                    map["user_id"] = SharePreference.getStringPref(
                        this@ActCheckout,
                        SharePreference.userId
                    )!!
                    map["coupon_name"] = checkoutBinding.edtcouponcode.text.toString()
                    callApiCheckoutitem(map)
                    checkoutBinding.edtcouponcode.requestFocus()
                    checkoutBinding.btnapplycoupon.setTextColor(getColor(R.color.red))
                    checkoutBinding.btnapplycoupon.text = resources.getString(R.string.remove)
                    coupon_name = checkoutBinding.edtcouponcode.text.toString()
                } else {
                    alertErrorOrValidationDialog(
                        this@ActCheckout,
                        resources.getString(R.string.coupan_code_validation)
                    )
                }
            } else if (checkoutBinding.btnapplycoupon.text.toString() == resources.getString(R.string.remove)) {
                checkoutBinding.edtcouponcode.setText("")
                checkoutBinding.btnapplycoupon.text = resources.getString(R.string.apply)
                checkoutBinding.tvdiscounttotal.text = currency + "0.00"
                checkoutBinding.tvtotal.text = price.toString()
                checkoutBinding.btnapplycoupon.setTextColor(getColor(R.color.Blackcolor))
                val map = HashMap<String, String>()
                map["user_id"] = SharePreference.getStringPref(
                    this@ActCheckout,
                    SharePreference.userId
                )!!
                callApiCheckoutitem(map)
                coupon_name = ""
            }
        }
    }

    //TODO AP CHECK OUT API
    private fun callApiCheckoutitem(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActCheckout)
        val call = ApiClient.getClient.getCheckOut(map)
        call.enqueue(object : Callback<GetCheckOutResponse> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<GetCheckOutResponse>,
                response: Response<GetCheckOutResponse>
            ) {
                Log.e("code", response.code().toString())
                Log.e("status", response.body().toString())
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        checkOutDataList = restResponce.checkoutdata
                        checkOutDataList?.let {
                            loadCheckOutItem(it)
                        }
                        restResponce.data?.let {
                            loadCheckOutDetails(it)
                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        checkoutBinding.btnapplycoupon.text = resources.getString(R.string.apply)
                        checkoutBinding.edtcouponcode.setText("")
                        checkoutBinding.btnapplycoupon.setTextColor(getColor(R.color.Blackcolor))
                        alertErrorOrValidationDialog(
                            this@ActCheckout,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetCheckOutResponse>, t: Throwable) {
                dismissLoadingProgress()
                Log.e("error", t.message.toString())
                alertErrorOrValidationDialog(
                    this@ActCheckout,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET CHECKOUT DATA
    private fun loadCheckOutItem(checkOutDataList: ArrayList<CheckOutDataItem>) {
        discountsum = if (checkOutDataList == null) {
            "0"
        } else {
            (checkOutDataList.sumOf { it.discountAmount?.toDouble() ?: 0.0 }).toString()
        }
        checkoutBinding.btnProccedtoPayment.setOnClickListener {
            if (checkoutBinding.btnaddress.visibility == View.VISIBLE) {
                Common.showErrorFullMsg(
                    this@ActCheckout,
                    resources.getString(R.string.select_your_address)
                )
            } else {
                val intent = Intent(this@ActCheckout, ActPaymentMethod::class.java)
                intent.putExtra("email", email)
                intent.putExtra("fname", fname)
                intent.putExtra("lname", lname)
                intent.putExtra("landmark", landmark)
                intent.putExtra("mobile", mobile)
                intent.putExtra("order_notes", checkoutBinding.edtordernote.text.toString())
                intent.putExtra("pincode", pincode)
                intent.putExtra("street_address", streetAddress)
                intent.putExtra("coupon_name", coupon_name)
                intent.putExtra("discount_amount", discountsum).removeExtra("$")
                intent.putExtra("grand_total", price.toDouble().toString())
                intent.putExtra("vendorid", vendorid)
                Log.d("intent", price.toString())
                startActivity(intent)
            }
        }

        lateinit var binding: RowCheckoutBinding
        val wishListDataAdapter =
            object : BaseAdaptor<CheckOutDataItem, RowCheckoutBinding>(
                this@ActCheckout,
                checkOutDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CheckOutDataItem,
                    position: Int
                ) {
                    binding.tvcateitemname.text = checkOutDataList[position].productName

                    if (checkOutDataList[position].attribute == null || checkOutDataList[position].variation == null) {
                        binding.tvcartitemsize.text = "-"
                    } else {
                        binding.tvcartitemsize.text =
                            checkOutDataList[position].attribute + " : " + checkOutDataList[position].variation
                    }
                    val qty = checkOutDataList[position].qty
                    val qtyprice = checkOutDataList[position].price?.toDouble()
                    val totalpriceqty = qtyprice!! * qty!!
                    binding.tvcartitemqty.text = "Qty: " + checkOutDataList[position].qty
                    Log.d("tax", checkOutDataList[position].tax!!.toString())

                    val shippingCost =
                        if (checkOutDataList[position].shippingCost == "Free Shipping") {
                            "0.0"
                        } else {
                            checkOutDataList[position].shippingCost
                        }
                    if (currencyPosition == "left") {
                        binding.tvcartitemprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.02f",
                                    totalpriceqty
                                )
                            )
                        binding.tvtax.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.02f",
                                    checkOutDataList[position].tax!!.toString().toDouble()
                                )
                            )
                        binding.tvshippingcost.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.02f",
                                    shippingCost?.toDouble()
                                )
                            )
                        if (checkOutDataList[position].discountAmount == null) {
                            binding.tvdiscout.text =
                                currency.plus(
                                    String.format(
                                        Locale.US,
                                        "%,.02f",
                                        0.toDouble()
                                    )
                                )
                        } else {
                            binding.tvdiscout.text =
                                currency.plus(
                                    String.format(
                                        Locale.US,
                                        "%,.02f",
                                        checkOutDataList[position].discountAmount!!.toDouble()
                                    )
                                )
                        }
                    } else {
                        binding.tvcartitemprice.text =
                            (String.format(
                                Locale.US,
                                "%,.02f",
                                totalpriceqty
                            )) + "" + currency
                        binding.tvtax.text =
                            (String.format(
                                Locale.US,
                                "%,.02f",
                                checkOutDataList[position].tax!!.toString().toDouble()
                            )) + "" + currency
                        binding.tvshippingcost.text =
                            (String.format(
                                Locale.US,
                                "%,.02f",
                                shippingCost?.toDouble()
                            )) + "" + currency

                        if (checkOutDataList[position].discountAmount == null) {
                            binding.tvdiscout.text =
                                (String.format(
                                    Locale.US,
                                    "%,.02f",
                                    0.toDouble()
                                )) + "" + currency

                        } else {
                            binding.tvdiscout.text =
                                (String.format(
                                    Locale.US,
                                    "%,.02f",
                                    checkOutDataList[position].discountAmount!!.toDouble()
                                )) + "" + currency
                        }
                    }
                    Glide.with(this@ActCheckout)
                        .load(checkOutDataList[position].imageUrl).into(binding.ivCartitemm)
                    binding.ivCartitemm.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    vendorid = checkOutDataList[position].vendorId.toString()
                    holder?.itemView?.setOnClickListener {
                        Log.e(
                            "product_id--->",
                            checkOutDataList[position].productId.toString()
                        )
                        val intent = Intent(this@ActCheckout, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            checkOutDataList[position].productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_viewall
                }

                override fun getBinding(parent: ViewGroup): RowCheckoutBinding {
                    binding = RowCheckoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        checkoutBinding.rvCheckoutdata.apply {
            layoutManager =
                LinearLayoutManager(this@ActCheckout, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = wishListDataAdapter
        }
    }


    //TODO SET CHECKOUT OTHER DETAILS
    @SuppressLint("SetTextI18n")
    private fun loadCheckOutDetails(checkOutList: CheckOutData) {
        subtotal = checkOutList.subtotal!!
        tax = checkOutList.tax?.toDouble()?.toInt()!!
        val disc = Math.round(discountsum.toDouble()).toInt()

        val shippingCost = if (checkOutList.shippingCost == "Free Shipping") {
            "0.0"
        } else {
            checkOutList.shippingCost
        }
        price = checkOutList.subtotal + checkOutList.tax.toString()
            .toDouble() + shippingCost?.toDouble()!!
        checkoutprice = checkOutList.subtotal

        if (currencyPosition == "left") {
            checkoutBinding.tvsubtotal.text = currency.plus(
                String.format(Locale.US, "%,.02f", subtotal.toDouble())
            )
            checkoutBinding.tvtaxtotal.text = currency.plus(
                String.format(Locale.US, "%,.02f", checkOutList.tax.toDouble())
            )
            checkoutBinding.tvshippingtotal.text = currency.plus(
                shippingCost.toDouble().let {
                    String.format(
                        Locale.US,
                        "%,.02f",
                        it.toDouble()
                    )
                }
            )
            checkoutBinding.tvdiscounttotal.text = currency.plus(
                "-" + String.format(Locale.US, "%,.02f", disc.toDouble())
            )
            checkoutBinding.tvtotal.text =
                currency.plus(String.format(Locale.US, "%,.02f", price.toDouble()))
        } else {
            checkoutBinding.tvsubtotal.text =
                String.format(Locale.US, "%,.02f", subtotal.toDouble()) + "" + currency
            checkoutBinding.tvtaxtotal.text =
                String.format(Locale.US, "%,.02f", checkOutList.tax.toDouble()) + "" + currency
            checkoutBinding.tvshippingtotal.text = String.format(
                Locale.US,
                "%,.02f",
                shippingCost.toDouble()
            ) + "" + currency
            checkoutBinding.tvdiscounttotal.text =
                "-" + String.format(Locale.US, "%,.02f", disc.toDouble()) + currency
            checkoutBinding.tvtotal.text =
                String.format(Locale.US, "%,.02f", price) + "" + currency
        }
    }


    //TODO SET ADDRESS TO CHECK OUT PAGE
    @SuppressLint("SetTextI18n")
    val addressDataSet =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 500) {
                checkoutBinding.tvUserName.visibility = View.VISIBLE
                checkoutBinding.tvUserphone.visibility = View.VISIBLE
                checkoutBinding.tvareaaddress.visibility = View.VISIBLE
                checkoutBinding.tvusermailid.visibility = View.VISIBLE
                checkoutBinding.clAddressselect.visibility = View.VISIBLE
                checkoutBinding.btnaddress.visibility = View.GONE
                checkoutBinding.tveditaddress.visibility = View.VISIBLE
                checkoutBinding.tvUserName.text =
                    result.data?.getStringExtra("FirstName") + "" + result.data?.getStringExtra("LastName")
                checkoutBinding.tvareaaddress.text =
                    result.data?.getStringExtra("StreetAddress") + " " + result.data?.getStringExtra(
                        "Landmark"
                    ) + "-" + result.data?.getStringExtra(
                        "Pincode"
                    )
                checkoutBinding.tvUserphone.text = result.data?.getStringExtra("Mobile")
                checkoutBinding.tvusermailid.text = result.data?.getStringExtra("Email")
                email = result.data?.getStringExtra("Email").toString()
                fname = result.data?.getStringExtra("FirstName").toString()
                lname = result.data?.getStringExtra("LastName").toString()
                mobile = result.data?.getStringExtra("Mobile").toString()
                landmark = result.data?.getStringExtra("Landmark").toString()
                streetAddress = result.data?.getStringExtra("StreetAddress").toString()
                pincode = result.data?.getStringExtra("Pincode").toString()
            } else {
                checkoutBinding.tvUserName.visibility = View.GONE
                checkoutBinding.tvUserphone.visibility = View.GONE
                checkoutBinding.tvareaaddress.visibility = View.GONE
                checkoutBinding.tvusermailid.visibility = View.GONE
                checkoutBinding.clAddressselect.visibility = View.GONE
                checkoutBinding.btnaddress.visibility = View.VISIBLE
                checkoutBinding.tveditaddress.visibility = View.GONE
            }
        }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActCheckout, false)
    }
}