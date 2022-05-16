package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class BannerResponse(

    @field:SerializedName("bottombanner")
    val bottombanner: ArrayList<BottombannerItem>? = null,

    @field:SerializedName("topbanner")
    val topbanner: ArrayList<TopbannerItem>? = null,

    @field:SerializedName("popupbanner")
    val popupbanner: ArrayList<PopupbannerItem>? = null,

    @field:SerializedName("leftbanner")
    val leftbanner: ArrayList<LeftbannerItem>? = null,

    @field:SerializedName("largebanner")
    val largebanner: ArrayList<LargebannerItem>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("sliders")
    val sliders: ArrayList<SlidersItem>? = null,
)

data class LeftbannerItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("cat_id")
    val catId: String? = null,
    @field:SerializedName("category_name")
    val categoryName: String? = null,
    @field:SerializedName("type")
    val type: String? = null
)

data class LargebannerItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("cat_id")
    val catId: String? = null,
    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("type")
    val type: String? = null
)

data class TopbannerItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: String? = null,
    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("cat_id")
    val catId: Int? = null,

    @field:SerializedName("type")
    val type: String? = null
)

data class BottombannerItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,
    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("product_id")
    val productId: Any? = null,

    @field:SerializedName("cat_id")
    val catId: Int? = null,

    @field:SerializedName("type")
    val type: String? = null
)

data class PopupbannerItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: Any? = null,
    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("cat_id")
    val catId: Int? = null,

    @field:SerializedName("type")
    val type: String? = null
)

data class SlidersItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("link")
    val link: String? = null
)
