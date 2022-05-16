package com.ecommerce.user.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.activity.ActLogin
import com.ecommerce.user.activity.ActProductDetails
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.base.BaseFragment
import com.ecommerce.user.databinding.FragFavoriteBinding
import com.ecommerce.user.databinding.RemoveItemDialogBinding
import com.ecommerce.user.databinding.RowViewallBinding
import com.ecommerce.user.model.GetWishListResponse
import com.ecommerce.user.model.WishListDataItem
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.getCurrentLanguage
import com.ecommerce.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class FavoriteFragment : BaseFragment<FragFavoriteBinding>() {
    private lateinit var fragFavBinding: FragFavoriteBinding

    private var wishListDataList: ArrayList<WishListDataItem>? = null
    var currency: String = ""
    var currencyPosition: String = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun initView(view: View) {
        fragFavBinding = FragFavoriteBinding.bind(view)
        currency = SharePreference.getStringPref(requireActivity(), SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(requireActivity(), SharePreference.CurrencyPosition)!!

        if (Common.isCheckNetwork(requireActivity())) {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                callApiWishList()
            } else {
                openActivity(ActLogin::class.java)
                requireActivity().finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                requireActivity(),
                resources.getString(R.string.no_internet)
            )
        }
    }

    override fun getBinding(): FragFavoriteBinding {
        fragFavBinding = FragFavoriteBinding.inflate(layoutInflater)
        return fragFavBinding
    }

    //TODO API WISHLIST CALL
    private fun callApiWishList() {
        Common.showLoadingProgress(requireActivity())
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!

        val call = ApiClient.getClient.getWishList(hasmap)
        call.enqueue(object : Callback<GetWishListResponse> {
            override fun onResponse(
                call: Call<GetWishListResponse>,
                response: Response<GetWishListResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        fragFavBinding.rvwhishliast.visibility = View.VISIBLE
                        fragFavBinding.tvNoDataFound.visibility = View.GONE
                        wishListDataList = restResponce.allData?.data
                        if (isAdded) {
                            loadFeaturedProducts(
                                wishListDataList!!,
                                currency, currencyPosition
                            )
                        }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        fragFavBinding.rvwhishliast.visibility = View.GONE
                        fragFavBinding.tvNoDataFound.visibility = View.VISIBLE
                        Common.alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetWishListResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO featured Products Adapter
    private fun loadFeaturedProducts(
        wishListDataList: ArrayList<WishListDataItem>,
        currency: String?,
        currencyPosition: String?
    ) {
        lateinit var binding: RowViewallBinding
        val wishListDataAdapter =
            object : BaseAdaptor<WishListDataItem, RowViewallBinding>(
                requireActivity(),
                wishListDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: WishListDataItem,
                    position: Int
                ) {

                    if (wishListDataList[position].isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_dislike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                requireActivity(),
                                SharePreference.isLogin
                            )
                        ) {
                            if (wishListDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    wishListDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(requireActivity())) {
                                    callApiFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                            if (wishListDataList[position].isWishlist == 1) {
                                removeFavouriteDialog(
                                    wishListDataList[position].id.toString(),
                                    position
                                )
                            }
                        } else {
                            openActivity(ActLogin::class.java)
                            activity?.finish()
                        }
                    }

                    if (wishListDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            wishListDataList[position].rattings?.get(0)?.avgRatting?.toDouble()
                                .toString()
                    }

                    binding.tvProductName.text = wishListDataList[position].productName
                    if (currencyPosition == "left") {
                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    wishListDataList[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    wishListDataList[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                wishListDataList[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                wishListDataList[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    Glide.with(requireActivity())
                        .load(wishListDataList[position].productimage?.imageUrl)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        Log.e(
                            "product_id--->",
                            wishListDataList[position].productimage?.productId.toString()
                        )
                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            wishListDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_viewall
                }

                override fun getBinding(parent: ViewGroup): RowViewallBinding {
                    binding = RowViewallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            fragFavBinding.rvwhishliast.apply {
                if (wishListDataList.size > 0) {
                    fragFavBinding.rvwhishliast.visibility = View.VISIBLE
                    fragFavBinding.tvNoDataFound.visibility = View.GONE

                    layoutManager =
                        GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = wishListDataAdapter
                } else {
                    fragFavBinding.rvwhishliast.visibility = View.GONE
                    fragFavBinding.tvNoDataFound.visibility = View.VISIBLE
                }
            }
        }
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(requireActivity())
        val call = ApiClient.getClient.setRemoveFromWishList(map)
        Log.e("remove-->", Gson().toJson(map))
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        wishListDataList!![position].isWishlist = 0
                        callApiWishList()
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        requireActivity(),
                        response.body()?.message
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun removeFavouriteDialog(productId: String, pos: Int) {
        val removeDialogBinding = RemoveItemDialogBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(removeDialogBinding.root)

        removeDialogBinding.tvRemoveTitle.text = resources.getString(R.string.remove_favourite)
        removeDialogBinding.tvAlertMessage.text =
            resources.getString(R.string.remove_favourite_desc)

        removeDialogBinding.btnProceed.setOnClickListener {
            if (Common.isCheckNetwork(requireActivity())) {
                dialog.dismiss()

                val map = HashMap<String, String>()
                map["product_id"] = productId
                map["user_id"] = SharePreference.getStringPref(
                    requireActivity(),
                    SharePreference.userId
                ) ?: ""
                callApiRemoveFavourite(map, pos)

            } else {
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.no_internet)
                )
            }
        }
        removeDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //TODO CALL API FAVOURITE
    private fun callApiFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(requireActivity())
        val call = ApiClient.getClient.setAddToWishList(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        wishListDataList!![position].isWishlist = 1
                        callApiWishList()
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        callApiWishList()
        getCurrentLanguage(requireActivity(), false)
    }
}