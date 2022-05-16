package com.ecommerce.user.activity

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.ecommerce.user.R
import com.ecommerce.user.base.BaseActivity
import com.ecommerce.user.databinding.ActSettingsBinding
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.getCurrentLanguage
import com.ecommerce.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class ActSettings : BaseActivity() {
    private lateinit var settingsBinding: ActSettingsBinding
    override fun setLayout(): View = settingsBinding.root

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        settingsBinding = ActSettingsBinding.inflate(layoutInflater)
        settingsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        settingsBinding.clChangepassword.setOnClickListener {

            openActivity(
                ActChangePassword::class.java
            )
        }
        settingsBinding.clMyaddress.setOnClickListener {
            openActivity(
                ActAddress::class.java
            )
        }
        settingsBinding.clWallet.setOnClickListener {
            openActivity(ActWallet::class.java)
        }
        settingsBinding.clOffers.setOnClickListener {
            openActivity(ActOffers::class.java)
        }
        settingsBinding.clChnagelayout.setOnClickListener {
            val dialog = BottomSheetDialog(this@ActSettings)
            if (Common.isCheckNetwork(this@ActSettings)) {
                val view =
                    layoutInflater.inflate(R.layout.row_bottomsheetlayout, null)
                dialog.window?.setBackgroundDrawable(getDrawable(R.color.tr))
                val btnLTREng = view.findViewById<TextView>(R.id.tvLTR)
                val btnRTLHindi = view.findViewById<TextView>(R.id.tvRTL)
                val btncancel = view.findViewById<TextView>(R.id.tvCancel)
                btncancel.setOnClickListener {
                    dialog.dismiss()
                }
                btnLTREng.setOnClickListener {
                    SharePreference.setStringPref(
                        this@ActSettings,
                        SharePreference.SELECTED_LANGUAGE,
                        this@ActSettings.resources.getString(R.string.language_english)
                    )
                    getCurrentLanguage(this@ActSettings, true)
                }
                btnRTLHindi.setOnClickListener {
                    SharePreference.setStringPref(
                        this@ActSettings,
                        SharePreference.SELECTED_LANGUAGE,
                        this@ActSettings.resources.getString(R.string.language_hindi)
                    )
                    getCurrentLanguage(this@ActSettings, true)
                }
                dialog.setCancelable(false)
                dialog.setContentView(view)
                dialog.show()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActSettings,
                    resources.getString(R.string.no_internet)
                )
                dialog.dismiss()
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this@ActSettings, false)

    }
}
