package com.mykaimeal.planner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.databinding.AdapterLayoutYourRecipeItemBinding
import com.mykaimeal.planner.model.YourRecipeItem

class YourRecipeAdapter(var context: Context, private var itemList: MutableList<YourRecipeItem>,private var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<YourRecipeAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterLayoutYourRecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: YourRecipeItem, position: Int) {
            with(binding) {
                // Bind data to views
                imageFood.setImageResource(item.imageRes)
                tvFoodName.text = item.name
                textServes.text = item.textServes

                imageCross.setOnClickListener {
                    onItemClickListener.itemClick(position,"2","")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterLayoutYourRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], position)


    }

    override fun getItemCount(): Int = itemList.size

    // Add items dynamically
    fun addItems(newItems: List<YourRecipeItem>) {
        val startPosition = itemList.size
        itemList.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
}