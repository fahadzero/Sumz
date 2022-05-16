package com.ecommerce.user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActReturnPolicyBinding

class ActReturnPolicy : BaseActivity() {
    private lateinit var actReturnPolicyBinding: ActReturnPolicyBinding

    override fun setLayout(): View =actReturnPolicyBinding.root

    override fun initView() {
        actReturnPolicyBinding= ActReturnPolicyBinding.inflate(layoutInflater)
        //TODO PRODUCT RETURN POLICIES TEXT SET
        val extras = intent.extras
        val description = extras!!.getString("return_policies")
        actReturnPolicyBinding.tvreturnpoliciesDescription.text=description
        actReturnPolicyBinding.ivBack.setOnClickListener { finish() }
    }
}