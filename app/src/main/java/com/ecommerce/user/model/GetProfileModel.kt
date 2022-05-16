package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetProfileResponse(

	@field:SerializedName("data")
	val data: UserData? = null,
	@field:SerializedName("contactinfo")
	val contactinfo: ContactInfo? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class UserData(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("profile_pic")
	val profilePic: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)


data class ContactInfo(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("contact")
	val contact: String? = null,

	@field:SerializedName("email")
	val email: String? = null,
	@field:SerializedName("facebook")
	val facebook: String? = null,
	@field:SerializedName("twitter")
	val twitter: String? = null,

	@field:SerializedName("instagram")
	val instagram: String? = null,

	@field:SerializedName("linkedin")
	val linkedin: String? = null
) : Serializable
