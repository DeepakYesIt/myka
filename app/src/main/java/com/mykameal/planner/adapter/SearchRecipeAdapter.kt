package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mykameal.planner.R
import com.mykameal.planner.databinding.AdapterSearchRecipeBinding
import com.mykameal.planner.fragment.mainfragment.searchtab.searchscreen.model.Ingredient

class SearchRecipeAdapter(
    private var ingredientsList: MutableList<Ingredient>,
    private var requireActivity: FragmentActivity
) : RecyclerView.Adapter<SearchRecipeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSearchRecipeBinding =
            AdapterSearchRecipeBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvRecipeName.text = ingredientsList[position].food

        Glide.with(requireActivity)
            .load(ingredientsList[position].image)
            .placeholder(R.drawable.mask_group_icon)
            .error(R.drawable.mask_group_icon)
            .into(holder.binding.imgRecipe)
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }


    class ViewHolder(var binding: AdapterSearchRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}