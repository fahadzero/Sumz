package com.ecommerce.user.activity

import android.view.View
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActWelComeBinding


class ActWelCome : BaseActivity() {
    private lateinit var actWelComeBinding: ActWelComeBinding
    override fun setLayout(): View =actWelComeBinding.root

    override fun initView() {
        actWelComeBinding= ActWelComeBinding.inflate(layoutInflater)
    }
}