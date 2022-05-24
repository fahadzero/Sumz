package com.sumz.app.activity

import android.view.View
import com.sumz.app.base.BaseActivity
import com.sumz.app.databinding.ActPaymentSuccessFullBinding

class ActPaymentSuccessFull : BaseActivity() {
    private lateinit var actPaymentSuccessFullBinding: ActPaymentSuccessFullBinding

    override fun setLayout(): View =actPaymentSuccessFullBinding.root

    override fun initView() {
        actPaymentSuccessFullBinding= ActPaymentSuccessFullBinding.inflate(layoutInflater)
        actPaymentSuccessFullBinding.btncontinueshopping.setOnClickListener { openActivity(ActMain::class.java
        ) }
    }

}