package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class OrderRetuenRequestResponse(

    @field:SerializedName("data")
    val data: ArrayList<OrderRetuenRequestDataItem>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("order_info")
    val orderInfo: OrderRetuenRequestOrderInfo? = null
)

data class OrderRetuenRequestOrderInfo(

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: String? = null,

    @field:SerializedName("vendor_id")
    val vendorId: String? = null,

    @field:SerializedName("qty")
    val qty: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("variation")
    val variation: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class OrderRetuenRequestDataItem(

    @field:SerializedName("return_conditions")
    val returnConditions: String? = null,
    @field:SerializedName("isSelect")
    var isSelect: Boolean? = false
)
