package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentAddTipScreenBinding
import com.mykaimeal.planner.databinding.FragmentRecipeDetailsBinding

class AddTipScreenFragment : Fragment() {

    private var binding: FragmentAddTipScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTipScreenBinding.inflate(layoutInflater, container, false)

        return binding!!.root
    }

}