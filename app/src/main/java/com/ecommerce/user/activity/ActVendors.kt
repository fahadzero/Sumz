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
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActVendorsBinding
import com.ecommerce.user.databinding.RowVendorsdetailsBinding
import com.ecommerce.user.model.VenDorsDataItem
import com.ecommerce.user.model.VendorsResponse
import com.ecommerce.user.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActVendors : BaseActivity() {
    private lateinit var vendorsBinding: ActVendorsBinding
    private var vendorsDataList = ArrayList<VenDorsDataItem>()
    private var vendorsAdapter:
            BaseAdaptor<VenDorsDataItem, RowVendorsdetailsBinding>? = null
    private var layoutLinearLayoutManager: LinearLayoutManager? = null
    override fun setLayout(): View = vendorsBinding.root
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
        vendorsBinding = ActVendorsBinding.inflate(layoutInflater)
        layoutLinearLayoutManager =
            LinearLayoutManager(this@ActVendors, LinearLayoutManager.VERTICAL, false)
        vendorsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        vendorsDataList(vendorsDataList)
        if (Common.isCheckNetwork(this@ActVendors)) {
            callApiVendors(currentPage.toString())
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActVendors,
                resources.getString(R.string.no_internet)
            )
        }
        vendorsBinding.rvAllVendors.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = layoutLinearLayoutManager!!.childCount
                    totalItemCount = layoutLinearLayoutManager!!.itemCount
                    pastVisibleItems = layoutLinearLayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            callApiVendors(currentPage.toString())
                        }
                    }
                }
            }
        })
    }

    //TODO CALL VENDORS API
    private fun callApiVendors(currentPage: String) {
        Common.showLoadingProgress(this@ActVendors)
        val call = ApiClient.getClient.getVendors(currentPage)
        call.enqueue(object : Callback<VendorsResponse> {
            override fun onResponse(
                call: Call<VendorsResponse>,
                response: Response<VendorsResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    Common.dismissLoadingProgress()
                    if (restResponce.status == 1) {
                        if (currentPage == "1") {
                            vendorsDataList.clear()
                        }
                        restResponce.vendors?.data?.let {
                            vendorsDataList.addAll(it)
                        }
                        this@ActVendors.currentPage = restResponce.vendors?.currentPage!!.toInt()
                        this@ActVendors.total_pages = restResponce.vendors.lastPage!!.toInt()
                        vendorsDataList(vendorsDataList)
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActVendors,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<VendorsResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActVendors,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO VENDORS DATA SET
    private fun vendorsDataList(vendorsDataList: ArrayList<VenDorsDataItem>) {
        lateinit var binding: RowVendorsdetailsBinding
        vendorsAdapter = object :
            BaseAdaptor<VenDorsDataItem, RowVendorsdetailsBinding>(
                this@ActVendors,
                vendorsDataList
            ) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: VenDorsDataItem,
                position: Int
            ) {
                binding.tvVendorsName.text = vendorsDataList[position].name
                Glide.with(this@ActVendors).load(vendorsDataList[position].imageUrl).into(binding.ivvendors)
                binding.ivvendors.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                if (vendorsDataList[position].rattings?.avgRatting == null) {
                    binding.tvRatePro.text = "0.0"

                } else {
                    binding.tvRatePro.text =
                        vendorsDataList[position].rattings?.avgRatting.toString()
                }
                holder?.itemView?.setOnClickListener {
                    Log.e("vendor_id--->", vendorsDataList[position].id.toString())
                    val intent = Intent(this@ActVendors, ActVendorsDetails::class.java)
                    intent.putExtra("vendor_id", vendorsDataList[position].id.toString())
                    intent.putExtra("vendors_name", vendorsDataList[position].name.toString())
                    intent.putExtra("vendors_iv", vendorsDataList[position].imageUrl.toString())
                    intent.putExtra("vendors_rate", vendorsDataList[position].rattings?.avgRatting.toString())
                    startActivity(intent)
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_vendorsdetails
            }

            override fun getBinding(parent: ViewGroup): RowVendorsdetailsBinding {
                binding = RowVendorsdetailsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }
        vendorsBinding.rvAllVendors.apply {
            if (vendorsDataList.size > 0) {
                vendorsBinding.rvAllVendors.visibility = View.VISIBLE
                vendorsBinding.tvNoDataFound.visibility = View.GONE
                layoutManager =
                    layoutLinearLayoutManager
                itemAnimator = DefaultItemAnimator()
                adapter = vendorsAdapter
            } else {
                vendorsBinding.rvAllVendors.visibility = View.GONE
                vendorsBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }
}