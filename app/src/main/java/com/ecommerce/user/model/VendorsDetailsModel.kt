package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class VendorsDetailsResponse(

    @field:SerializedName("vendordetails")
    val vendordetails: VendordetailsData? = null,
    @field:SerializedName("data")
    val data: VendorsDetailsData? = null,
    @field:SerializedName("banners")
    var bannerList: ArrayList<TopbannerItem>,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class VariationData(

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("product_id")
    val productId: String? = null,

    @field:SerializedName("qty")
    val qty: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("discounted_variation_price")
    val discountedVariationPrice: String? = null,

    @field:SerializedName("variation")
    val variation: String? = null
)

data class VendordetailsData(

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("store_address")
    val storeAddress: String? = null,

    @field:SerializedName("rattings")
    val rattings: VendorsDetailsRattingsItem? = null,

    @field:SerializedName("email")
    val email: String? = null
)

data class VendorsDetailsRattingsItem(

    @field:SerializedName("vendor_id")
    val vendorId: String? = null,

    @field:SerializedName("avg_ratting")
    val avgRatting: String? = null
)

data class VendorsDetailsDataItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<VendorsDetailsRattingsItem>? = null,

    @field:SerializedName("is_variation")
    val isVariation: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("product_price")
    val productPrice: String? = null,

    @field:SerializedName("sku")
    val sku: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("is_wishlist")
    var isWishlist: Int? = null,

    @field:SerializedName("productimage")
    val productimage: VendorsDetailsProductimage? = null,

    @field:SerializedName("variation")
    val variation: VariationData? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null
)

data class VendorsDetailsProductimage(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class VendorsDetailsData(

    @field:SerializedName("first_page_url")
    val firstPageUrl: String? = null,

    @field:SerializedName("path")
    val path: String? = null,

    @field:SerializedName("per_page")
    val perPage: Int? = null,

    @field:SerializedName("total")
    val total: Int? = null,

    @field:SerializedName("data")
    val data: ArrayList<VendorsDetailsDataItem>? = null,

    @field:SerializedName("last_page")
    val lastPage: Int? = null,

    @field:SerializedName("last_page_url")
    val lastPageUrl: String? = null,

    @field:SerializedName("next_page_url")
    val nextPageUrl: Any? = null,

    @field:SerializedName("from")
    val from: Int? = null,

    @field:SerializedName("to")
    val to: Int? = null,

    @field:SerializedName("prev_page_url")
    val prevPageUrl: Any? = null,

    @field:SerializedName("current_page")
    val currentPage: Int? = null
)
