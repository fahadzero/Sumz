package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class GetWishListResponse(

	@field:SerializedName("data")
	val allData: WishListData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class WishListProductimage(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class WishListDataItem(

	@field:SerializedName("rattings")
	val rattings: ArrayList<WishListRattingsItem>? = null,

	@field:SerializedName("is_variation")
	val isVariation: Int? = null,

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
	val productimage: WishListProductimage? = null,

	@field:SerializedName("variation")
	val variation: WishListVariation? = null,

	@field:SerializedName("discounted_price")
	val discountedPrice: String? = null
)

data class WishListRattingsItem(

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("avg_ratting")
	val avgRatting: String? = null

)

data class WishListData(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<WishListDataItem>? = null,

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

data class WishListVariation(

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("qty")
	val qty: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("discounted_variation_price")
	val discountedVariationPrice: String? = null,

	@field:SerializedName("variation")
	val variation: String? = null
)
