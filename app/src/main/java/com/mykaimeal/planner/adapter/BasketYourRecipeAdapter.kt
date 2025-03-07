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
import com.mykaimeal.planner.databinding.AdapterLayoutYourRecipeItemBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Recipes

class BasketYourRecipeAdapter(private var yourRecipesData: MutableList<Recipes>?,
                              private var requireActivity: FragmentActivity,
                              private var onItemSelectListener: OnItemSelectListener):
    RecyclerView.Adapter<BasketYourRecipeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterLayoutYourRecipeItemBinding = AdapterLayoutYourRecipeItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data= yourRecipesData?.get(position)


        if (data != null) {
            holder.binding.tvFoodName.text=data.data!!.recipe?.label
            if (data.data.recipe?.images?.SMALL?.url!=null){
                Glide.with(requireActivity)
                    .load(data.data.recipe.images.SMALL.url)
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
        return yourRecipesData!!.size
    }

    class ViewHolder(var binding: AdapterLayoutYourRecipeItemBinding) : RecyclerView.ViewHolder(binding.root){
    }

}

/*
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
}*/
