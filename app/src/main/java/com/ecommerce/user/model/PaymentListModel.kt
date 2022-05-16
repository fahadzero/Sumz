package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class PaymentListResponse(

    @field:SerializedName("walletamount")
    val walletamount: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("paymentlist")
    val paymentlist: ArrayList<PaymentlistItem>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class PaymentlistItem(

    @field:SerializedName("live_secret_key")
    val liveSecretKey: String? = null,

    @field:SerializedName("environment")
    var environment: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("live_public_key")
    var livePublicKey: String? = null,

    @field:SerializedName("test_public_key")
    var testPublicKey: String? = null,

    @field:SerializedName("encryption_key")
    val encryptionKey: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("payment_name")
    var paymentName: String? = null,

    @field:SerializedName("test_secret_key")
    val testSecretKey: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("isSelect")
    var isSelect: Boolean? = false
)
