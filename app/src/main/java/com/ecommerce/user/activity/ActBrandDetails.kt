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
import com.ecommerce.user.databinding.ActBrandDetailsBinding
import com.ecommerce.user.databinding.RowGravityBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.isCheckNetwork
import com.ecommerce.user.utils.Common.showLoadingProgress
import com.ecommerce.user.utils.SharePreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActBrandDetails : BaseActivity() {
    private lateinit var brandDetailsBinding: ActBrandDetailsBinding
    private var branddetailsDataList = ArrayList<BrandDetailsDataItem>()
    var currency: String = ""
    var currencyPosition: String = ""
    private var viewAllDataAdapter: BaseAdaptor<BrandDetailsDataItem, RowGravityBinding>? =
        null
    private var linearLayoutManager: LinearLayoutManager? = null
    override fun setLayout(): View = brandDetailsBinding.root
    private var currentPage = 1
    var total_pages: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisibleItems = 0
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun initView() {
        brandDetailsBinding = ActBrandDetailsBinding.inflate(layoutInflater)
        brandDetailsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        linearLayoutManager =
            LinearLayoutManager(this@ActBrandDetails, LinearLayoutManager.VERTICAL, false)
        currency =
            SharePreference.getStringPref(this@ActBrandDetails, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActBrandDetails,
                SharePreference.CurrencyPosition
            )!!
        if (isCheckNetwork(this@ActBrandDetails)) {
//            if (SharePreference.getBooleanPref(this@ActBrandDetails, SharePreference.isLogin)) {
                callApiBrandsDetail(currentPage.toString(), intent.getStringExtra("brand_id")!!)
                brandDetailsBinding.tvtitle.text = intent.getStringExtra("brand_name")!!

//            } else {
//                openActivity(ActLogin::class.java)
//                this.finish()
//            }
        } else {
            alertErrorOrValidationDialog(
                this@ActBrandDetails,
                resources.getString(R.string.no_internet)
            )
        }
        brandDetailsBinding.rvBrandList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager!!.childCount
                    totalItemCount = linearLayoutManager!!.itemCount
                    pastVisibleItems = linearLayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            callApiBrandsDetail(
                                currentPage.toString(),
                                intent.getStringExtra("brand_id")!!
                            )
                        }
                    }
                }
            }
        })
    }

    //TODO API BRANDS DETAILS CALL
    private fun callApiBrandsDetail(currentPage: String, brandId: String) {
        showLoadingProgress(this@ActBrandDetails)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActBrandDetails, SharePreference.userId)!!
        hasmap["brand_id"] = brandId
        val call = ApiClient.getClient.getBrandDetails(currentPage, hasmap)
        call.enqueue(object : Callback<BrandDetailsResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<BrandDetailsResponse>,
                response: Response<BrandDetailsResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        restResponce.alldata?.data?.let { branddetailsDataList.addAll(it) }
                        loadBrandDetails(branddetailsDataList)
                        this@ActBrandDetails.currentPage =
                            restResponce.alldata?.currentPage!!.toInt()
                        this@ActBrandDetails.total_pages = restResponce.alldata.lastPage!!.toInt()
                        viewAllDataAdapter?.notifyDataSetChanged()
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActBrandDetails,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<BrandDetailsResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActBrandDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET BRANDS DETAILS DATA
    private fun loadBrandDetails(branddetailsDataList: ArrayList<BrandDetailsDataItem>) {
        lateinit var binding: RowGravityBinding
        viewAllDataAdapter =
            object : BaseAdaptor<BrandDetailsDataItem, RowGravityBinding>(
                this@ActBrandDetails,
                branddetailsDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: BrandDetailsDataItem,
                    position: Int
                ) {
                        if (branddetailsDataList.get(position).isWishlist == 0) {
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
                        if (SharePreference.getBooleanPref( this@ActBrandDetails, SharePreference.isLogin)) {
                            if (branddetailsDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    branddetailsDataList[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActBrandDetails,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork( this@ActBrandDetails)) {
                                    callApiFavourite(map,position,branddetailsDataList)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActBrandDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (branddetailsDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    branddetailsDataList[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActBrandDetails,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork( this@ActBrandDetails)) {
                                    callApiRemoveFavourite(map,position,branddetailsDataList)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActBrandDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }


                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    }

                    binding.tvcateitemname.text = branddetailsDataList[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    branddetailsDataList[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    branddetailsDataList[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                branddetailsDataList[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                branddetailsDataList[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    holder?.itemView?.setOnClickListener {
                        Log.e("product_id--->", branddetailsDataList[position].id.toString())
                        val intent = Intent(this@ActBrandDetails, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            branddetailsDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                    Glide.with(this@ActBrandDetails)
                        .load(branddetailsDataList[position].productimage?.imageUrl).into(binding.ivCartitemm)
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

        brandDetailsBinding.rvBrandList.apply {
            if (branddetailsDataList.size > 0) {
                brandDetailsBinding.rvBrandList.visibility = View.VISIBLE
                brandDetailsBinding.tvNoDataFound.visibility = View.GONE
                layoutManager = linearLayoutManager
                itemAnimator = DefaultItemAnimator()
                adapter = viewAllDataAdapter
            } else {
                brandDetailsBinding.rvBrandList.visibility = View.GONE
                brandDetailsBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

    private fun callApiRemoveFavourite(
        map: HashMap<String, String>,
        position: Int,
        branddetailsDataList: ArrayList<BrandDetailsDataItem>
    ) {
        showLoadingProgress(this@ActBrandDetails)
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
                        dismissLoadingProgress()
                        branddetailsDataList[position].isWishlist = 0
                        viewAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActBrandDetails,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActBrandDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiFavourite(
        map: HashMap<String, String>,
        position: Int,
        branddetailsDataList: ArrayList<BrandDetailsDataItem>
    ) {

        showLoadingProgress(this@ActBrandDetails)
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
                        dismissLoadingProgress()
                        branddetailsDataList[position].isWishlist = 1
                        viewAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActBrandDetails,
                            restResponse.message
                        )

                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActBrandDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActBrandDetails, false)
    }
}