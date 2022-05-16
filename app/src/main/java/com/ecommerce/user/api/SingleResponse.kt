package com.ecommerce.user.api

import com.google.gson.annotations.SerializedName

data class SingleResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
