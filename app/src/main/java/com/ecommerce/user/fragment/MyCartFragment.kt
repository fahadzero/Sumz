package com.ecommerce.user.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.activity.ActCheckout
import com.ecommerce.user.activity.ActLogin
import com.ecommerce.user.activity.ActMain
import com.ecommerce.user.activity.ActProductDetails
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.base.BaseFragment
import com.ecommerce.user.databinding.FragMyCartBinding
import com.ecommerce.user.databinding.RemoveItemDialogBinding
import com.ecommerce.user.databinding.RowMycartBinding
import com.ecommerce.user.databinding.SuccessBottomsheetDialogBinding
import com.ecommerce.user.model.CartDataItem
import com.ecommerce.user.model.GetCartResponse
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.getCurrentLanguage
import com.ecommerce.user.utils.Common.isCheckNetwork
import com.ecommerce.user.utils.Common.showErrorFullMsg
import com.ecommerce.user.utils.Common.showLoadingProgress
import com.ecommerce.user.utils.SharePreference
import com.ecommerce.user.utils.SharePreference.Companion.getStringPref
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MyCartFragment : BaseFragment<FragMyCartBinding>() {
    private lateinit var fragMyCartBinding: FragMyCartBinding
    private var cartDataList: ArrayList<CartDataItem>? = null
    var currency: String = ""
    var currencyPosition: String = ""
    var count=0
    private var cartListDataAdapter: BaseAdaptor<CartDataItem, RowMycartBinding>? =
        null
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun initView(view: View) {
        fragMyCartBinding = FragMyCartBinding.bind(view)
        currency = getStringPref(requireActivity(), SharePreference.Currency)!!
        currencyPosition = getStringPref(requireActivity(), SharePreference.CurrencyPosition)!!

        if (isCheckNetwork(requireActivity())) {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                callApiCartData(false)
            } else {
                openActivity(ActLogin::class.java)
                requireActivity().finish()
            }
        } else {
            alertErrorOrValidationDialog(
                requireActivity(),
                resources.getString(R.string.no_internet)
            )
        }
    }

    override fun getBinding(): FragMyCartBinding {
        fragMyCartBinding = FragMyCartBinding.inflate(layoutInflater)
        fragMyCartBinding.btncheckout.setOnClickListener { openActivity(ActCheckout::class.java) }
        return fragMyCartBinding
    }


    //TODO API CART CALL
    private fun callApiCartData(isQty: Boolean) {
        if (!isQty) {
            showLoadingProgress(requireActivity())
        }
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            getStringPref(requireActivity(), SharePreference.userId)!!
        val call = ApiClient.getClient.getCartData(hasmap)
        call.enqueue(object : Callback<GetCartResponse> {
            override fun onResponse(
                call: Call<GetCartResponse>,
                response: Response<GetCartResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        fragMyCartBinding.rvMycard.visibility = View.VISIBLE
                        fragMyCartBinding.tvNoDataFound.visibility = View.GONE
                        cartDataList = restResponce.data
                        if (isAdded) {
                            loadCartData(
                                cartDataList!!
                            )
                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        fragMyCartBinding.rvMycard.visibility = View.GONE
                        fragMyCartBinding.tvNoDataFound.visibility = View.VISIBLE
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetCartResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    fun removeItemDialog(strCartId: String, pos: Int) {
        val removeDialogBinding = RemoveItemDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(removeDialogBinding.root)
        removeDialogBinding.tvRemoveTitle.text = resources.getString(R.string.remove_product)
        removeDialogBinding.tvAlertMessage.text = resources.getString(R.string.remove_product_desc)
        removeDialogBinding.btnProceed.setOnClickListener {
            if (isCheckNetwork(requireActivity())) {
                dialog.dismiss()
                val hashMap = HashMap<String, String>()
                hashMap["user_id"] =
                    getStringPref(
                        requireActivity(),
                        SharePreference.userId
                    ).toString()
                hashMap["cart_id"] = strCartId
                callDeleteApi(hashMap, pos)
            } else {
                alertErrorOrValidationDialog(
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


    //TODO SET CART DATA
    private fun loadCartData(cartDataList: ArrayList<CartDataItem>) {
        lateinit var binding: RowMycartBinding
        cartListDataAdapter =
            object : BaseAdaptor<CartDataItem, RowMycartBinding>(
                requireActivity(),
                cartDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CartDataItem,
                    position: Int
                ) {
                    val price =
                        cartDataList[position].qty?.toInt()!! * cartDataList[position].price!!.toDouble()
                    if (currencyPosition == "left") {
                        binding.tvcartitemprice.text = currency.plus(
                            String.format(Locale.US, "%,.2f", price)
                        )
                    } else {
                        binding.tvcartitemprice.text =
                            String.format(Locale.US, "%,.2f", price) + currency
                    }
                    binding.tvorderitem.text = cartDataList[position].qty.toString()
                    binding.tvcateitemname.text = cartDataList[position].productName
                    if (cartDataList[position].variation?.isEmpty() == true) {
                        binding.tvcartitemsize.text = "-"
                    } else {
                        if (cartDataList[position].attribute == null || cartDataList[position].variation == null) {
                            binding.tvcartitemsize.text = "-"
                        } else {
                            binding.tvcartitemsize.text =
                                cartDataList[position].attribute + " : " + cartDataList[position].variation
                        }
                    }
                    Glide.with(requireActivity())
                        .load(cartDataList[position].imageUrl).into(binding.ivCartitemm)
                    binding.ivCartitemm.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    binding.tvDelete.setOnClickListener {
                        if (isCheckNetwork(requireActivity())) {
                            removeItemDialog(cartDataList[position].id.toString(),position)
                        } else {
                            alertErrorOrValidationDialog(
                                requireActivity(),
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                    binding.ivMinus.setOnClickListener {
                        if (cartDataList[position].qty!!.toInt() > 1) {
                            binding.ivMinus.isClickable = true
                            Common.getLog("Qty>>", cartDataList[position].qty.toString())
                            if (isCheckNetwork(requireActivity())) {
                                callQtyUpdate(cartDataList[position], false)
                            } else {
                                alertErrorOrValidationDialog(
                                    requireActivity(),
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        } else {
                            binding.ivMinus.isClickable = false
                            Common.getLog("Qty1>>", cartDataList[position].qty.toString())
                        }
                    }
                    binding.swipe.surfaceView.setOnClickListener {
                        Log.e("product_id--->", cartDataList[position].productId.toString())
                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
                        intent.putExtra("product_id", cartDataList[position].productId.toString())
                        startActivity(intent)
                    }
                    binding.ivPlus.setOnClickListener {
                        if (cartDataList[position].qty!!.toInt() < 10
                        ) {
                            count++
                            if (isCheckNetwork(requireActivity())) {
                                callQtyUpdate(cartDataList[position], true)
                            } else {
                                alertErrorOrValidationDialog(
                                    requireActivity(),
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        } else {
                            alertErrorOrValidationDialog(
                                requireActivity(),
                                resources.getString(R.string.max_qty)
                            )
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_mycart
                }

                override fun getBinding(parent: ViewGroup): RowMycartBinding {
                    binding = RowMycartBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            fragMyCartBinding.rvMycard.apply {
                if (cartDataList.size > 0) {
                    fragMyCartBinding.rvMycard.visibility = View.VISIBLE
                    fragMyCartBinding.tvNoDataFound.visibility = View.GONE
                    fragMyCartBinding.btncheckout.visibility = View.VISIBLE

                    layoutManager =
                        LinearLayoutManager(
                            activity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    itemAnimator = DefaultItemAnimator()
                    adapter = cartListDataAdapter
                } else {
                    fragMyCartBinding.rvMycard.visibility = View.GONE
                    fragMyCartBinding.tvNoDataFound.visibility = View.VISIBLE
                    fragMyCartBinding.btncheckout.visibility = View.GONE
                }
            }
        }
    }

    //TODO CART ITEM DELETE CINFOFMATION DIALOG
    @SuppressLint("InflateParams")
    fun dlgDeleteConformationDialog(act: Activity, msg: String?, strCartId: String, pos: Int) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvDesc)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvYes)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                if (isCheckNetwork(requireActivity())) {
                    finalDialog.dismiss()
                    val hashMap = HashMap<String, String>()
                    hashMap["user_id"] =
                        getStringPref(
                            requireActivity(),
                            SharePreference.userId
                        ).toString()
                    hashMap["cart_id"] = strCartId
                    callDeleteApi(hashMap, pos)
                } else {
                    alertErrorOrValidationDialog(
                        requireActivity(),
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            val tvCancle: TextView = mView.findViewById(R.id.tvNo)
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    //TODO API CART ITEM DELETE CALL
    private fun callDeleteApi(hasmap: HashMap<String, String>, pos: Int) {
        showLoadingProgress(requireActivity())

        val call = ApiClient.getClient.deleteProduct(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        cartDataList?.removeAt(pos)
                        callApiCartData(true)
                        if (cartDataList?.size ?: 0 > 0) {
                            fragMyCartBinding.tvNoDataFound.visibility = View.GONE
                            fragMyCartBinding.rvMycard.visibility = View.VISIBLE
                            fragMyCartBinding.btncheckout.visibility = View.VISIBLE
                        } else {
                            fragMyCartBinding.tvNoDataFound.visibility = View.VISIBLE
                            fragMyCartBinding.rvMycard.visibility = View.GONE
                            fragMyCartBinding.btncheckout.visibility = View.GONE
                        }
                    } else {
                        showErrorFullMsg(
                            requireActivity(),
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    alertErrorOrValidationDialog(
                        requireActivity(),
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO API QTY UPDATE CALL
    private fun callQtyUpdate(cartModel: CartDataItem, isPlus: Boolean) {
        val qty = if (isPlus) {
            cartModel.qty!!.toInt() + 1
        } else {
            cartModel.qty!!.toInt() - 1
        }
        showLoadingProgress(requireActivity())
        val hashMap = HashMap<String, String>()
        hashMap["cart_id"] = cartModel.id.toString()
        hashMap["qty"] = qty.toString()
        val call = ApiClient.getClient.qtyUpdate(hashMap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        Common.isAddOrUpdated = true
                        callApiCartData(true)
                    } else {
                        response.body()?.message?.let {
                            showErrorFullMsg(
                                requireActivity(),
                                it
                            )
                        }
                    }
                } else {
                    alertErrorOrValidationDialog(
                        requireActivity(),
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(requireActivity(), false)
        if (isCheckNetwork(requireActivity())) {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                callApiCartData(false)
            } else {
                openActivity(ActLogin::class.java)
                requireActivity().finish()
            }
        } else {
            alertErrorOrValidationDialog(
                requireActivity(),
                resources.getString(R.string.no_internet)
            )
        }
    }
}