package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.ecommerce.user.R
import com.ecommerce.user.adapter.VariationAdapter
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.*
import com.ecommerce.user.model.*
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.isCheckNetwork
import com.ecommerce.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActProductDetails : BaseActivity() {

    private lateinit var productDetailsBinding: ActProductDetailsBinding
    var productDetailsList: ProductDetailsData? = null
    var currency: String = ""
    var currencyPosition: String = ""
    var isAPICalling: Boolean = false
    var taxpercent: String = ""
    var productprice: Double = 0.0
    var addtax: String = ""
    var price: String = ""
    var variation: String = ""
    var pos: Int = 0
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )
    private var viewAllDataAdapter: BaseAdaptor<RelatedProductsItem, RowFeaturedproductBinding>? =
        null
    var paymenttype: String = ""
    private var productimageDataAdapter: BaseAdaptor<ProductimagesItem, RowProductviewpagerBinding>? =
        null
    private var variationAdaper: VariationAdapter? = null
    private var variationList = ArrayList<VariationsItem>()
    override fun setLayout(): View = productDetailsBinding.root

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView() {
        productDetailsBinding = ActProductDetailsBinding.inflate(layoutInflater)
        setupProductVariationAdapter(variationList)
        if (isCheckNetwork(this@ActProductDetails)) {
            callApiProductDetail(intent.getStringExtra("product_id")!!)
        } else {
            alertErrorOrValidationDialog(
                this@ActProductDetails,
                resources.getString(R.string.no_internet)
            )
        }
        currency =
            SharePreference.getStringPref(this@ActProductDetails, SharePreference.Currency) ?: ""
        currencyPosition =
            SharePreference.getStringPref(
                this@ActProductDetails,
                SharePreference.CurrencyPosition
            )!!
        productDetailsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        productDetailsBinding.btnaddtocart.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActProductDetails, SharePreference.isLogin)) {
                productDetailsList?.let { it1 ->
                    apiaddtocart(
                        it1, variation
                    )
                }
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        }
    }

    //TODO CALL PRODUCT DETAILS API
    private fun callApiProductDetail(productId: String) {
        Common.showLoadingProgress(this@ActProductDetails)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActProductDetails, SharePreference.userId)!!
        hasmap["product_id"] = productId
        val call = ApiClient.getClient.getProductDetails(hasmap)
        call.enqueue(object : Callback<GetProductDetailsResponse> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<GetProductDetailsResponse>,
                response: Response<GetProductDetailsResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        productDetailsList = restResponce.data
                        loadProductDetails(productDetailsList!!)
                        restResponce.relatedProducts?.let { loadRelatedProducts(it) }
                        restResponce.vendors?.let { loadVendorsData(it) }
                        productDetailsBinding.clReturnpolicy.setOnClickListener {
                            val intent = Intent(this@ActProductDetails, ActReturnPolicy::class.java)
                            intent.putExtra(
                                "return_policies",
                                restResponce.returnpolicy?.returnPolicies.toString()
                            )
                            startActivity(intent)
                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProductDetailsResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActProductDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO VENDORS DATA SET
    private fun loadVendorsData(vendors: Vendors) {
        productDetailsBinding.tvVendorsName.text = vendors.name
        Glide.with(this@ActProductDetails)
            .load(vendors.imageUrl).into(productDetailsBinding.ivvendors)
        /* productDetailsBinding.ivvendors.setBackgroundColor(Color.parseColor(colorArray[pos % 6]))*/
        if (vendors.rattings == null) {
            productDetailsBinding.tvvendorsrate.text =
                "0.0"
        } else {
            productDetailsBinding.tvvendorsrate.text =
                vendors.rattings.avgRatting?.toString() ?: ""
        }
        productDetailsBinding.tvvisitstore.setOnClickListener {
            val intent = Intent(this@ActProductDetails, ActVendorsDetails::class.java)
            intent.putExtra("vendor_id", vendors.id?.toString())
            intent.putExtra("vendors_name", vendors.name)
            intent.putExtra("vendors_iv", vendors.imageUrl)
            intent.putExtra("vendors_rate", vendors.rattings?.avgRatting?.toString())
            startActivity(intent)
        }
    }

    //TODO RELATED PRODUCT DETAILS DATA SET
    private fun loadRelatedProducts(relatedProducts: ArrayList<RelatedProductsItem>) {
        lateinit var binding: RowFeaturedproductBinding
        viewAllDataAdapter =
            object : BaseAdaptor<RelatedProductsItem, RowFeaturedproductBinding>(
                this@ActProductDetails,
                relatedProducts
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: RelatedProductsItem,
                    position: Int
                ) {
                    if (relatedProducts.get(position).isWishlist == 0) {
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
                                this@ActProductDetails,
                                SharePreference.isLogin
                            )
                        ) {
                            if (relatedProducts[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    relatedProducts[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActProductDetails,
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(this@ActProductDetails)) {
                                    callApiFavourite(map, position, relatedProducts)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@ActProductDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                            if (relatedProducts[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    relatedProducts[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActProductDetails,
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(this@ActProductDetails)) {
                                    callApiRemoveFavourite(map, position, relatedProducts)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@ActProductDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActLogin::class.java)
                            finish()
                        }
                    }
                    if (relatedProducts[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            relatedProducts[position].rattings?.get(0)?.avgRatting?.toString()
                    }
                    binding.tvProductName.text = relatedProducts[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.02f",
                                    relatedProducts[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.02f",
                                    relatedProducts[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.02f",
                                relatedProducts[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.02f",
                                relatedProducts[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    Glide.with(this@ActProductDetails)
                        .load(relatedProducts[position].productimage?.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        Log.e(
                            "product_id--->",
                            relatedProducts[position].productimage?.productId.toString()
                        )
                        val intent = Intent(this@ActProductDetails, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            relatedProducts[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }

                override fun getBinding(parent: ViewGroup): RowFeaturedproductBinding {
                    binding = RowFeaturedproductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        productDetailsBinding.rvstorelist.apply {
            layoutManager =
                GridLayoutManager(this@ActProductDetails, 1, GridLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = viewAllDataAdapter
        }
    }

    //TODO PRODUCT DETAILS DATA SET
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint(
        "ResourceAsColor", "SetTextI18n", "NotifyDataSetChanged",
        "UseCompatLoadingForDrawables"
    )
    private fun loadProductDetails(productDetailsList: ProductDetailsData) {
        if (productDetailsList.rattings?.size == 0) {
            productDetailsBinding.tvRatePro.text =
                "0.0"
        } else {
            productDetailsBinding.tvRatePro.text =
                productDetailsList.rattings?.get(0)?.avgRatting?.toString()
        }
        paymenttype = productDetailsList.taxType ?: ""
        productDetailsBinding.tvproduct.text =
            productDetailsList.categoryName + " | " + productDetailsList.subcategoryName + " | " + productDetailsList.innersubcategoryName
        productDetailsBinding.tvproducttitle.text = productDetailsList.productName
        productDetailsBinding.tvBarProductTitle.text = productDetailsList.productName
        if (productDetailsList.returnDays == "0") {
            productDetailsBinding.clReturnpolicy.visibility = View.GONE
            productDetailsBinding.viewreturn.visibility = View.GONE
        } else {
            productDetailsBinding.tvreturnpilicy.text =
                productDetailsList.returnDays + " " + getString(R.string.day) + " " + getString(R.string.return_policies)
        }
        if (currencyPosition == "left") {
            productDetailsBinding.tvProductPrice.text =
                currency.plus(
                    String.format(
                        Locale.US,
                        "%,.02f",
                        productDetailsList.productPrice!!.toDouble()
                    )
                )
            productDetailsBinding.tvProductDisprice.text =
                currency.plus(
                    String.format(
                        Locale.US,
                        "%,.02f",
                        productDetailsList.discountedPrice!!.toDouble()
                    )
                )
            if (productDetailsList.freeShipping == 1) {
                productDetailsBinding.tvshoppingcharge.text = currency.plus("0.00")
            } else if (productDetailsList.freeShipping == 2) {
                productDetailsBinding.tvshoppingcharge.text = currency.plus(
                    String.format(
                        Locale.US,
                        "%,.02f",
                        productDetailsList.shippingCost!!.toDouble()
                    )
                )
            }
        } else {
            productDetailsBinding.tvProductPrice.text =
                (String.format(
                    Locale.US,
                    "%,.02f",
                    productDetailsList.productPrice!!.toDouble()
                )) + "" + currency
            productDetailsBinding.tvProductDisprice.text =
                (String.format(
                    Locale.US,
                    "%,.02f",
                    productDetailsList.discountedPrice!!.toDouble()
                )) + "" + currency
            if (productDetailsList.freeShipping == 1) {
                productDetailsBinding.tvshoppingcharge.text = (String.format(
                    Locale.US,
                    "%,.02f",
                    0.toDouble()
                )) + "" + currency
            } else if (productDetailsList.freeShipping == 2) {
                productDetailsBinding.tvshoppingcharge.text = (String.format(
                    Locale.US,
                    "%,.02f",
                    productDetailsList.shippingCost!!.toDouble()
                )) + "" + currency
            }
        }
        Log.d("isVariation", productDetailsList.isVariation.toString())
        Log.d("availableStock", productDetailsList.availableStock.toString())
        if (productDetailsList.isVariation == 1) {
            if (productDetailsList.variations?.get(0)?.qty!! == "0") {
                productDetailsBinding.tvInstock.text = getString(R.string.outofstock)
                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.red))
                productDetailsBinding.btnaddtocart.background =
                    getDrawable(R.drawable.round_gray_bg_9)
                productDetailsBinding.btnaddtocart.isClickable = false
            } else {
                productDetailsBinding.tvInstock.text = getString(R.string.in_stock)
                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.green))
                productDetailsBinding.btnaddtocart.background =
                    getDrawable(R.drawable.round_blue_bg_9)
                productDetailsBinding.btnaddtocart.isClickable = true
            }
        } else if (productDetailsList.isVariation == 0) {
            if (productDetailsList.availableStock == "0") {
                productDetailsBinding.tvInstock.text = getString(R.string.outofstock)
                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.red))
                productDetailsBinding.btnaddtocart.background =
                    getDrawable(R.drawable.round_gray_bg_9)
                productDetailsBinding.btnaddtocart.isClickable = false
            } else {
                productDetailsBinding.tvInstock.text = getString(R.string.in_stock)
                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.green))
                productDetailsBinding.btnaddtocart.background =
                    getDrawable(R.drawable.round_blue_bg_9)
                productDetailsBinding.btnaddtocart.isClickable = true
            }
        }
        if (productDetailsList.isWishlist == 0) {
            productDetailsBinding.ivwishlist.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_dislike,
                    null
                )
            )
        } else {
            productDetailsBinding.ivwishlist.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_like,
                    null
                )
            )
        }
        productDetailsBinding.ivwishlist.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActProductDetails, SharePreference.isLogin)) {
                if (productDetailsList.isWishlist == 0) {
                    val map = HashMap<String, String>()
                    map["product_id"] =
                        productDetailsList.id?.toString()!!
                    map["user_id"] = SharePreference.getStringPref(
                        this@ActProductDetails,
                        SharePreference.userId
                    )!!
                    if (isCheckNetwork(this@ActProductDetails)) {
                        callApiFavouritePro(map)
                    } else {
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
                if (productDetailsList.isWishlist == 1) {
                    val map = HashMap<String, String>()
                    map["product_id"] =
                        productDetailsList.id?.toString()!!
                    map["user_id"] = SharePreference.getStringPref(
                        this@ActProductDetails,
                        SharePreference.userId
                    )!!

                    if (isCheckNetwork(this@ActProductDetails)) {
                        callApiRemoveFavouritePro(map)
                    } else {
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
            } else {
                openActivity(ActLogin::class.java)
                finish()
            }
        }
        productDetailsBinding.tvcode.text = productDetailsList.sku
        if (productDetailsList.attribute == null) {
            productDetailsBinding.tvproductdesc.visibility = View.GONE
            productDetailsBinding.rvproductSize.visibility = View.GONE
        } else {
            productDetailsBinding.tvproductdesc.visibility = View.VISIBLE
            productDetailsBinding.tvproductdesc.text = productDetailsList.attribute
            productDetailsBinding.rvproductSize.visibility = View.VISIBLE
        }
        taxpercent = productDetailsList.tax!!
        Log.d("Taxper--->", taxpercent)
        productprice = productDetailsList.productPrice.toDouble()
        Log.d("Price--->", productprice.toString())
        val tax = productprice * taxpercent.toDouble() / 100
        Log.d("Tax--->", tax.toString())
        addtax = if (paymenttype == "amount") {
            productDetailsList.tax
        } else {
            tax.toString()
        }
        if (currencyPosition == "left") {
            if (productDetailsList.tax != "0") {
                Log.d("tax", productDetailsList.tax)
                productDetailsBinding.tvaddtax.text =
                    currency.plus(
                        String.format(
                            Locale.US,
                            "%,.02f",
                            addtax.toDouble()
                        )
                    ) + " " + getString(R.string.add_tax)
                productDetailsBinding.tvaddtax.setTextColor(getColor(R.color.red))
            } else {
                productDetailsBinding.tvaddtax.setTextColor(getColor(R.color.green))
                productDetailsBinding.tvaddtax.text = getString(R.string.inclusive_all_taxes)
            }
        } else {
            if (productDetailsList.tax != "0") {
                productDetailsBinding.tvaddtax.text =
                    (String.format(
                                Locale.US,
                                "%,.02f",
                                addtax.toDouble()
                            )
                            ) + currency.plus(" Additional ").plus("Tax")
                productDetailsBinding.tvaddtax.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.red,
                        null
                    )
                )
            } else {
                productDetailsBinding.tvaddtax.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    )
                )
                productDetailsBinding.tvaddtax.text = "Inclusive All Taxes"
            }
        }
        if (productDetailsList.freeShipping == 1) {
            productDetailsBinding.tvshoppingcharge.visibility = View.GONE
            productDetailsBinding.tvshoppingchargetitle.text = "Free Shipping"
        } else {
            productDetailsBinding.tvshoppingcharge.visibility = View.VISIBLE
            if (currencyPosition == "left") {
                productDetailsBinding.tvshoppingcharge.text =
                    currency.plus(
                        String.format(
                            Locale.US,
                            "%,.02f",
                            productDetailsList.shippingCost!!.toDouble()
                        )
                    )
            } else {
                productDetailsBinding.tvshoppingcharge.text =
                    (
                            String.format(
                                Locale.US,
                                "%,.02f",
                                productDetailsList.shippingCost!!.toDouble()
                            ) + currency
                            )
            }
        }
        productDetailsBinding.tvshoppingday.text = productDetailsList.estShippingDays + " " + "Day"
        productDetailsBinding.clDescripition.setOnClickListener {
            val intent = Intent(this@ActProductDetails, ActProductDescription::class.java)
            intent.putExtra("description", productDetailsList.description.toString())
            startActivity(intent)
        }
        productDetailsList.variations?.let {
            if (it.size > 0) {
                it.get(0).isSelect = true
                setupPriceData(it[0])
            }
            variationList.clear()
            variationList.addAll(it)
            variationAdaper?.notifyDataSetChanged()
        }
        if (variationList.size > 0) {
            productDetailsBinding.rvproductSize.visibility = View.VISIBLE
            productDetailsBinding.tvproductdesc.visibility = View.VISIBLE
        } else {
            productDetailsBinding.rvproductSize.visibility = View.GONE
            productDetailsBinding.tvproductdesc.visibility = View.GONE
        }
        val imageList = ArrayList<SlideModel>()
        for (i in 0 until productDetailsList.productimages?.size!!) {
            val slideModel = SlideModel(productDetailsList.productimages[i].imageUrl)
            imageList.add(slideModel)
        }
        productDetailsBinding.imageSlider.setImageList(imageList)
        productDetailsBinding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                val intent = Intent(this@ActProductDetails, ActImageSlider::class.java)
                intent.putParcelableArrayListExtra("imageList", productDetailsList.productimages)
                startActivity(intent)
            }
        })
        productDetailsBinding.imageSlider.setOnClickListener {
        }
        productDetailsBinding.clReviews.setOnClickListener {
            val intent = Intent(this@ActProductDetails, ActReviews::class.java)
            intent.putExtra("product_id", productDetailsList.id.toString())
            startActivity(intent)
        }
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavourite(
        map: HashMap<String, String>,
        position: Int,
        relatedProducts: ArrayList<RelatedProductsItem>
    ) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        Common.showLoadingProgress(this@ActProductDetails)
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
                        relatedProducts[position].isWishlist = 0
                        viewAllDataAdapter!!.notifyItemChanged(position)

                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActProductDetails,
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    //TODO CALL API FAVOURITE
    private fun callApiFavourite(
        map: HashMap<String, String>,
        position: Int,
        relatedProducts: ArrayList<RelatedProductsItem>
    ) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        Common.showLoadingProgress(this@ActProductDetails)
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
                        relatedProducts[position].isWishlist = 1
                        viewAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActProductDetails,
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }


    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavouritePro(map: HashMap<String, String>) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        Common.showLoadingProgress(this@ActProductDetails)
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
                        productDetailsList?.isWishlist = 0
                        onResume()
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActProductDetails,
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    //TODO CALL API FAVOURITE
    private fun callApiFavouritePro(map: HashMap<String, String>) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        Common.showLoadingProgress(this@ActProductDetails)
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
                        productDetailsList?.isWishlist = 1
                        onResume()
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActProductDetails,
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    private fun loadProductImage(productimages: ArrayList<ProductimagesItem>) {
        lateinit var binding: RowProductviewpagerBinding
        productimageDataAdapter =
            object : BaseAdaptor<ProductimagesItem, RowProductviewpagerBinding>(
                this@ActProductDetails,
                productimages
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ProductimagesItem,
                    position: Int
                ) {
                    Glide.with(this@ActProductDetails)
                        .load(productimages[position].imageUrl)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_productviewpager
                }

                override fun getBinding(parent: ViewGroup): RowProductviewpagerBinding {
                    binding = RowProductviewpagerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        productDetailsBinding.viewPager.apply {
            productDetailsBinding.viewPager.adapter =
                StartScreenAdapter(this@ActProductDetails, productimages)
            productDetailsBinding.tabLayout.setupWithViewPager(
                productDetailsBinding.viewPager,
                true
            )
        }
    }

    class StartScreenAdapter(
        private var mContext: Context,
        private var mImagelist: ArrayList<ProductimagesItem>
    ) :
        PagerAdapter() {
        @SuppressLint("SetTextI18n")
        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mContext)
            val layout =
                inflater.inflate(R.layout.row_productviewpager, collection, false) as ViewGroup
            val iv: ImageView = layout.findViewById(R.id.ivProduct)
            Glide.with(mContext).load(mImagelist[position].imageUrl).into(iv)
            iv.setOnClickListener {
                val intent = Intent(mContext, ActImageSlider::class.java)
                intent.putParcelableArrayListExtra("imageList", mImagelist)
                mContext.startActivity(intent)
            }
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(
            collection: ViewGroup,
            position: Int,
            view: Any
        ) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return mImagelist.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    private fun apiaddtocart(
        productDetailsList: ProductDetailsData,
        variation: String
    ) {
        Common.showLoadingProgress(this@ActProductDetails)
        productprice = productprice
        val attribute: String?
        var tax = productprice * taxpercent.toDouble() / 100
        addtax = tax.toString()
        if (productDetailsList.attribute == "null") {
            attribute = ""
        } else {
            attribute = productDetailsList.attribute
        }
        paymenttype = productDetailsList.taxType ?: ""
        if (productDetailsList.taxType == "amount") {
            tax = productDetailsList.tax?.toDouble() ?: 0.0
        } else {
            tax = addtax.toDouble()
        }
        var shippingcost = ""
        if (productDetailsList.freeShipping == 1) {
            shippingcost = "0.00"
        } else if (productDetailsList.freeShipping == 2) {
            shippingcost = productDetailsList.shippingCost?.toString()!!
        }
        val hasmap = HashMap<String, String>()
        hasmap["product_id"] = productDetailsList.id?.toString()!!
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActProductDetails, SharePreference.userId)!!
        hasmap["vendor_id"] = productDetailsList.vendorId?.toString()!!
        hasmap["product_name"] = productDetailsList.productName!!
        hasmap["qty"] = "1"
        hasmap["price"] = productprice.toString()
        hasmap["variation"] = variation
        hasmap["shipping_cost"] = shippingcost
        hasmap["image"] = productDetailsList.productimages?.get(0)?.imageName!!
        hasmap["tax"] = tax.toString()
        hasmap["attribute"] = attribute ?: ""
        val call = ApiClient.getClient.getAddtocart(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        Common.isAddOrUpdated = true
                        if (isCheckNetwork(this@ActProductDetails)) {
                            successDialogBottomSheet()
                        } else {
                            alertErrorOrValidationDialog(
                                this@ActProductDetails,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else {
                        response.body()?.message?.let {
                            Common.showErrorFullMsg(
                                this@ActProductDetails,
                                it
                            )
                        }
                    }
                } else {
                    alertErrorOrValidationDialog(
                        this@ActProductDetails,
                        resources.getString(R.string.error_cart)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActProductDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun successDialogBottomSheet() {
        val successDialogBinding = SuccessBottomsheetDialogBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(this@ActProductDetails)
        dialog.setContentView(successDialogBinding.root)

        successDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        successDialogBinding.btnGotoCart.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@ActProductDetails, ActMain::class.java)
            intent.putExtra("pos", "3")
            startActivity(intent)
            finish()
        }
        successDialogBinding.btncontinueshopping.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@ActProductDetails, ActMain::class.java)
            intent.putExtra("temp", 0)
            startActivity(intent)
            finish()
        }
        dialog.show()

    }

    @SuppressLint("InflateParams")
    fun dlgAddtoCartConformationDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_addtocart, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvDesc)
            textDesc.text = msg
            val tvContinuation: TextView = mView.findViewById(R.id.tvcontinueshooping)
            val finalDialog: Dialog = dialog
            tvContinuation.setOnClickListener {
                if (isCheckNetwork(this@ActProductDetails)) {
                    finalDialog.dismiss()
                    val intent = Intent(this@ActProductDetails, ActMain::class.java)
                    intent.putExtra("temp", 0)
                    startActivity(intent)
                } else {
                    alertErrorOrValidationDialog(
                        this@ActProductDetails,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            val tvGoToCart: TextView = mView.findViewById(R.id.tvgotocart)
            tvGoToCart.setOnClickListener {
                val intent = Intent(this@ActProductDetails, ActMain::class.java)
                intent.putExtra("pos", "3")
                startActivity(intent)
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setupPriceData(data: VariationsItem) {
        variation = if (data.variation == "") {
            ""
        } else {
            data.variation ?: ""
        }
        if (currencyPosition == "left") {
            productDetailsBinding.tvProductPrice.text =
                currency.plus(String.format(Locale.US, "%,.02f", data.price?.toDouble()))
            productDetailsBinding.tvProductDisprice.text =
                currency.plus(
                    String.format(
                        Locale.US,
                        "%,.02f",
                        data.discountedVariationPrice!!.toDouble()
                    )
                )
        } else {
            productDetailsBinding.tvProductPrice.text =
                String.format(
                    Locale.US,
                    "%,.02f",
                    data.price!!.toDouble()
                ).plus(currency)
            productDetailsBinding.tvProductDisprice.text =
                String.format(
                    Locale.US,
                    "%,.02f",
                    data.discountedVariationPrice!!.toDouble()
                ).plus(currency)
        }

        productprice = data.price?.toDouble()!!
        Log.d("clickprice", productprice.toString())
        val tax = productprice * taxpercent.toDouble() / 100
        addtax = tax.toString()
        if (currencyPosition == "left") {
            if (productDetailsList?.tax != "0") {
                productDetailsBinding.tvaddtax.text =
                    currency.plus(
                        String.format(
                            Locale.US,
                            "%,.02f",
                            addtax.toDouble()
                        )
                    ).plus(" ").plus(getString(R.string.additional)).plus(getString(R.string.tax))
                productDetailsBinding.tvaddtax.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.red,
                        null
                    )
                )
            } else {
                productDetailsBinding.tvaddtax.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    )
                )
                productDetailsBinding.tvaddtax.text = getString(R.string.inclusive_all_taxes)
            }
        } else {
            if (productDetailsList?.tax != "0") {
                productDetailsBinding.tvaddtax.text =
                    (
                            String.format(
                                Locale.US,
                                "%,.02f",
                                addtax.toDouble()
                            )
                            ) + currency.plus("").plus(getString(R.string.additional))
                        .plus(getString(R.string.tax))
                productDetailsBinding.tvaddtax.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.red,
                        null
                    )
                )
            } else {
                productDetailsBinding.tvaddtax.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    )
                )
                productDetailsBinding.tvaddtax.text = getString(R.string.inclusive_all_taxes)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("NotifyDataSetChanged")
    private fun setupProductVariationAdapter(variationList: ArrayList<VariationsItem>) {
        variationAdaper = VariationAdapter(
            this@ActProductDetails,
            variationList, taxpercent, productDetailsList?.tax ?: "0.00", productDetailsBinding
        ) { i: Int, s: String ->
            if (s == "ItemClick") {
                for (item in variationList) {
                    item.isSelect = false
                    if (productDetailsList?.isVariation == 1) {
                        if (variationList[i].qty == "0") {
                            Log.d("qty", variationList[pos].qty.toString())
                            productDetailsBinding.tvInstock.text = getString(R.string.outofstock)
                            productDetailsBinding.tvInstock.setTextColor(getColor(R.color.red))
                            productDetailsBinding.btnaddtocart.background =
                                getDrawable(R.drawable.round_gray_bg_9)
                            productDetailsBinding.btnaddtocart.isClickable = false
                        } else {
                            productDetailsBinding.tvInstock.text = getString(R.string.in_stock)
                            productDetailsBinding.tvInstock.setTextColor(getColor(R.color.green))
                            productDetailsBinding.btnaddtocart.background =
                                getDrawable(R.drawable.round_blue_bg_9)
                            productDetailsBinding.btnaddtocart.isClickable = true
                        }
                    }
                }
                setupPriceData(variationList[i])
                variationList[i].isSelect = true
                variationAdaper?.notifyDataSetChanged()
            }
        }
        productDetailsBinding.rvproductSize.apply {
            layoutManager =
                LinearLayoutManager(
                    this@ActProductDetails,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            itemAnimator = DefaultItemAnimator()
            adapter = variationAdaper
        }
    }

    override fun onResume() {
        super.onResume()
        if (isCheckNetwork(this@ActProductDetails)) {
            callApiProductDetail(intent.getStringExtra("product_id")!!)
        } else {
            alertErrorOrValidationDialog(
                this@ActProductDetails,
                resources.getString(R.string.no_internet)
            )
        }
    }
}


