package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterTermsCondition
import com.yesitlabs.mykaapp.databinding.FragmentTermsConditionBinding
import com.yesitlabs.mykaapp.model.DataModel


class TermsConditionFragment : Fragment() {

    private var binding: FragmentTermsConditionBinding? = null
    private var adapterTermsCondition: AdapterTermsCondition? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTermsConditionBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()

                }
            })

        binding!!.imgBackTermsAndCondition.setOnClickListener {
            findNavController().navigateUp()
        }

        initialize()




        return binding!!.root
    }

    private fun initialize() {

        val termsText = getString(R.string.terms_and_conditions)
        binding!!.descText.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(termsText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(termsText)
        }
    }

}