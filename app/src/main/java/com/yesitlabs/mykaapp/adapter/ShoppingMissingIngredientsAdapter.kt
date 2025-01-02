package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.databinding.LayoutShoppingMissingIngredientsBinding
import com.yesitlabs.mykaapp.model.DataModel

class ShoppingMissingIngredientsAdapter(private val foodList: MutableList<DataModel>) : RecyclerView.Adapter<ShoppingMissingIngredientsAdapter.FoodViewHolder>() {

    private  var isCheckEnabled = false

    class FoodViewHolder(var binding : LayoutShoppingMissingIngredientsBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = LayoutShoppingMissingIngredientsBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodList[position]

        // Bind data to the views
        holder.binding.imageFood.setImageResource(foodItem.image)
        holder.binding.tvFoodName.text = foodItem.title
        holder.binding.tvFoodGram.text = foodItem.quantity

        if (isCheckEnabled){
            holder.binding.checkbox.isChecked=true
        }else{
            holder.binding.checkbox.isChecked=false
        }

    }

    override fun getItemCount(): Int = foodList.size

    fun setCheckEnabled(enabled: Boolean) {
        isCheckEnabled=enabled
        notifyDataSetChanged()

    }


}