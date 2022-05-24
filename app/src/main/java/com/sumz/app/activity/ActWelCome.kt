package com.sumz.app.activity

import android.view.View
import com.sumz.app.base.BaseActivity
import com.sumz.app.databinding.ActWelComeBinding


class ActWelCome : BaseActivity() {
    private lateinit var actWelComeBinding: ActWelComeBinding
    override fun setLayout(): View =actWelComeBinding.root

    override fun initView() {
        actWelComeBinding= ActWelComeBinding.inflate(layoutInflater)
    }
}