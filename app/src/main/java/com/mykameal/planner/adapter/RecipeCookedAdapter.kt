package com.mykameal.planner.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.databinding.AdapterIngredientsItemBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse.UserDataModel

class RecipeCookedAdapter(var datalist: MutableList<UserDataModel>?) : RecyclerView.Adapter<RecipeCookedAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterIngredientsItemBinding =
            AdapterIngredientsItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data= datalist?.get(position)

        holder.binding.imageMinus.visibility=View.GONE

        if (data != null) {
            holder.binding.tvBreakfast.text=data.recipe?.label
        }



    }



    override fun getItemCount(): Int {
        return datalist?.size!!
    }

    class ViewHolder(var binding: AdapterIngredientsItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }


}