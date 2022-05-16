package com.ecommerce.user.activity


import android.os.Handler
import android.os.Looper
import android.view.View
import com.ecommerce.user.R
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActSplashScreenBinding
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference

class ActSplashScreen : BaseActivity() {

    private lateinit var splashScreenBinding:ActSplashScreenBinding

    override fun setLayout(): View = splashScreenBinding.root

    override fun initView() {
        splashScreenBinding = ActSplashScreenBinding.inflate(layoutInflater)
        if (Common.isCheckNetwork(this@ActSplashScreen)) {
            init()
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActSplashScreen,
                resources.getString(R.string.no_internet)
            )
        }
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!SharePreference.getBooleanPref(this@ActSplashScreen, SharePreference.isTutorial)) {
                openActivity(ActTutorial::class.java)
                finish()
            } else {
                openActivity(ActMain::class.java)
                finish()
            }
        }, 3000)
    }

}