package com.sumz.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumz.app.R
import com.sumz.app.activity.ActViewAll
import com.sumz.app.model.InnersubcategoryItem

class SubInnerCateAdapter (private val mList: ArrayList<InnersubcategoryItem>) : RecyclerView.Adapter<SubInnerCateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_all_inner_sub_categories, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val innersubcategory = mList[position]
        holder.textView.text = innersubcategory.innersubcategoryName
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ActViewAll::class.java)
            intent.putExtra("innersubcategory_id", innersubcategory.id.toString())
            intent.putExtra(
                "title",
                innersubcategory.innersubcategoryName.toString()
            )
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.tvinnersubcateitemname)
    }
}