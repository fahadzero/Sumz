package com.ecommerce.user.model

import com.google.gson.annotations.SerializedName

data class HomefeedResponse(

    @field:SerializedName("featured_products")
    val featuredProducts: ArrayList<FeaturedProductsItem>? = null,

    @field:SerializedName("brands")
    val brands: ArrayList<BrandsItem>? = null,

    @field:SerializedName("new_products")
    val newProducts: ArrayList<NewProductsItem>? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("hot_products")
    val hotProducts: ArrayList<HotProductsItem>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("currency_position")
    val currencyPosition: String? = null,

    @field:SerializedName("vendors")
    val vendors: ArrayList<VendorsItem>? = null,

    @field:SerializedName("notifications")
    val notifications: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class VendorsItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class FeaturedProductsItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

    @field:SerializedName("is_variation")
    val isVariation: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("product_price")
    val productPrice: String? = null,

    @field:SerializedName("sku")
    val sku: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("is_wishlist")
    var isWishlist: Int? = null,

    @field:SerializedName("productimage")
    val productimage: Productimage? = null,

    @field:SerializedName("variation")
    val variation: Any? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null,
    var isChecked: Int = 0
)

data class Variation(

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("qty")
    val qty: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("discounted_variation_price")
    val discountedVariationPrice: String? = null,

    @field:SerializedName("variation")
    val variation: String? = null
)

data class HotProductsItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

    @field:SerializedName("is_variation")
    val isVariation: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("product_price")
    val productPrice: String? = null,

    @field:SerializedName("sku")
    val sku: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("is_wishlist")
    var isWishlist: Int? = null,

    @field:SerializedName("productimage")
    val productimage: Productimage? = null,

    @field:SerializedName("variation")
    val variation: Variation? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null
)

data class Productimage(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class NewProductsItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

    @field:SerializedName("is_variation")
    val isVariation: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("product_price")
    val productPrice: String? = null,

    @field:SerializedName("sku")
    val sku: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("is_wishlist")
    var isWishlist: Int? = null,

    @field:SerializedName("productimage")
    val productimage: Productimage? = null,

    @field:SerializedName("variation")
    val variation: Variation? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null
)

data class BrandsItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("brand_name")
    val brandName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class RattingsItem(

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("avg_ratting")
    val avgRatting: String? = null

)
