package com.ecommerce.user.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.ecommerce.user.R
import com.ecommerce.user.activity.*
import com.ecommerce.user.api.ApiClient
import com.ecommerce.user.base.BaseFragment
import com.ecommerce.user.databinding.FragAccountBinding
import com.ecommerce.user.model.ContactInfo
import com.ecommerce.user.model.GetProfileResponse
import com.ecommerce.user.model.UserData
import com.ecommerce.user.utils.Common
import com.ecommerce.user.utils.Common.getCurrentLanguage
import com.ecommerce.user.utils.SharePreference
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountFragment : BaseFragment<FragAccountBinding>() {
    private lateinit var accountBinding: FragAccountBinding
    var dataResponse: UserData? = null
    var contactData = ContactInfo()

    @SuppressLint("SetTextI18n")
    override fun initView(view: View) {
        accountBinding = FragAccountBinding.bind(view)
        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            if (Common.isCheckNetwork(requireActivity())) {
                val hasmap = HashMap<String, String>()
                hasmap["user_id"] = SharePreference.getStringPref(
                    requireActivity(),
                    SharePreference.userId
                )!!
                callApiProfile(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.no_internet)
                )
            }
        }
        accountBinding.ivEditprofile.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                onActivityResult.launch(Intent(requireActivity(), ActEditProfile::class.java))
            } else {
                openActivity(ActLogin::class.java)
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }

        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            accountBinding.linearSocialMedia.visibility = View.VISIBLE
        } else {
            accountBinding.linearSocialMedia.visibility = View.GONE
        }
        accountBinding.btnLogout.setOnClickListener { alertLogOutDialog() }
        accountBinding.clSetting.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                openActivity(ActSettings::class.java)
            } else {
                openActivity(ActLogin::class.java)
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }

        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            accountBinding.tvUsername.text =
                SharePreference.getStringPref(requireActivity(), SharePreference.userName)!!
            accountBinding.tvEmail.text =
                SharePreference.getStringPref(requireActivity(), SharePreference.userEmail)!!
        } else if (!SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            accountBinding.tvUsername.text = "Guest"
            accountBinding.tvEmail.text = "guest@gmail.com"
            accountBinding.ivProfile.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.profile,
                    null
                )
            )
            accountBinding.btnLogout.visibility = View.GONE
        } else {
            openActivity(ActLogin::class.java)
            requireActivity().finish()
            requireActivity().finishAffinity()
        }
        accountBinding.clPrivacypolicy.setOnClickListener {
            startActivity(
                Intent(activity, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Policy"
                )
            )
        }

        accountBinding.clAboutus.setOnClickListener {
            startActivity(
                Intent(activity, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "About"
                )
            )
        }
        accountBinding.clHelp.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                val intent = Intent(requireActivity(), ActHelpContactUs::class.java)
                val bundle = Bundle()
                bundle.putSerializable("contact", contactData)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                openActivity(ActLogin::class.java)
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }

        accountBinding.ivInstagram.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.instagram)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.ivLinkedIn.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.linkedin)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }
        accountBinding.ivFaceBook.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.facebook)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.ivTwitter.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.twitter)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    override fun getBinding(): FragAccountBinding {
        accountBinding = FragAccountBinding.inflate(layoutInflater)
        return accountBinding
    }

    //TODO USER LOGOUT DIALOG AND LOGOUT
    private fun alertLogOutDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.log_out)
        builder.setMessage(R.string.logout_text)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            Common.setLogout(requireActivity())
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    //TODO API PROFILE CALL
    private fun callApiProfile(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(requireActivity())
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<GetProfileResponse> {
            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: GetProfileResponse = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        dataResponse = restResponce.data!!
                        contactData = restResponce.contactinfo!!
                        SharePreference.setStringPref(
                            requireActivity(),
                            SharePreference.userName,
                            dataResponse!!.name!!
                        )
                        SharePreference.setStringPref(
                            requireActivity(),
                            SharePreference.userProfile,
                            dataResponse!!.profilePic!!
                        )
                        SharePreference.setStringPref(
                            requireActivity(),
                            SharePreference.userEmail,
                            dataResponse!!.email!!
                        )
                        setProfileData()
                    } else if (restResponce.data!!.equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponce.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        Common.dismissLoadingProgress()
                        Common.setLogout(requireActivity())
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            requireActivity(),
                            error.getString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET PROFILE DATA
    private fun setProfileData(
    ) {
        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            accountBinding.tvUsername.text =
                SharePreference.getStringPref(requireActivity(), SharePreference.userName)
            accountBinding.tvEmail.text =
                SharePreference.getStringPref(requireActivity(), SharePreference.userEmail)
            Glide.with(requireActivity()).load(
                SharePreference.getStringPref(
                    requireActivity(),
                    SharePreference.userProfile
                )
            ).placeholder(ResourcesCompat.getDrawable(resources, R.drawable.profile, null))
                .into(accountBinding.ivProfile)

        } else {
            accountBinding.ivProfile.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_elogo,
                    null
                )
            )
        }
    }

    private var onActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            if (Common.isCheckNetwork(requireActivity())) {
                val hasmap = HashMap<String, String>()
                hasmap["user_id"] = SharePreference.getStringPref(
                    requireActivity(),
                    SharePreference.userId
                )!!
                callApiProfile(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(requireActivity(), false)
    }
}