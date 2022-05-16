package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActViewAllBinding
import com.ecommerce.user.databinding.RowViewallBinding
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActViewAll : BaseActivity() {

    private lateinit var viewAllBinding: ActViewAllBinding
    private var viewAllDataList = ArrayList<ViewAllDataItem>()
    private var filterAllDataList = ArrayList<FilterDataItem>()
    private var productAllDataList = ArrayList<ProductDataItem>()
    var currency: String = ""
    var currencyPosition: String = ""
    private var viewAllDataAdapter: BaseAdaptor<ViewAllDataItem, RowViewallBinding>? =
        null
    private var filterAllDataAdapter: BaseAdaptor<FilterDataItem, RowViewallBinding>? =
        null
    private var productAllDataAdapter: BaseAdaptor<ProductDataItem, RowViewallBinding>? =
        null
    private var gridLayoutManager: GridLayoutManager? = null
    private var gridLayoutManagerFilter: GridLayoutManager? = null
    private var gridLayoutManagerProduct: GridLayoutManager? = null
    private var pos = 0
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

    override fun setLayout(): View = viewAllBinding.root

    override fun initView() {
        viewAllBinding = ActViewAllBinding.inflate(layoutInflater)
        gridLayoutManager = GridLayoutManager(this@ActViewAll, 2, GridLayoutManager.VERTICAL, false)
        gridLayoutManagerFilter =
            GridLayoutManager(this@ActViewAll, 2, GridLayoutManager.VERTICAL, false)
        gridLayoutManagerProduct =
            GridLayoutManager(this@ActViewAll, 2, GridLayoutManager.VERTICAL, false)
        currency = SharePreference.getStringPref(this@ActViewAll, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(this@ActViewAll, SharePreference.CurrencyPosition)!!
        var title: String? = intent.getStringExtra("title")
        var type: String = ""
        viewAllBinding.tvviewall.text = title.toString()
        loadFeaturedProducts(viewAllDataList)
        loadFilter(filterAllDataList)
        loadProductData(productAllDataList)
        if (Common.isCheckNetwork(this@ActViewAll)) {
            if (title == "Featured Products") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
                title = "featured_products"
                viewAllData(currentPage.toString(), title.toString())
            } else if (title == "New Arrivals") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
                title = "new_products"
                viewAllData(currentPage.toString(), title.toString())
            } else if (title == "Hot Deals") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
                title = "hot_products"
                viewAllData(currentPage.toString(), title.toString())
            } else {
                productData(currentPage.toString())
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this,
                resources.getString(R.string.no_internet)
            )
        }

        viewAllBinding.ivBack.setOnClickListener {
            finish()
        }
        viewAllBinding.ivFilter.setOnClickListener {
            filterAllDataList.clear()
            val dialog = BottomSheetDialog(this@ActViewAll)
            if (Common.isCheckNetwork(this@ActViewAll)) {
                val view =
                    layoutInflater.inflate(R.layout.row_bottomsheetsortby, null)
                val latest = view.findViewById<TextView>(R.id.tvlatest)
                val pricelowtohigh = view.findViewById<TextView>(R.id.tvpricelowtohigh)
                val pricehightolow = view.findViewById<TextView>(R.id.tvpricehightolow)
                val rattinglowtohigh = view.findViewById<TextView>(R.id.tvrattinglowtohigh)
                val rattinghightolow = view.findViewById<TextView>(R.id.tvrattinghightolow)
                val close = view.findViewById<ImageView>(R.id.iv_close)

                latest.setOnClickListener {
                    type = "new"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                pricelowtohigh.setOnClickListener {
                    type = "price-low-to-high"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                pricehightolow.setOnClickListener {
                    type = "price-high-to-low"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {

                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                rattinglowtohigh.setOnClickListener {
                    type = "ratting-low-to-high"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                rattinghightolow.setOnClickListener {
                    type = "ratting-high-to-low"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {

                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                close.setOnClickListener { dialog.dismiss() }
                dialog.setCancelable(false)
                dialog.setContentView(view)
                dialog.show()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.no_internet)
                )
                dialog.dismiss()
            }
        }

        viewAllBinding.rvViewall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager!!.childCount
                    totalItemCount = gridLayoutManager!!.itemCount
                    pastVisibleItems = gridLayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            viewAllData(currentPage.toString(), title.toString())
                        }
                    }
                }
            }
        })
        viewAllBinding.rvFilterall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManagerFilter!!.childCount
                    totalItemCount = gridLayoutManagerFilter!!.itemCount
                    pastVisibleItems = gridLayoutManagerFilter!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            callApiFilter(currentPage.toString(), type)
                        }
                    }
                }
            }
        })
        viewAllBinding.rvProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManagerProduct!!.childCount
                    totalItemCount = gridLayoutManagerProduct!!.itemCount
                    pastVisibleItems = gridLayoutManagerProduct!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            productData(currentPage.toString())
                        }
                    }
                }
            }
        })
    }

    //TODO CALL PRODUCT API
    private fun productData(currentPage: String) {
        viewAllBinding.rvProduct.visibility = View.VISIBLE
        viewAllBinding.rvViewall.visibility = View.GONE
        viewAllBinding.rvFilterall.visibility = View.GONE
        Common.showLoadingProgress(this@ActViewAll)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] = SharePreference.getStringPref(this@ActViewAll, SharePreference.userId)!!
        hasmap["innersubcategory_id"] =
            intent.getSerializableExtra("innersubcategory_id").toString()
        val call = ApiClient.getClient.getProduct(currentPage, hasmap)
        call.enqueue(object : Callback<ProductResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == "1") {
                            productAllDataList.clear()
                        }
                        restResponce.data?.data?.let {
                            productAllDataList.addAll(it)
                        }
                        this@ActViewAll.currentPage = restResponce.data?.currentPage!!.toInt()
                        this@ActViewAll.total_pages = restResponce.data.lastPage!!.toInt()
                        productAllDataAdapter?.notifyDataSetChanged()
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()

                        if (restResponce.message == "No data found") {
                            viewAllBinding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO PRODUCT DATA SET
    private fun loadProductData(productAllDataList: ArrayList<ProductDataItem>) {
        lateinit var binding: RowViewallBinding
        productAllDataAdapter =
            object : BaseAdaptor<ProductDataItem, RowViewallBinding>(
                this@ActViewAll,
                productAllDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ProductDataItem,
                    position: Int
                ) {

                    if (productAllDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            productAllDataList[position].rattings?.get(0)?.avgRatting?.toDouble()
                                .toString()
                    }

                    if (productAllDataList.get(position).isWishlist == 0) {
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
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            if (productAllDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    productAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiFavouriteProduct(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (productAllDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    productAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiRemoveFavouriteProduct(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }


                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    }

                    binding.tvProductName.text = productAllDataList[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    productAllDataList[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    productAllDataList[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                productAllDataList[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                productAllDataList[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    Glide.with(this@ActViewAll)
                        .load(productAllDataList[position].productimage?.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActViewAll, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            productAllDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }

                override fun getBinding(parent: ViewGroup): RowViewallBinding {
                    binding = RowViewallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        viewAllBinding.rvProduct.apply {
            layoutManager = gridLayoutManagerProduct
            itemAnimator = DefaultItemAnimator()
            adapter = productAllDataAdapter
        }
    }

    //TODO CALL FILTER API
    private fun callApiFilter(userId: String, type: String) {
        Common.showLoadingProgress(this@ActViewAll)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] = userId
        hasmap["type"] = type
        val call = ApiClient.getClient.getFilter(currentPage.toString(), hasmap)
        call.enqueue(object : Callback<GetFilterResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetFilterResponse>,
                response: Response<GetFilterResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == 1) {
                            filterAllDataList.clear()
                        }
                        restResponce.data?.data?.let {
                            filterAllDataList.addAll(it)
                        }
                        this@ActViewAll.total_pages = restResponce.data?.lastPage!!.toInt()
                        filterAllDataAdapter?.notifyDataSetChanged()
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()

                        if (restResponce.message == "No data found") {
                            viewAllBinding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetFilterResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO FILTER DATA SET
    private fun loadFilter(filterAllDataList: ArrayList<FilterDataItem>) {
        lateinit var binding: RowViewallBinding
        filterAllDataAdapter =
            object : BaseAdaptor<FilterDataItem, RowViewallBinding>(
                this@ActViewAll,
                filterAllDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FilterDataItem,
                    position: Int
                ) {
                    if (filterAllDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            filterAllDataList[position].rattings?.get(0)?.avgRatting?.toDouble()
                                .toString()
                    }
                    if (filterAllDataList.get(position).isWishlist == 0) {
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
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            if (filterAllDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    filterAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiFavouriteFilter(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (filterAllDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    filterAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiRemoveFavouriteFilter(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    }

                    binding.tvProductName.text = filterAllDataList[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    filterAllDataList[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    filterAllDataList[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                filterAllDataList[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                filterAllDataList[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    Glide.with(this@ActViewAll)
                        .load(filterAllDataList[position].productimage?.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActViewAll, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            filterAllDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }

                override fun getBinding(parent: ViewGroup): RowViewallBinding {
                    binding = RowViewallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        viewAllBinding.rvFilterall.apply {
            layoutManager = gridLayoutManagerFilter
            itemAnimator = DefaultItemAnimator()
            adapter = filterAllDataAdapter
        }
    }

    //TODO CALL VIEW ALL API
    private fun viewAllData(currentPage: String, title: String) {
        Common.showLoadingProgress(this@ActViewAll)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] = SharePreference.getStringPref(this@ActViewAll, SharePreference.userId)!!
        hasmap["type"] = title
        val call = ApiClient.getClient.setViewAllListing(currentPage, hasmap)
        call.enqueue(object : Callback<ViewAllListResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ViewAllListResponse>,
                response: Response<ViewAllListResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == "1") {
                            viewAllDataList.clear()
                        }
                        restResponce.alldata?.data?.let {
                            viewAllDataList.addAll(it)
                        }
                        this@ActViewAll.total_pages = restResponce.alldata?.lastPage!!.toInt()
                        viewAllDataAdapter?.notifyDataSetChanged()
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()

                        if (restResponce.message == "No data found") {
                            viewAllBinding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ViewAllListResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO VIEW ALL DATA SET
    private fun loadFeaturedProducts(viewAllDataList: ArrayList<ViewAllDataItem>) {
        lateinit var binding: RowViewallBinding
        viewAllDataAdapter =
            object : BaseAdaptor<ViewAllDataItem, RowViewallBinding>(
                this@ActViewAll,
                viewAllDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ViewAllDataItem,
                    position: Int
                ) {
                    if (viewAllDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            viewAllDataList[position].rattings?.get(0)?.avgRatting?.toDouble()
                                .toString()
                    }
                    if (viewAllDataList[position].isWishlist == 0) {
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
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            if (viewAllDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    viewAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                            if (viewAllDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    viewAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiRemoveFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    }
                    binding.tvProductName.text = viewAllDataList[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    viewAllDataList[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    viewAllDataList[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                viewAllDataList[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                viewAllDataList[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    Glide.with(this@ActViewAll)
                        .load(viewAllDataList[position].productimage?.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActViewAll, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            viewAllDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }
                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }
                override fun getBinding(parent: ViewGroup): RowViewallBinding {
                    binding = RowViewallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        viewAllBinding.rvViewall.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAllDataAdapter
        }
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
        val call = ApiClient.getClient.setRemoveFromWishList(map)
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
                        viewAllDataList[position].isWishlist = 0
                        viewAllDataAdapter!!.notifyItemChanged(position)

                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL FAVOURITE API
    private fun callApiFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
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
                        viewAllDataList[position].isWishlist = 1
                        viewAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavouriteProduct(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
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
                        productAllDataList[position].isWishlist = 0
                        productAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL FAVOURITE API
    private fun callApiFavouriteProduct(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
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
                        productAllDataList[position].isWishlist = 1
                        productAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavouriteFilter(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
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
                        filterAllDataList[position].isWishlist = 0
                        filterAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL FAVOURITE API
    private fun callApiFavouriteFilter(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
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
                        filterAllDataList[position].isWishlist = 1
                        filterAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}