package com.ecommerce.user.api

class RestResponse<T> {

    private var data: T? = null

    private var message: String? = null

    private var status: String? = null

    private var mobile: String? = null

    fun getData(): T? {
        return data
    }

    fun getMessage(): String? {
        return message
    }

    fun getStatus(): String? {
        return status
    }

    fun getMobile(): String? {
        return mobile
    }


    private var currency: String? = null



    private var min_order_amount: String? = null



    private var max_order_amount: String? = null



    private var max_order_qty: String? = null



    private var referral_amount: String? = null



    private var map: String? = null

    fun getMap(): String? {
        return map
    }
}