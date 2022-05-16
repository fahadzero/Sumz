package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.user.R
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.base.BaseAdaptor
import com.ecommerce.user.databinding.ActNotificationBinding
import com.ecommerce.user.databinding.RowNotificationBinding
import com.ecommerce.user.model.NotificationDataItem
import com.ecommerce.user.model.NotificationsResponse
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class ActNotification : BaseActivity() {

    private lateinit var notificationBinding: ActNotificationBinding
    private var notificationsList = ArrayList<NotificationDataItem>()
    private var viewAllDataAdapter: BaseAdaptor<NotificationDataItem, RowNotificationBinding>? =
        null
    private var manager: LinearLayoutManager? = null
    private var currentPage = 1
    var total_pages: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisibleItems = 0

    override fun setLayout(): View = notificationBinding.root

    override fun initView() {
        notificationBinding = ActNotificationBinding.inflate(layoutInflater)
        manager =
            LinearLayoutManager(this@ActNotification, LinearLayoutManager.VERTICAL, false)
        loadNotificationDetails(notificationsList)
        notificationBinding.ivBack.setOnClickListener {
            finish()
        }
        if (Common.isCheckNetwork(this@ActNotification)) {
            if (SharePreference.getBooleanPref(this@ActNotification, SharePreference.isLogin)) {
                callApiNotifications()
            } else {
                openActivity(ActLogin::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActNotification,
                resources.getString(R.string.no_internet)
            )
        }
        notificationBinding.rvNotification.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager!!.childCount
                    totalItemCount = manager!!.itemCount
                    pastVisibleItems = manager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            callApiNotifications()
                        }
                    }
                }
            }
        })
    }

    //TODO API NOTIFICATION CALL
    private fun callApiNotifications() {
        Common.showLoadingProgress(this@ActNotification)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActNotification, SharePreference.userId)!!
        val call = ApiClient.getClient.getNotificatios(currentPage.toString(), hasmap)
        call.enqueue(object : Callback<NotificationsResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<NotificationsResponse>,
                response: Response<NotificationsResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == 1) {
                            notificationsList.clear()
                        }
                        restResponce.data?.data?.let {
                            notificationsList.addAll(it)
                        }
                        this@ActNotification.currentPage = restResponce.data?.currentPage!!.toInt()
                        this@ActNotification.total_pages = restResponce.data.lastPage!!.toInt()
                        loadNotificationDetails(notificationsList)

                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActNotification,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActNotification,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET NOTIFICATION DATA
    private fun loadNotificationDetails(notificationsList: ArrayList<NotificationDataItem>) {
        lateinit var binding: RowNotificationBinding
        viewAllDataAdapter =
            object : BaseAdaptor<NotificationDataItem, RowNotificationBinding>(
                this@ActNotification,
                notificationsList
            ) {
                @SuppressLint(
                    "NewApi", "ResourceType", "SetTextI18n",
                    "UseCompatLoadingForDrawables"
                )
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: NotificationDataItem,
                    position: Int
                ) {
                    when (notificationsList[position].orderStatus) {
                        1 -> {
                            binding.tvnotificationsName.text = getString(R.string.order_place)
                            binding.ivnotification.setImageResource(R.drawable.ic_orderplace)
                            binding.ivnotification.background = getDrawable(R.drawable.orderplace)
                        }
                        2 -> {
                            binding.tvnotificationsName.text = getString(R.string.order_confirmed)
                            binding.ivnotification.setImageResource(R.drawable.ic_orderconfirmed)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.orderconfirmed)
                        }
                        3 -> {
                            binding.tvnotificationsName.text = getString(R.string.order_shipped)
                            binding.ivnotification.setImageResource(R.drawable.delivery)
                            binding.ivnotification.background = getDrawable(R.drawable.ordershipped)
                        }
                        4 -> {
                            binding.tvnotificationsName.text = getString(R.string.order_delivered)
                            binding.ivnotification.setImageResource(R.drawable.orderdelivery)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.orderdelivered)
                        }
                        5 -> {
                            binding.tvnotificationsName.text = getString(R.string.order_cancelled)
                            binding.ivnotification.setImageResource(R.drawable.ordercancelledpackage)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.ordercancelled)
                        }
                        6 -> {
                            binding.tvnotificationsName.text = getString(R.string.order_cancelled)
                            binding.ivnotification.setImageResource(R.drawable.ordercancelledpackage)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.ordercancelled)
                        }
                        7 -> {
                            binding.tvnotificationsName.text =
                                getString(R.string.order_return_created)
                            binding.ivnotification.setImageResource(R.drawable.ic_orderreturn)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.orderreturn)
                        }
                        8 -> {
                            binding.tvnotificationsName.text =
                                getString(R.string.order_return_accepted)
                            binding.ivnotification.setImageResource(R.drawable.orderdelivery)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.orderdelivered)
                        }
                        9 -> {
                            binding.tvnotificationsName.text =
                                getString(R.string.order_return_completed)
                            binding.ivnotification.setImageResource(R.drawable.delivery)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.ordershipped)
                        }
                        10 -> {
                            binding.tvnotificationsName.text =
                                getString(R.string.order_return_rejected)
                            binding.ivnotification.setImageResource(R.drawable.ordercancelledpackage)
                            binding.ivnotification.background =
                                getDrawable(R.drawable.ordercancelled)
                        }
                    }
                    binding.tvdeliverydate.text = notificationsList[position].date?.let {
                        Common.getDate(
                            it
                        )
                    }
                    binding.tvDeliveryDetails.text = notificationsList[position].message
                    holder?.itemView?.setOnClickListener {
                        val orderstatus = notificationsList[position].orderStatus.toString()
                        if (orderstatus == "7" || orderstatus == "8" || orderstatus == "9" || orderstatus == "10") {
                            Log.e(
                                "order_number--->",
                                notificationsList[position].orderId.toString()
                            )
                            val intent =
                                Intent(this@ActNotification, ActReturnTrackOrder::class.java)
                            intent.putExtra(
                                "orderId",
                                notificationsList[position].orderId.toString()
                            )
                            startActivity(intent)
                        } else if (orderstatus=="2"||orderstatus=="3"||orderstatus=="4") {
                            Log.e(
                                "order_number--->",
                                notificationsList[position].orderNumber.toString()
                            )
                            val intent = Intent(this@ActNotification, ActTrackOrder::class.java)
                            intent.putExtra(
                                "order_id",
                                notificationsList[position].orderId.toString()
                            )
                            startActivity(intent)
                        }else{
                            Log.e(
                                "order_number--->",
                                notificationsList[position].orderNumber.toString()
                            )
                            val intent = Intent(this@ActNotification, ActOrderDetails::class.java)
                            intent.putExtra(
                                "order_number",
                                notificationsList[position].orderNumber.toString()
                            )
                            startActivity(intent)
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_notification
                }
                override fun getBinding(parent: ViewGroup): RowNotificationBinding {
                    binding = RowNotificationBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        notificationBinding.rvNotification.apply {
            if (notificationsList.size > 0) {
                notificationBinding.rvNotification.visibility = View.VISIBLE
                notificationBinding.tvNoDataFound.visibility = View.GONE
                layoutManager = manager
                itemAnimator = DefaultItemAnimator()
                adapter = viewAllDataAdapter
            } else {
                notificationBinding.rvNotification.visibility = View.GONE
                notificationBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActNotification, false)
    }
}