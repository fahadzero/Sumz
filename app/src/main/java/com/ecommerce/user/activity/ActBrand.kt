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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActBrandBinding
import com.ecommerce.user.databinding.RowBrandsBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.isCheckNetwork
import com.ecommerce.user.utils.Common.showLoadingProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActBrand : BaseActivity() {
    private lateinit var brandBinding: ActBrandBinding
    private var brandDataList = ArrayList<BrandDataItem>()
    private var bannerAdapter: BaseAdaptor<BrandDataItem, RowBrandsBinding>? =
        null
    private var gridLayoutManager: GridLayoutManager? = null
    override fun setLayout(): View = brandBinding.root
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
        brandBinding = ActBrandBinding.inflate(layoutInflater)
        brandBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        gridLayoutManager = GridLayoutManager(this@ActBrand, 3, GridLayoutManager.VERTICAL, false)
        brandBinding.rvAllBrands.layoutManager = gridLayoutManager
        loadBrandDataList(brandDataList)
        if (isCheckNetwork(this@ActBrand)) {
            callApiBrand(currentPage.toString())
        } else {
            alertErrorOrValidationDialog(
                this,
                resources.getString(R.string.no_internet)
            )
        }
        brandBinding.rvAllBrands.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager!!.childCount
                    totalItemCount = gridLayoutManager!!.itemCount
                    pastVisibleItems = gridLayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            if (isCheckNetwork(this@ActBrand)) {
                                callApiBrand(currentPage.toString())
                            } else {
                                alertErrorOrValidationDialog(
                                    this@ActBrand,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    //TODO API BRAND CALL
    private fun callApiBrand(currentPage: String) {
        showLoadingProgress(this@ActBrand)
        val call = ApiClient.getClient.getBrands(currentPage)
        call.enqueue(object : Callback<BrandResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<BrandResponse>,
                response: Response<BrandResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        restResponce.vendors?.data?.let { brandDataList.addAll(it) }
                        this@ActBrand.currentPage = restResponce.vendors?.currentPage!!.toInt()
                        this@ActBrand.total_pages = restResponce.vendors.lastPage!!.toInt()
                        bannerAdapter?.notifyDataSetChanged()

                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActBrand,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<BrandResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActBrand,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET BRAND DATA
    private fun loadBrandDataList(brandDataList: ArrayList<BrandDataItem>) {

        lateinit var binding: RowBrandsBinding
        bannerAdapter = object :
            BaseAdaptor<BrandDataItem, RowBrandsBinding>(this@ActBrand, brandDataList) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: BrandDataItem,
                position: Int
            ) {
                binding.tvBrandsName.text = brandDataList[position].brandName
                binding.tvBrandsName.visibility=View.GONE
                Glide.with(this@ActBrand).load(brandDataList[position].imageUrl).into(binding.ivBrands)
                binding.ivBrands.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                binding.clMain.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                binding.ivBrands.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                holder?.itemView?.setOnClickListener {
                    Log.e("brand_id--->", brandDataList[position].id.toString())
                    val intent = Intent(this@ActBrand, ActBrandDetails::class.java)
                    intent.putExtra("brand_id", brandDataList[position].id.toString())
                    intent.putExtra("brand_name", brandDataList[position].brandName.toString())
                    startActivity(intent)
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_brands
            }

            override fun getBinding(parent: ViewGroup): RowBrandsBinding {
                binding = RowBrandsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }
        brandBinding.rvAllBrands.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = bannerAdapter

        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActBrand, false)
    }
}