package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class SearchProductResponse(

	@field:SerializedName("data")
	val data: ArrayList<SearchDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class SearchProductimage(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class SearchDataItem(

	@field:SerializedName("rattings")
	val rattings: ArrayList<SearchRattingsItem>? = null,

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
	val isWishlist: String? = null,

	@field:SerializedName("productimage")
	val productimage: SearchProductimage? = null,

	@field:SerializedName("variation")
	val variation: SearchVariation? = null,

	@field:SerializedName("discounted_price")
	val discountedPrice: String? = null
)

data class SearchVariation(

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

data class SearchRattingsItem(

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("avg_ratting")
	val avgRatting: String? = null
)
