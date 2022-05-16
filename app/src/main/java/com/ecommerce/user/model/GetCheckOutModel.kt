package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class GetCheckOutResponse(

    @field:SerializedName("cartdata")
    val checkoutdata: ArrayList<CheckOutDataItem>? = null,

    @field:SerializedName("data")
    val data: CheckOutData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("coupon_name")
    val couponName: Any? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class CheckOutData(

    @field:SerializedName("shipping_cost")
    val shippingCost: String? = null,

    @field:SerializedName("subtotal")
    val subtotal: Int? = null,

    @field:SerializedName("tax")
    val tax: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class CheckOutDataItem(

    @field:SerializedName("shipping_cost")
    val shippingCost: String? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("discount_amount")
    val discountAmount: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("qty")
    val qty: Int? = null,

    @field:SerializedName("tax")
    val tax: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("attribute")
    val attribute: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("variation")
    val variation: String? = null,

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null
)
