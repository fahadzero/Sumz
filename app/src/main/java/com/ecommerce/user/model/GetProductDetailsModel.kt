package com.ecommerce.user.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GetProductDetailsResponse(

    @field:SerializedName("data")
    val data: ProductDetailsData? = null,

    @field:SerializedName("returnpolicy")
    val returnpolicy: Returnpolicy? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("vendors")
    val vendors: Vendors? = null,

    @field:SerializedName("related_products")
    val relatedProducts: ArrayList<RelatedProductsItem>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class VariationsItem(

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
    val variation: String? = null,

    @field:SerializedName("isSelect")
    var isSelect: Boolean? = false
)

data class ProductimagesItem(

    @field:SerializedName("image_name")
    val imageName: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageName)
        parcel.writeString(imageUrl)
        parcel.writeValue(productId)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductimagesItem> {
        override fun createFromParcel(parcel: Parcel): ProductimagesItem {
            return ProductimagesItem(parcel)
        }

        override fun newArray(size: Int): Array<ProductimagesItem?> {
            return arrayOfNulls(size)
        }
    }
}

data class Productimages(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class ProductDetailsData(

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("free_shipping")
    val freeShipping: Int? = null,

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("is_variation")
    val isVariation: Int? = null,

    @field:SerializedName("subcategory_name")
    val subcategoryName: String? = null,

    @field:SerializedName("product_price")
    val productPrice: String? = null,

    @field:SerializedName("is_wishlist")
    var isWishlist: Int? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null,

    @field:SerializedName("is_return")
    val isReturn: Int? = null,

    @field:SerializedName("variations")
    val variations: ArrayList<VariationsItem>? = null,

    @field:SerializedName("innersubcategory_name")
    val innersubcategoryName: String? = null,

    @field:SerializedName("cat_id")
    val catId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("attribute")
    val attribute: String? = null,

    @field:SerializedName("sku")
    val sku: String? = null,

    @field:SerializedName("return_days")
    val returnDays: String? = null,

    @field:SerializedName("shipping_cost")
    val shippingCost: String? = null,

    @field:SerializedName("tax")
    val tax: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("est_shipping_days")
    val estShippingDays: String? = null,

    @field:SerializedName("tax_type")
    val taxType: String? = null,

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null,

    @field:SerializedName("productimages")
    val productimages: ArrayList<ProductimagesItem>? = null,

    @field:SerializedName("product_qty")
    val availableStock: String? = null
)

data class Returnpolicy(

    @field:SerializedName("return_policies")
    val returnPolicies: String? = null
)

data class Rattings(

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null,

    @field:SerializedName("avg_ratting")
    val avgRatting: Double? = null
)

data class RelatedProductsItem(

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
    val productimage: Productimages? = null,

    @field:SerializedName("variation")
    val variation: Any? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null
)

data class Vendors(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("rattings")
    val rattings: Rattings? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
