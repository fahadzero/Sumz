package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class CategoriesResponse(

    @field:SerializedName("data")
	val data: ArrayList<DataItem>? = null,


    @field:SerializedName("message")
	val message: String? = null,

    @field:SerializedName("status")
	val status: Int? = null
)

data class DataItem(

	@field:SerializedName("category_name")
	val categoryName: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
