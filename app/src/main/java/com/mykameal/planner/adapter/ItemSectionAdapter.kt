package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemSelectListener
import com.mykameal.planner.databinding.ItemProductCardBinding
import com.mykameal.planner.model.DataModel

class ItemSectionAdapter (private val data: HashMap<String, List<DataModel>>,private var onItemSelectListener: OnItemSelectListener) : RecyclerView.Adapter<ItemSectionAdapter.CategoryCardViewHolder>() {

    private val categoryList: List<Pair<String, List<DataModel>>> = data.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryCardViewHolder {
        val binding = ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryCardViewHolder, position: Int) {
        val (category, products) = categoryList[position]
        holder.bind(category, products)
    }

    override fun getItemCount(): Int = categoryList.size

    inner class CategoryCardViewHolder(private val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String, products: List<DataModel>) {
            binding.categoryTitle.text = category
            val adapter = CategoryProductAdapter(products,onItemSelectListener)
            binding.productRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.productRecyclerView.adapter = adapter

        }
    }
}