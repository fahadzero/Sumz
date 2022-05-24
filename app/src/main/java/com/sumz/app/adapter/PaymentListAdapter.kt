package com.sumz.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumz.app.R
import com.sumz.app.databinding.RowPaymentBinding
import com.sumz.app.model.PaymentlistItem

class PaymentListAdapter(
    private val context: Activity,
    private val paymentOptionList: ArrayList<PaymentlistItem>,
    private val itemClick: (Int, String) -> Unit,
) : RecyclerView.Adapter<PaymentListAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(private val itemBinding: RowPaymentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            data: PaymentlistItem,
            context: Activity,
            position: Int,
            itemClick: (Int, String) -> Unit
        ) = with(itemBinding)
        {

            if (data.isSelect == true) {
                itemBinding.ivCheck.visibility = View.VISIBLE
            } else {
                itemBinding.ivCheck.visibility = View.GONE
            }

            when (data.paymentName) {
              "Wallet" -> {
                    itemBinding.ivPayment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_wallet,
                            null
                        )
                    )

                }
                "COD" -> {
                    itemBinding.ivPayment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_cod,
                            null
                        )
                    )

                }
                "RazorPay" -> {

                    itemBinding.ivPayment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_rezorpaypayment,
                            null
                        )
                    )
                }
                "Stripe" -> {

                    itemBinding.ivPayment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_stripepayment,
                            null
                        )
                    )
                }
                "Paystack"->{
                    itemBinding.ivPayment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_paystackpayment,
                            null
                        )
                    )
                }
                "Flutterwave" -> {
                    itemBinding.ivPayment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_flutterwavepayment,
                            null
                        )
                    )

                }
            }
            itemBinding.tvpaymenttype.text = data.paymentName.plus(" ").plus(context.resources.getString(R.string.payment))
            itemView.setOnClickListener {
                itemClick(position, Constants.ItemClick)
                for (i in 0 until paymentOptionList.size) {
                    paymentOptionList[i].isSelect = false
                }
                data.isSelect = true
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = RowPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(paymentOptionList[position], context, position, itemClick)
    }

    override fun getItemCount(): Int {
        return paymentOptionList.size
    }
}