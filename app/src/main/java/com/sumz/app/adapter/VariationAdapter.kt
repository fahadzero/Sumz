package com.sumz.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumz.app.R
import com.sumz.app.databinding.ActProductDetailsBinding
import com.sumz.app.databinding.RowProductsizeBinding
import com.sumz.app.model.VariationsItem
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