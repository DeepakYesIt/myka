package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentBaseketIngredientsDetailsBinding
import com.mykaimeal.planner.databinding.FragmentOrderDetailsScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BasketIngredientsDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBaseketIngredientsDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBaseketIngredientsDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

}