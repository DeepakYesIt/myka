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
import com.mykaimeal.planner.databinding.AdapterProductDetailsSelectItemBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.BasketProductDetailsFragment
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.BasketProductsDetailsModelData

class AdapterProductsDetailsSelectItem(
    private var datalist: MutableList<BasketProductsDetailsModelData>,
    private var requireActivity: FragmentActivity,
    private var onItemSelectListener: OnItemSelectListener
) : RecyclerView.Adapter<AdapterProductsDetailsSelectItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterProductDetailsSelectItemBinding =
            AdapterProductDetailsSelectItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = datalist[position]

        if (data.name != null) {
            holder.binding.textProductName.text = data.name.toString()
        }

        if (data.formatted_price != null) {
            holder.binding.textPrice.text = data.formatted_price.toString()
        }

        data.let {
            // ✅ Load image with Glide
            Glide.with(requireActivity)
                .load(it.image)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.layProgess.root.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.layProgess.root.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.binding.productImage)
        } ?: run {
            holder.binding.layProgess.root.visibility = View.GONE
        }

        /*holder.binding.textProductName.text = datalist[position].title
        holder.binding.textProductQuantity.text = datalist[position].quantity
        holder.binding.textPrice.text = datalist[position].price
        holder.binding.productImage.setImageResource(datalist[position].image)*/


        holder.binding.productImage.setOnClickListener {
            onItemSelectListener.itemSelect(position, data.product_id, "products")
        }

        holder.binding.textProductName.setOnClickListener {
            onItemSelectListener.itemSelect(position, data.product_id, "products")
        }


        holder.binding.productDetails.setOnClickListener {
            onItemSelectListener.itemSelect(position, data.product_id, "swap")
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterProductDetailsSelectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}