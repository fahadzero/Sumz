package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.ecommerce.user.databinding.ActAllCategoriesBinding
import com.ecommerce.user.databinding.RowAllCategoriesBinding
import com.ecommerce.user.model.CategoriesResponse
import com.ecommerce.user.model.DataItem
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.isCheckNetwork
import com.ecommerce.user.utils.Common.showLoadingProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ActAllCategories : BaseActivity() {
    private lateinit var allCategoriesBinding: ActAllCategoriesBinding
    var categoriesList: ArrayList<DataItem>? = null
    var categoriesAdaptor: BaseAdaptor<DataItem, RowAllCategoriesBinding>? = null
    var linearLayoutManagerCategories: LinearLayoutManager? = null
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )
    override fun setLayout(): View = allCategoriesBinding.root

    override fun initView() {
        allCategoriesBinding = ActAllCategoriesBinding.inflate(layoutInflater)
        linearLayoutManagerCategories =
            LinearLayoutManager(this@ActAllCategories, LinearLayoutManager.VERTICAL, false)
        if (isCheckNetwork(this@ActAllCategories)) {
            callCategories()
        } else {
            alertErrorOrValidationDialog(
                this@ActAllCategories,
                resources.getString(R.string.no_internet)
            )
        }
        allCategoriesBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
    }

    private fun callCategories() {
        showLoadingProgress(this@ActAllCategories)
        val call = ApiClient.getClient.getcategory()
        call.enqueue(object : Callback<CategoriesResponse> {
            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        categoriesList = restResponce.data
                        loadCategoriesDeals(categoriesList!!)
                    } else {
                        alertErrorOrValidationDialog(
                            this@ActAllCategories,
                            resources.getString(R.string.error_msg)
                        )
                    }
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActAllCategories,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun loadCategoriesDeals(categoriesList: ArrayList<DataItem>) {
        lateinit var binding: RowAllCategoriesBinding
        categoriesAdaptor =
            object : BaseAdaptor<DataItem, RowAllCategoriesBinding>(
                this@ActAllCategories,
                categoriesList
            ) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: DataItem,
                    position: Int
                ) {
                    binding.tvcateitemname.text = categoriesList[position].categoryName
                    Glide.with(this@ActAllCategories)
                        .load(categoriesList[position].imageUrl).into(binding.ivCarttemm)
                    binding.ivCarttemm.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        Log.e("cat_id--->", categoriesList[position].id.toString())
                        val intent = Intent(this@ActAllCategories, ActAllSubCategories::class.java)
                        val extras = Bundle()
                        extras.putString("cat_id", categoriesList[position].id.toString() + "")
                        extras.putString(
                            "categoryName",
                            categoriesList[position].categoryName.toString() + ""
                        )
                        intent.putExtras(extras)
                        startActivity(intent)
                    }
                    binding.clmain.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    binding.card.setCardBackgroundColor(Color.parseColor(colorArray[position % 6]))
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_all_categories
                }

                override fun getBinding(parent: ViewGroup): RowAllCategoriesBinding {
                    binding = RowAllCategoriesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        allCategoriesBinding.rvCategories.apply {
            if (categoriesList.size > 0) {
                allCategoriesBinding.rvCategories.visibility = View.VISIBLE
                allCategoriesBinding.tvNoDataFound.visibility = View.GONE
                layoutManager = linearLayoutManagerCategories
                itemAnimator = DefaultItemAnimator()
                adapter = categoriesAdaptor
            } else {
                allCategoriesBinding.rvCategories.visibility = View.GONE
                allCategoriesBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActAllCategories, false)
    }

}