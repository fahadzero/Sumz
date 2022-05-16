package com.ecommerce.user.activity

import android.util.Log
import android.view.View
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.api.SingleResponse
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActAddAddressBinding
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.alertErrorOrValidationDialog
import com.ecommerce.user.utils.Common.dismissLoadingProgress
import com.ecommerce.user.utils.Common.getCurrentLanguage
import com.ecommerce.user.utils.Common.showErrorFullMsg
import com.ecommerce.user.utils.Common.showLoadingProgress
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActAddAddress : BaseActivity() {
    private lateinit var addAddressBinding: ActAddAddressBinding
    private var addressId = 0
    var type = 0
    private var isClick = false
    override fun setLayout(): View = addAddressBinding.root

    override fun initView() {
        addAddressBinding = ActAddAddressBinding.inflate(layoutInflater)
        addAddressBinding.ivBack.setOnClickListener { finish() }
        addAddressBinding.btnsave.setOnClickListener {
            if (Common.isCheckNetwork(this@ActAddAddress)) {
                validation()
            } else {
                alertErrorOrValidationDialog(
                    this@ActAddAddress,
                    resources.getString(R.string.no_internet)
                )
            }
        }
        type = intent.getIntExtra("Type", 0)
        if (type == 1) {
            isClick = true
            addAddressBinding.tvAddressTitle.text = resources.getString(R.string.edit_address)
            addAddressBinding.btnsave.text = resources.getString(R.string.update_address)
            getdata()
        } else {
            addAddressBinding.tvAddressTitle.text = resources.getString(R.string.new_address)
            addAddressBinding.btnsave.text = resources.getString(R.string.save_address)
        }
    }

    //TODO SET ADDRESS TO EDIT DATA
    private fun getdata() {
        addressId = intent.getIntExtra("address_id", 0)
        addAddressBinding.edFullname.setText(intent.getStringExtra("FirstName"))
        addAddressBinding.edLastName.setText(intent.getStringExtra("LastName"))
        addAddressBinding.edStreerAddress.setText(intent.getStringExtra("StreetAddress"))
        addAddressBinding.edLandmark.setText(intent.getStringExtra("Landmark"))
        addAddressBinding.edPostCodeZip.setText(intent.getStringExtra("Pincode"))
        addAddressBinding.edPhone.setText(intent.getStringExtra("Mobile"))
        addAddressBinding.edtEmailAddress.setText(intent.getStringExtra("Email"))
    }

    //TODO ADDRESS VALIDATION
    private fun validation() = when {
        addAddressBinding.edFullname.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edLastName.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edStreerAddress.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edLandmark.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edPostCodeZip.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edPhone.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edtEmailAddress.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        else -> {
            val addadrress = HashMap<String, String>()
            addadrress["user_id"] =
                SharePreference.getStringPref(this@ActAddAddress, SharePreference.userId)
                    ?: ""
            addadrress["first_name"] = addAddressBinding.edFullname.text.toString()
            addadrress["last_name"] = addAddressBinding.edLastName.text.toString()
            addadrress["street_address"] = addAddressBinding.edStreerAddress.text.toString()
            addadrress["landmark"] = addAddressBinding.edLandmark.text.toString()
            addadrress["pincode"] = addAddressBinding.edPostCodeZip.text.toString()
            addadrress["mobile"] = addAddressBinding.edPhone.text.toString()
            addadrress["email"] = addAddressBinding.edtEmailAddress.text.toString()
            if (type == 1) {
                addadrress["address_id"] = addressId.toString()
                Log.e("request", addadrress.toString())
                callApiUpdateAddress(addadrress)
            } else {
                callApiAddAddress(addadrress)
            }
        }
    }

    //TODO API ADD ADDRESS CALL
    private fun callApiAddAddress(addadrress: HashMap<String, String>) {
        showLoadingProgress(this@ActAddAddress)
        val call = ApiClient.getClient.addAddress(addadrress)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        Common.isAddOrUpdated = true
                        finish()
                    } else {
                        showErrorFullMsg(
                            this@ActAddAddress,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    alertErrorOrValidationDialog(
                        this@ActAddAddress,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActAddAddress,
                    resources.getString(R.string.error_msg)
                )

            }
        })
    }

    //TODO API UPDATE ADDRESS CALL
    private fun callApiUpdateAddress(addressRequest: HashMap<String, String>) {
        showLoadingProgress(this@ActAddAddress)
        val call = ApiClient.getClient.updateAddress(addressRequest)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    Common.isAddOrUpdated = true

                    if (response.body()?.status == 1) {
                        setResult(RESULT_OK)
                        finish()

                    } else {
                        showErrorFullMsg(
                            this@ActAddAddress,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@ActAddAddress,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActAddAddress,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this@ActAddAddress, false)
    }
}