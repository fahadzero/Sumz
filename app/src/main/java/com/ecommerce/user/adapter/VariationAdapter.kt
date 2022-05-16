package com.ecommerce.user.adapter

import android.app.Activity
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.user.R
import com.ecommerce.user.databinding.ActProductDetailsBinding
import com.ecommerce.user.databinding.RowProductsizeBinding
import com.ecommerce.user.model.ProductDetailsData
import com.ecommerce.user.model.VariationsItem
import com.google.android.material.color.MaterialColors.getColor
import java.util.*

class VariationAdapter(private val context: Activity,
                       private val productList:ArrayList<VariationsItem>,
                       private val taxPercent:String,
                       private val tax:String,
                       private val productDetailsBinding: ActProductDetailsBinding,
                       private val itemClick: (Int, String) -> Unit
):RecyclerView.Adapter<VariationAdapter.RowProductViewHolder>() {


    inner class RowProductViewHolder(private val binding: RowProductsizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: VariationsItem,
            context: Activity,
            productDetailsBinding: ActProductDetailsBinding,
            position: Int,
            itemClick: (Int, String) -> Unit
        ) = with(binding)
        {
            if (data.variation == null) {
                productDetailsBinding.tvproductdesc.visibility = View.GONE
                productDetailsBinding.rvproductSize.visibility = View.GONE
            } else {
                productDetailsBinding.rvproductSize.visibility = View.VISIBLE
                productDetailsBinding.tvproductdesc.visibility = View.VISIBLE
                binding.tvproductsizeS.text = data.variation
            }
            binding.tvproductsizeS.background = ResourcesCompat.getDrawable(context.resources, R.drawable.size_gray_border, null)
            if (data.isSelect==true) {
                binding.tvproductsizeS.background = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.size_blue_border,
                    null
                )
            }
                Log.d("variations-->", data.variation!!)
            itemView.setOnClickListener {
                itemClick(position,"ItemClick")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowProductViewHolder {
        val view = RowProductsizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowProductViewHolder(view)    }

    override fun onBindViewHolder(holder: RowProductViewHolder, position: Int) {
        holder.bind(
            productList[position],
            context,
            productDetailsBinding,
            position,
            itemClick
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}