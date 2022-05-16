package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActVendorsDetailsBinding
import com.ecommerce.user.databinding.RowBannerBinding
import com.ecommerce.user.databinding.RowGravityBinding
import com.ecommerce.user.databinding.RowStoreBannerBinding
import com.ecommerce.user.model.TopbannerItem
import com.ecommerce.user.model.VendorsDetailsDataItem
import com.ecommerce.user.model.VendorsDetailsResponse
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActVendorsDetails : BaseActivity() {
    private lateinit var vendorsdetailsBinding: ActVendorsDetailsBinding
    private var bannerList = ArrayList<TopbannerItem>()

    private var vendorsdetailsDataList = ArrayList<VendorsDetailsDataItem>()
    var currency: String = ""
    var currencyPosition: String = ""
    private var vendorsDataAdapter: BaseAdaptor<VendorsDetailsDataItem, RowGravityBinding>? =
        null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var currentPage = 1
    var total_pages: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisibleItems = 0
    var image = ""
    var rate = ""
    var vendorsName = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )
    var vendorsId = ""
    private var timer: Timer? = null

    override fun setLayout(): View = vendorsdetailsBinding.root

    override fun initView() {
        vendorsdetailsBinding = ActVendorsDetailsBinding.inflate(layoutInflater)
        currency = SharePreference.getStringPref(this@ActVendorsDetails, SharePreference.Currency)!!
        currencyPosition = SharePreference.getStringPref(
            this@ActVendorsDetails,
            SharePreference.CurrencyPosition
        )!!
        vendorsdetailsBinding.ivAboutus.visibility = View.GONE
        linearLayoutManager = LinearLayoutManager(
            this@ActVendorsDetails,
            LinearLayoutManager.VERTICAL,
            false
        )
        vendorsdetailsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        vendorsId = intent.getStringExtra("vendor_id") ?: ""
        vendorsdetailsBinding.tvtitle.text = intent.getStringExtra("vendors_name")!!
        if (Common.isCheckNetwork(this@ActVendorsDetails)) {
//            if (SharePreference.getBooleanPref(this@ActVendorsDetails, SharePreference.isLogin)) {
                callApiVendorsDetail(vendorsId)
                image = intent.getStringExtra("vendors_iv")!!
                rate = intent.getStringExtra("vendors_rate") ?: "0.0"
                vendorsName = intent.getStringExtra("vendors_name")!!
//            } else {
//                openActivity(ActLogin::class.java)
//                this.finish()
//            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActVendorsDetails,
                resources.getString(R.string.no_internet)
            )
        }
        vendorsdetailsBinding.rvVendorsList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager!!.childCount
                    totalItemCount = linearLayoutManager!!.itemCount
                    pastVisibleItems = linearLayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            if (Common.isCheckNetwork(this@ActVendorsDetails)) {
                                callApiVendorsDetail(vendorsId)
                            } else {
                                Common.alertErrorOrValidationDialog(
                                    this@ActVendorsDetails,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    //TODO first banner
    private fun loadPagerImagesSliders(slidersList: ArrayList<TopbannerItem>) {
        lateinit var binding: RowStoreBannerBinding
        val bannerAdapter = object :
            BaseAdaptor<TopbannerItem, RowStoreBannerBinding>(this@ActVendorsDetails, slidersList) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: TopbannerItem,
                position: Int
            ) {
                Glide.with(this@ActVendorsDetails).load(slidersList[position].imageUrl)
                    .into(binding.ivBanner)
                binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
            }

            override fun setItemLayout(): Int {
                return R.layout.row_banner
            }

            override fun getBinding(parent: ViewGroup): RowStoreBannerBinding {
                binding = RowStoreBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }
        if (bannerList.size > 0) {
            vendorsdetailsBinding.rvBanner.visibility = View.VISIBLE
            vendorsdetailsBinding.rvBanner.apply {
                layoutManager = LinearLayoutManager(
                    this@ActVendorsDetails,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                itemAnimator = DefaultItemAnimator()
                adapter = bannerAdapter
                isNestedScrollingEnabled = true
                timer = Timer()
                vendorsdetailsBinding.rvBanner.let {
                    timer?.schedule(AutoScrollTaskSliders(-1, it, slidersList), 0, 5000L)
                }
            }
        } else {
            vendorsdetailsBinding.rvBanner.visibility = View.GONE
        }
    }

    //TODO first banner
    private class AutoScrollTaskSliders(
        private var position: Int,
        private var rvBanner: RecyclerView,
        private var arrayList: ArrayList<TopbannerItem>
    ) : TimerTask() {
        override fun run() {
            if (arrayList.size > position) {

                if (position == arrayList.size - 1) {
                    position = 0
                } else {
                    position++
                }
            }
            rvBanner.smoothScrollToPosition(position)
        }
    }

    //TODO CALL VENDORES DETAILS API
    private fun callApiVendorsDetail(vendorsId: String) {
        Common.showLoadingProgress(this@ActVendorsDetails)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActVendorsDetails, SharePreference.userId)!!
        hasmap["vendor_id"] = vendorsId
        val call = ApiClient.getClient.getVendorsDetails(currentPage.toString(), hasmap)
        call.enqueue(object : Callback<VendorsDetailsResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<VendorsDetailsResponse>,
                response: Response<VendorsDetailsResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    Log.e("Status", restResponce.status.toString())
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == 1) {
                            vendorsdetailsDataList.clear()
                        }
                        restResponce.data?.data?.let {
                            vendorsdetailsDataList.addAll(it)
                        }
                        rate = if (restResponce.vendordetails?.rattings != null) {
                            restResponce.vendordetails.rattings.avgRatting.toString()
                        } else {
                            "0.0"
                        }
                        currentPage = restResponce.data?.currentPage?.toInt() ?: 0
                        total_pages = restResponce.data?.lastPage?.toInt() ?: 0
                        bannerList.addAll(restResponce.bannerList)
                        loadPagerImagesSliders(bannerList)
                        loadVendorsDetails(vendorsdetailsDataList)
                        vendorsdetailsBinding.ivAboutus.visibility = View.VISIBLE

                        vendorsdetailsBinding.ivAboutus.setOnClickListener {
                            val intent = Intent(this@ActVendorsDetails, ActStoreInfo::class.java)
                            intent.putExtra("mobile", restResponce.vendordetails?.mobile)
                            intent.putExtra("email", restResponce.vendordetails?.email)
                            intent.putExtra("image", image)
                            intent.putExtra("rate", rate)
                            intent.putExtra("vendorsName", vendorsName)
                            intent.putExtra(
                                "storeaddress",
                                restResponce.vendordetails?.storeAddress
                            )
                            startActivity(intent)
                        }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActVendorsDetails,
                            restResponce.message.toString()
                        )
                        vendorsdetailsBinding.ivAboutus.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<VendorsDetailsResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActVendorsDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO VENDORS DETAILS DATA SET
    private fun loadVendorsDetails(vendorsdetailsDataList: ArrayList<VendorsDetailsDataItem>) {
        lateinit var binding: RowGravityBinding
        vendorsDataAdapter =
            object : BaseAdaptor<VendorsDetailsDataItem, RowGravityBinding>(
                this@ActVendorsDetails,
                vendorsdetailsDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: VendorsDetailsDataItem,
                    position: Int
                ) {
                    if (vendorsdetailsDataList.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_dislike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_like,
                                null
                            )
                        )
                    }
                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(this@ActVendorsDetails, SharePreference.isLogin))
                        {
                            if (vendorsdetailsDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    vendorsdetailsDataList[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActVendorsDetails,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActVendorsDetails)) {
                                    callApiFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActVendorsDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            } else if (vendorsdetailsDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    vendorsdetailsDataList[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActVendorsDetails,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActVendorsDetails)) {
                                    callApiRemoveFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActVendorsDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    }

                    if (vendorsdetailsDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            vendorsdetailsDataList[position].rattings?.get(0)?.avgRatting.toString()
                    }
                    binding.tvcateitemname.text = vendorsdetailsDataList[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    vendorsdetailsDataList[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    vendorsdetailsDataList[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                vendorsdetailsDataList[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                vendorsdetailsDataList[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActVendorsDetails, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            vendorsdetailsDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                    Glide.with(this@ActVendorsDetails)
                        .load(vendorsdetailsDataList[position].productimage?.imageUrl)
                        .into(binding.ivCartitemm)
                    binding.ivCartitemm.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_gravity
                }

                override fun getBinding(parent: ViewGroup): RowGravityBinding {
                    binding = RowGravityBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        vendorsdetailsBinding.rvVendorsList.apply {
            if (vendorsdetailsDataList.size > 0) {
                vendorsdetailsBinding.rvVendorsList.visibility = View.VISIBLE
                vendorsdetailsBinding.tvNoDataFound.visibility = View.GONE
                layoutManager = linearLayoutManager
                itemAnimator = DefaultItemAnimator()
                adapter = vendorsDataAdapter
                vendorsdetailsBinding.rvVendorsList.isNestedScrollingEnabled = true
            } else {
                vendorsdetailsBinding.rvVendorsList.visibility = View.GONE
                vendorsdetailsBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActVendorsDetails)
        val call = ApiClient.getClient.setRemoveFromWishList(map)
        Log.e("remove-->", Gson().toJson(map))
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {

                        Common.dismissLoadingProgress()
                        vendorsdetailsDataList[position].isWishlist = 0
                        vendorsDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActVendorsDetails,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActVendorsDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    //TODO CALL API FAVOURITE
    private fun callApiFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActVendorsDetails)
        val call = ApiClient.getClient.setAddToWishList(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        vendorsdetailsDataList[position].isWishlist = 1
                        vendorsDataAdapter!!.notifyItemChanged(position)

                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActVendorsDetails,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActVendorsDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}