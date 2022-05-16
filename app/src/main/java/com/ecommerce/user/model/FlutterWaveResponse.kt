package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class FlutterWaveResponse(

	@field:SerializedName("data")
	val data: FlutterWaveData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)



data class FlutterWaveData(

	@field:SerializedName("charge_type")
	val chargeType: String? = null,

	@field:SerializedName("redirectUrl")
	val redirectUrl: String? = null,

	@field:SerializedName("AccountId")
	val accountId: String? = null,

	@field:SerializedName("merchantfee")
	val merchantfee: String? = null,

	@field:SerializedName("acctvalrespcode")
	val acctvalrespcode: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("charged_amount")
	val chargedAmount: String? = null,

	@field:SerializedName("customer.createdAt")
	val customerCreatedAt: String? = null,

	@field:SerializedName("paymentId")
	val paymentId: String? = null,

	@field:SerializedName("paymentPage")
	val paymentPage: Any? = null,

	@field:SerializedName("customer.phone")
	val customerPhone: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("appfee")
	val appfee: Double? = null,

	@field:SerializedName("customer.email")
	val customerEmail: String? = null,

	@field:SerializedName("merchantbearsfee")
	val merchantbearsfee: String? = null,

	@field:SerializedName("customer.fullName")
	val customerFullName: String? = null,

	@field:SerializedName("IP")
	val iP: String? = null,

	@field:SerializedName("customer.deletedAt")
	val customerDeletedAt: Any? = null,

	@field:SerializedName("raveRef")
	val raveRef: String? = null,

	@field:SerializedName("chargeResponseMessage")
	val chargeResponseMessage: String? = null,

	@field:SerializedName("authurl")
	val authurl: String? = null,

	@field:SerializedName("deletedAt")
	val deletedAt: Any? = null,

	@field:SerializedName("meta")
	val meta: List<Any?>? = null,

	@field:SerializedName("getpaidBatchId")
	val getpaidBatchId: Any? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("fraud_status")
	val fraudStatus: String? = null,

	@field:SerializedName("chargeResponseCode")
	val chargeResponseCode: String? = null,

	@field:SerializedName("vbvrespcode")
	val vbvrespcode: String? = null,

	@field:SerializedName("settlement_token")
	val settlementToken: Any? = null,

	@field:SerializedName("cycle")
	val cycle: String? = null,

	@field:SerializedName("paymentType")
	val paymentType: String? = null,

	@field:SerializedName("customer.customertoken")
	val customerCustomertoken: Any? = null,

	@field:SerializedName("flwRef")
	val flwRef: String? = null,

	@field:SerializedName("customerId")
	val customerId: Int? = null,

	@field:SerializedName("authModelUsed")
	val authModelUsed: String? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("retry_attempt")
	val retryAttempt: Any? = null,

	@field:SerializedName("customer.updatedAt")
	val customerUpdatedAt: String? = null,

	@field:SerializedName("device_fingerprint")
	val deviceFingerprint: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("paymentPlan")
	val paymentPlan: Any? = null,

	@field:SerializedName("customer.AccountId")
	val customerAccountId: Int? = null,

	@field:SerializedName("modalauditid")
	val modalauditid: String? = null,

	@field:SerializedName("vbvrespmessage")
	val vbvrespmessage: String? = null,

	@field:SerializedName("orderRef")
	val orderRef: String? = null,

	@field:SerializedName("customer.id")
	val customerIId: Int? = null,

	@field:SerializedName("txRef")
	val txRef: String? = null,

	@field:SerializedName("narration")
	val narration: String? = null,

	@field:SerializedName("is_live")
	val isLive: Int? = null,

	@field:SerializedName("acctvalrespmsg")
	val acctvalrespmsg: Any? = null
)
