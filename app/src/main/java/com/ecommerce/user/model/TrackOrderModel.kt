package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class TrackOrderResponse(

	@field:SerializedName("ratting")
	val ratting: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("order_info")
	val orderInfo: TrackOrderInfo? = null
)

data class TrackOrderInfo(
	@field:SerializedName("shipped_at")
	val shippedAt: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("variation")
	val variation: String? = null,

	@field:SerializedName("vendor_comment")
	val vendorComment: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("qty")
	val qty: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("return_number")
	val returnNumber: String? = null,

	@field:SerializedName("confirmed_at")
	val confirmedAt: String? = null,

	@field:SerializedName("delivered_at")
	val deliveredAt: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
