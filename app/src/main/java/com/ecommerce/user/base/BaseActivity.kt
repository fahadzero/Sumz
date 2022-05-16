package com.ecommerce.user.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper


abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setContentView(setLayout())
    }

    protected abstract fun setLayout(): View
    protected abstract fun initView()

    open fun openActivity(destinationClass: Class<*>) {
        startActivity(Intent(this@BaseActivity, destinationClass))
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }
}