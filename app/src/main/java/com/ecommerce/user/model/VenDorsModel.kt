package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class VendorsResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("vendors")
	val vendors: VendorsData? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class VenDorsRattings(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("avg_ratting")
	val avgRatting: String? = null
)

data class VendorsData(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<VenDorsDataItem>? = null,

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

data class VenDorsDataItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("rattings")
	val rattings: VenDorsRattings? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
