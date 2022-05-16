package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class GetCartResponse(

	@field:SerializedName("data")
	val data: ArrayList<CartDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class CartDataItem(

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("attribute")
	val attribute: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("variation")
	val variation: String? = null
)
