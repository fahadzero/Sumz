package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class ProductReviewResponse(

	@field:SerializedName("reviews")
	val reviews: Reviews? = null,

	@field:SerializedName("all_review")
	val allReview: AllReview? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ReviewDataItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("ratting")
	val 	ratting: String? = null,

	@field:SerializedName("comment")
	val comment: String? = null,

	@field:SerializedName("users")
	val users: Users? = null
)

data class Reviews(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("five_ratting")
	val fiveRatting: Int? = null,

	@field:SerializedName("three_ratting")
	val threeRatting: Int? = null,

	@field:SerializedName("two_ratting")
	val twoRatting: Int? = null,

	@field:SerializedName("one_ratting")
	val oneRatting: Int? = null,

	@field:SerializedName("avg_ratting")
	val avgRatting: String? = null,

	@field:SerializedName("four_ratting")
	val fourRatting: Int? = null
)

data class Users(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class AllReview(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<ReviewDataItem>? = null,

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
