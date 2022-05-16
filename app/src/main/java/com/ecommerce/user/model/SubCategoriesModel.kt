package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class SubCategoriesResponse(

    @field:SerializedName("data")
    val data: SubCateData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class SubcategoryItem(

    @field:SerializedName("subcat_id")
    val subcatId: Int? = null,

    @field:SerializedName("subcategory_name")
    val subcategoryName: String? = null,

    @field:SerializedName("innersubcategory")
    val innersubcategory: ArrayList<InnersubcategoryItem>? = null,

    var expand: Boolean = false
)

data class SubCateData(

    @field:SerializedName("subcategory")
    val subcategory: ArrayList<SubcategoryItem>? = null
)

data class InnersubcategoryItem(

    @field:SerializedName("innersubcategory_name")
    val innersubcategoryName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
