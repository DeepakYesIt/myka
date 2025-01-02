package com.yesitlabs.mykaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.databinding.AdapterBasketIngItemBinding
import com.yesitlabs.mykaapp.model.IngredientsItems


class IngredientsAdapter(var context: Context, private var itemList: MutableList<IngredientsItems>) : RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterBasketIngItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IngredientsItems, position: Int) {
            with(binding) {
                // Bind data to views
                imageFood.setImageResource(item.imageFood)
                tvFoodName.text = item.tvFoodName
                tvFoodGram.text = item.tvFoodGram
                tvFoodPrice.text = item.tvFoodPrice

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterBasketIngItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], position)
    }

    override fun getItemCount(): Int = itemList.size

    // Add items dynamically
    fun addItems(newItems: List<IngredientsItems>) {
        val startPosition = itemList.size
        itemList.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
}