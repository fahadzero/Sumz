package com.ecommerce.user.api

import com.ecommerce.user.model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("login")
    fun getLogin(@Body map: HashMap<String, String>): Call<RestResponse<LoginModel>>
    @POST("register")
    fun setRegistration(@Body map: HashMap<String, String>): Call<RestResponse<RegistrationModel>>

    @POST("vendorsregister")
    fun setVendorsRegister(@Body map: HashMap<String, String>): Call<SingleResponse>


    @POST("emailverify")
    fun setEmailVerify(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("resendemailverification")
    fun setResendEmailVerification(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("forgotPassword")
    fun setforgotPassword(@Body map: HashMap<String, String>): Call<SingleResponse>



    @POST("changepassword")
    fun setChangePassword(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("homefeeds")
    fun gethomefeeds(@Body map: HashMap<String, String>): Call<HomefeedResponse>

    @GET("banner")
    fun getbanner(): Call<BannerResponse>

    @GET("category")
    fun getcategory(): Call<CategoriesResponse>

    @POST("subcategory")
    fun getSubCategoriesDetail(@Body map: HashMap<String, String>): Call<SubCategoriesResponse>

    @POST("addtowishlist")
    fun setAddToWishList(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("removefromwishlist")
    fun setRemoveFromWishList(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("viewalllisting")
    fun setViewAllListing(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<ViewAllListResponse>

    @POST("getwishlist")
    fun getWishList(@Body map: HashMap<String, String>): Call<GetWishListResponse>

    @POST("getprofile")
    fun getProfile(@Body map: HashMap<String, String>): Call<GetProfileResponse>

    @Multipart
    @POST("editprofile")
    fun setProfile(
        @Part("user_id") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part profileimage: MultipartBody.Part?
    ): Call<SingleResponse>

    @POST("saveaddress")
    fun addAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getaddress")
    fun getAddress(@Body map: HashMap<String, String>): Call<GetAddressResponse>

    @POST("deleteaddress")
    fun deleteAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("editaddress")
    fun updateAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("productdetails")
    fun getProductDetails(@Body map: HashMap<String, String>): Call<GetProductDetailsResponse>

    @POST("vendorproducts")
    fun getVendorProducts(@Body map: HashMap<String, String>): Call<GetVendorDetailsResponse>

    @POST("productreview")
    fun getProductReview(@Body map: HashMap<String, String>): Call<ProductReviewResponse>

    @GET("brands")
    fun getBrands(@Query("page") page: String): Call<BrandResponse>

    @POST("brandsproducts")
    fun getBrandDetails(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<BrandDetailsResponse>

    @GET("vendors")
    fun getVendors(@Query("page") page: String): Call<VendorsResponse>

    @POST("vendorproducts")
    fun getVendorsDetails(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<VendorsDetailsResponse>

    @POST("notification")
    fun getNotificatios(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<NotificationsResponse>

    @POST("orderdetails")
    fun getOrderDetails(@Body map: HashMap<String, String>): Call<OrderDetailsResponse>

    @POST("trackorder")
    fun getTrackOrder(@Body map: HashMap<String, String>): Call<TrackOrderResponse>

    @GET("cmspages")
    fun getCmsData():Call<CmsPageResponse>
    @POST("orderhistory")
    fun getOrderHistory(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<OrderHistoryResponse>

    @POST("cancelorder")
    fun getCancelOrder(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("addtocart")
    fun getAddtocart(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getcart")
    fun getCartData(@Body map: HashMap<String, String>): Call<GetCartResponse>

    @POST("deleteproduct")
    fun deleteProduct(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("qtyupdate")
    fun qtyUpdate(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("checkout")
    fun getCheckOut(@Body map: HashMap<String, String>): Call<GetCheckOutResponse>

    @POST("paymentlist")
    fun getPaymentList(@Body map: HashMap<String, String>): Call<PaymentListResponse>

    @POST("order")
    fun setOrderPayment(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("searchproducts")
    fun getSearchProducts(@Body map: HashMap<String, String>): Call<SearchProductResponse>

    @POST("filter")
    fun getFilter(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<GetFilterResponse>

    @POST("products")
    fun getProduct(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<ProductResponse>

    @GET("coupons")
    fun getCoupon(
        @Query("page") page: String
    ): Call<GetCouponResponse>

    @POST("wallet")
    fun getWallet(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<WalletResponse>

    @POST("returnconditions")
    fun getOrderReturnRequest(
        @Body map: HashMap<String, String>
    ): Call<OrderRetuenRequestResponse>

    @POST("returnrequest")
    fun returnRequest(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("addratting")
    fun addRatting(@Body map: HashMap<String, String>): Call<SingleResponse>


    @POST("help")
    fun help(@Body map: HashMap<String, String>): Call<SingleResponse>

  @POST("recharge")
    fun addMoney(@Body map: HashMap<String, String>): Call<SingleResponse>
}
