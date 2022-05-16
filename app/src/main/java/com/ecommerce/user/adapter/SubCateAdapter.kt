package com.ecommerce.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.user.R
import com.ecommerce.user.model.InnersubcategoryItem
import com.ecommerce.user.model.SubcategoryItem
import kotlin.coroutines.coroutineContext

class SubCateAdapter(private val mList: ArrayList<SubcategoryItem>) :
    RecyclerView.Adapter<SubCateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_all_sub_categories, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.textView.text = ItemsViewModel.subcategoryName
        val isVisible: Boolean = ItemsViewModel.expand
        holder.recyclerView.visibility = if (isVisible) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (ItemsViewModel.innersubcategory.isNullOrEmpty()) {
                holder.recyclerView.isClickable = false
            } else {
                ItemsViewModel.expand = !ItemsViewModel.expand
                Log.d("isVisible", isVisible.toString())
                notifyItemChanged(position)
            }
        }
        val adapter: SubInnerCateAdapter? = ItemsViewModel.innersubcategory?.let {
            SubInnerCateAdapter(
                it
            )
        }
        holder.recyclerView.adapter = adapter
        holder.recyclerView.layoutManager =
            LinearLayoutManager(holder.recyclerView.context, LinearLayoutManager.VERTICAL, false)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.tvSubcateitemname)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.rvinnersubcate)

    }
}