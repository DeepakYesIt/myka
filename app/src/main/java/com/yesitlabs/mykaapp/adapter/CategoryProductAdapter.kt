package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.databinding.ItemSectionHeaderBinding
import com.yesitlabs.mykaapp.model.DataModel

class CategoryProductAdapter(private val products: List<DataModel>, private var onItemSelectListener: com.yesitlabs.mykaapp.OnItemSelectListener) : RecyclerView.Adapter<CategoryProductAdapter.ProductViewHolder>() {

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
            binding.textProductQuantity.text = product.quantity
            binding.textPrice.text = "$${product.value}"

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
