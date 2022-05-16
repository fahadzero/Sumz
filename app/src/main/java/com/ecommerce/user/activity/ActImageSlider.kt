package com.ecommerce.user.activity

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.ecommerce.user.R
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActImageSliderBinding
import com.ecommerce.user.model.ProductimagesItem
import com.ecommerce.user.utils.Common

class ActImageSlider : BaseActivity() {
    private lateinit var imageSliderBinding: ActImageSliderBinding
    var imgList: ArrayList<ProductimagesItem>? = null

    override fun setLayout(): View = imageSliderBinding.root

    override fun initView() {
        imageSliderBinding = ActImageSliderBinding.inflate(layoutInflater)
        imgList = intent.getStringArrayListExtra("imageList") as ArrayList<ProductimagesItem>?
        val imageList = ArrayList<SlideModel>()
        for (i in 0 until imgList?.size!!) {
            val slideModel = SlideModel(imgList!![i].imageUrl)
            imageList.add(slideModel)
        }
        imageSliderBinding.imageSlider.setImageList(imageList)
        imageSliderBinding.ivCancle.setOnClickListener {
            finish()
        }
    }

}