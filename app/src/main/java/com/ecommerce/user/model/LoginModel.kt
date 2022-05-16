package com.ecommerce.user.model


class LoginModel {

    private var mobile: String? = null

    private var id: String? = null

    private var email: String? = null

    private var name: String? = null

    private var profile_pic: String? = null

    private var referral_code: String? = null

    private var login_type: String? = null

    fun getMobile(): String? {
        return mobile
    }

    fun getLoginType():String?{
        return login_type
    }
    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getEmail(): String? {
        return email
    }

    fun getName(): String? {
        return name
    }

    fun getProfile(): String? {
        return profile_pic
    }

    fun getReferralCode(): String? {
        return referral_code
    }
}