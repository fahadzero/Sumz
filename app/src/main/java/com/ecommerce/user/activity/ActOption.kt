package com.ecommerce.user.activity

import android.view.View
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActSplashBinding


class
ActOption : BaseActivity() {

    private lateinit var splashBinding: ActSplashBinding

    override fun setLayout(): View =splashBinding.root

    override fun initView() {
        splashBinding = ActSplashBinding.inflate(layoutInflater)
        splashBinding.btnLogin.setOnClickListener { openActivity(ActLogin::class.java) }
        splashBinding.tvSignUp.setOnClickListener { openActivity(ActSignUp::class.java) }
        splashBinding.btnSkip.setOnClickListener { openActivity(ActMain::class.java) }
    }
}