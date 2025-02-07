package com.mykaimeal.planner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.databinding.AdapterLayoutSupermarketBinding
import com.mykaimeal.planner.model.MarketItem

class SuperMarketListAdapter(var context: Context, private var itemList: MutableList<MarketItem>,private var onItemSelectListener: OnItemSelectListener) : RecyclerView.Adapter<SuperMarketListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterLayoutSupermarketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarketItem, position: Int) { with(binding) {
                // Bind data to views
                imageSuperMarket.setImageResource(item.imageSuperMarket)
                tvSuperMarketName.text = item.tvSuperMarketName
                tvSuperMarketRupees.text = item.tvSuperMarketRupees
                textMiles.text = item.textMiles

            cardViewMainLayout.setOnClickListener{
                onItemSelectListener.itemSelect(position,"","")
            }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterLayoutSupermarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], position)
    }

    override fun getItemCount(): Int = itemList.size

    // Add items dynamically
    fun addItems(newItems: List<MarketItem>) {
        val startPosition = itemList.size
        itemList.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
}