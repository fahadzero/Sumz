package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class BrandResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("vendors")
	val vendors: BrandVendors? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class BrandDataItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("brand_name")
	val brandName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class BrandVendors(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<BrandDataItem>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: Any? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)
