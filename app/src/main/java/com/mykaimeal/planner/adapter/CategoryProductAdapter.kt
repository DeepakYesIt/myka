package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.databinding.ItemSectionHeaderBinding
import com.mykaimeal.planner.model.DataModel

class CategoryProductAdapter(private val products: List<DataModel>, private var onItemSelectListener: com.mykaimeal.planner.OnItemSelectListener) : RecyclerView.Adapter<CategoryProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemSectionHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(private val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: DataModel) {
            binding.textProductName.text = product.title
            binding.productImage.setImageResource(product.image)
/*
            binding.textProductQuantity.text = product.quantity
*/
            binding.textPrice.text = "$${product.valuePrice.toString()}"

            // Implement quantity controls
            binding.imageDecreaseQuantity.setOnClickListener {
                // Decrease quantity logic
            }
            binding.imageIncreaseQuantity.setOnClickListener {
                // Increase quantity logic
            }

            binding.productSwap.setOnClickListener {
                onItemSelectListener.itemSelect(0,"2","")
            }
        }
    }
}
