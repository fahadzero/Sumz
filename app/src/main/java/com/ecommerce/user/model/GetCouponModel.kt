package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class GetCouponResponse(

	@field:SerializedName("data")
	val data: CouponData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class CouponData(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<CouponDataItem>? = null,

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

data class CouponDataItem(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("amount")
	val amount: Any? = null,

	@field:SerializedName("percentage")
	val percentage: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("coupon_name")
	val couponName: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
)
