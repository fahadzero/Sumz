package com.sumz.app.activity

import android.view.View
import com.sumz.app.base.BaseActivity
import com.sumz.app.databinding.ActProductDescriptionBinding

class ActProductDescription : BaseActivity() {

    private lateinit var productDescriptionBinding: ActProductDescriptionBinding
    override fun setLayout(): View =productDescriptionBinding.root

    override fun initView() {
        productDescriptionBinding= ActProductDescriptionBinding.inflate(layoutInflater)
        //TODO PRODUCT DESCRIPTION TEXT SET
        val extras = intent.extras
        val description = extras!!.getString("description")
        productDescriptionBinding.tvProductDescription.text=description
        productDescriptionBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)}
    }
}