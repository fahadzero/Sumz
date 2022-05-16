package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class WalletResponse(

	@field:SerializedName("data")
	val data: WalletData? = null,

	@field:SerializedName("walletamount")
	val walletamount: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class WalletData(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<WalletDataItem>? = null,

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

data class WalletDataItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("wallet")
	val wallet: String? = null,

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("transaction_type")
	val transactionType: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("username")
	val username: Any? = null
)
