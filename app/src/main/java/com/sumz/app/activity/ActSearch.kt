package com.sumz.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sumz.app.R
import com.sumz.app.api.ApiClient
import com.sumz.app.base.BaseActivity
import com.sumz.app.base.BaseAdaptor
import com.sumz.app.databinding.ActSearchBinding
import com.sumz.app.databinding.RowSearchBinding
import com.sumz.app.model.*
import com.sumz.app.utils.Common
import com.sumz.app.utils.SharePreference
import com.sumz.app.utils.SharePreference.Companion.getBooleanPref
import com.sumz.app.utils.SharePreference.Companion.getStringPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActSearch : BaseActivity() {
    private lateinit var searchBinding: ActSearchBinding
    var searchList: ArrayList<SearchDataItem> = ArrayList()
    var tempsearchList: ArrayList<SearchDataItem>? = null
    private var viewAllDataAdapter: BaseAdaptor<SearchDataItem, RowSearchBinding>? =
        null
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )
    override fun setLayout(): View = searchBinding.root

    override fun initView() {
        tempsearchList = ArrayList()
        searchBinding = ActSearchBinding.inflate(layoutInflater)
        searchBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        if (Common.isCheckNetwork(this@ActSearch)) {
            callApiSearchList()
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActSearch,
                resources.getString(R.string.no_internet)
            )
        }

        //TODO EDITTEXT VIEW DATA CHANGED METHOD
        searchBinding.edSearch.doAfterTextChanged {
            it?.let {
                searchList.clear()
                val searchText = it.toString()!!.toLowerCase()
                if (searchText.isNotEmpty()) {
                    searchBinding.rvsearch.visibility = View.VISIBLE
                    searchBinding.tvNoDataFound.visibility = View.GONE
                    tempsearchList?.filter {
                        it.productName?.toLowerCase()?.contains(searchText) ?: false
                    }?.let {
                        searchList.addAll(it)
                    }
                } else {
                    searchBinding.rvsearch.visibility = View.GONE
                    searchBinding.tvNoDataFound.visibility = View.VISIBLE
                    tempsearchList?.let { searchList.addAll(it) }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    loadSearchList(searchList)
                }, 100L)
            }
        }
    }

    //TODO CALL SEARCH API
    private fun callApiSearchList() {
        Common.showLoadingProgress(this@ActSearch)
        val hasmap = HashMap<String, String>()
        if (getBooleanPref(this@ActSearch, SharePreference.isLogin)) {
            hasmap["user_id"] = getStringPref(this@ActSearch, SharePreference.userId)!!
        } else {
            hasmap["user_id"] = ""
        }
        val call = ApiClient.getClient.getSearchProducts(hasmap)
        call.enqueue(object : Callback<SearchProductResponse> {
            override fun onResponse(
                call: Call<SearchProductResponse>,
                response: Response<SearchProductResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        restResponce.data?.let {
                            searchList.addAll(it)
                            tempsearchList?.addAll(it)
                        }
                        loadSearchList(searchList)
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActSearch,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SearchProductResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActSearch,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SEARCH DATA SET
    private fun loadSearchList(tempsearchList: ArrayList<SearchDataItem>) {
        lateinit var binding: RowSearchBinding
        viewAllDataAdapter =
            object : BaseAdaptor<SearchDataItem, RowSearchBinding>(
                this@ActSearch,
                tempsearchList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: SearchDataItem,
                    position: Int
                ) {
                    binding.tvsearch.text = tempsearchList[position].productName
                    Glide.with(this@ActSearch)
                        .load(tempsearchList[position].productimage?.imageUrl).into(binding.ivsearchproduct)
                    binding.ivsearchproduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActSearch, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            tempsearchList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }


                override fun setItemLayout(): Int {
                    return R.layout.row_gravity
                }

                override fun getBinding(parent: ViewGroup): RowSearchBinding {
                    binding = RowSearchBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        searchBinding.rvsearch.apply {
            layoutManager =
                LinearLayoutManager(this@ActSearch, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = viewAllDataAdapter
        }
    }
}