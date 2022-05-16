package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName
import com.iamkamrul.expandablerecyclerviewlist.model.ParentListItem

data class InerrSubCategoriesResponse(

	@field:SerializedName("data")
	val data: ArrayList<InnerSubCateDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)/*: ParentListItem {
	override fun getChildItemList(): List<*> = data!!
	override fun isInitiallyExpanded(): Boolean = false
}*/
data class InnerSubCateDataItem(

	@field:SerializedName("innersubcategory_name")
	val innersubcategoryName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
