package com.mykaimeal.planner.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterBasketIngItemBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Ingredient

class IngredientsAdapter(private var ingredientsData: MutableList<Ingredient>?,
                         private var requireActivity: FragmentActivity,
                         private var onItemSelectListener: OnItemSelectListener
):
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBasketIngItemBinding = AdapterBasketIngItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data= ingredientsData?.get(position)


        if (data != null) {
            holder.binding.tvFoodName.text=data.name
            if (data.image!=null){
                Glide.with(requireActivity)
                    .load(data.image)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.binding.layProgess.root.visibility= View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.binding.layProgess.root.visibility= View.GONE
                            return false
                        }
                    })
                    .into(holder.binding.imageFood)
            }else{
                holder.binding.layProgess.root.visibility= View.GONE
            }

        }

    }

    override fun getItemCount(): Int {
        return ingredientsData!!.size
    }

    class ViewHolder(var binding: AdapterBasketIngItemBinding) : RecyclerView.ViewHolder(binding.root){
    }

}

/*
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
}*/
