package com.ecommerce.user.base


import android.app.Application
import co.paystack.android.PaystackSdk

import com.ecommerce.user.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class BaseApplication : Application() {

    companion object {
        lateinit var app: BaseApplication

        fun getInstance(): BaseApplication {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        PaystackSdk.initialize(applicationContext)

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                ).build()
        )
    }
}