package com.ecommerce.user.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.user.R
import com.ecommerce.user.adapter.SubCateAdapter
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActAllSubCategoriesBinding
import com.ecommerce.user.model.SubCategoriesResponse
import com.ecommerce.user.model.SubcategoryItem
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.showLoadingProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class ActAllSubCategories : BaseActivity() {
    private lateinit var allSubCategoriesBinding: ActAllSubCategoriesBinding
    var cateId = ""
    var cartSubList: ArrayList<SubcategoryItem>? = ArrayList()
    var linearLayoutManager: LinearLayoutManager? = null

    override fun setLayout(): View = allSubCategoriesBinding.root

    override fun initView() {
        allSubCategoriesBinding = ActAllSubCategoriesBinding.inflate(layoutInflater)
        allSubCategoriesBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        val extras = intent.extras
        val cat_id = extras!!.getString("cat_id")
        val categoryName = extras.getString("categoryName")
        allSubCategoriesBinding.Subcategoriename.text = categoryName.toString()
        cateId = intent.getStringExtra("cat_id")!!
        if (Common.isCheckNetwork(this@ActAllSubCategories)) {
            if (cat_id != null) {
                callApiSubCategories(cat_id)
            }
        } else {
            alertErrorOrValidationDialog(
                this@ActAllSubCategories,
                resources.getString(R.string.no_internet)
            )
        }
    }

    // TODO API SUB CATEGORIES CALL
    private fun callApiSubCategories(cat_id: String) {
        showLoadingProgress(this@ActAllSubCategories)
        val map = HashMap<String, String>()
        map["cat_id"] = cat_id
        val call = ApiClient.getClient.getSubCategoriesDetail(map)
        call.enqueue(object : Callback<SubCategoriesResponse> {
            override fun onResponse(
                call: Call<SubCategoriesResponse>,
                response: Response<SubCategoriesResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data?.subcategory?.size!! > 0) {
                            allSubCategoriesBinding.rvSubcate.visibility = View.VISIBLE
                            allSubCategoriesBinding.tvNoDataFound.visibility = View.GONE
                            cartSubList = restResponce.data.subcategory
                            val adapter = SubCateAdapter(cartSubList!!)
                            allSubCategoriesBinding.rvSubcate.layoutManager =
                                LinearLayoutManager(this@ActAllSubCategories)
                            allSubCategoriesBinding.rvSubcate.adapter = adapter
                        } else {
                            allSubCategoriesBinding.rvSubcate.visibility = View.GONE
                            allSubCategoriesBinding.tvNoDataFound.visibility = View.VISIBLE
                            dismissLoadingProgress()
                            alertErrorOrValidationDialog(
                                this@ActAllSubCategories,
                                restResponce.message.toString()
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SubCategoriesResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActAllSubCategories,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActAllSubCategories, false)

    }
}