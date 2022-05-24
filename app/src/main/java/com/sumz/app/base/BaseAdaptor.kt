package com.sumz.app.base


import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

import java.util.*

abstract class BaseAdaptor<T, binding : ViewBinding>(
    val context: Activity,
    private var items: ArrayList<T>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: binding? = null
    abstract fun onBindData(holder: RecyclerView.ViewHolder?, `val`: T, position: Int)
    abstract fun setItemLayout(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = getBinding(parent)

        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(holder, items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    abstract fun getBinding(parent: ViewGroup): binding

    internal inner class ViewHolder(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}