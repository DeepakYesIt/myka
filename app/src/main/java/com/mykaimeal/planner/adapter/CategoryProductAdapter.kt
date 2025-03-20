package com.mykaimeal.planner.adapter

import android.graphics.drawable.Drawable
import android.util.Log
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
import com.mykaimeal.planner.databinding.ItemSectionHeaderBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket.model.Product

class CategoryProductAdapter(
    private var requireActivity: FragmentActivity,
    private val products: MutableList<Product>?,
    private var onItemSelectListener: OnItemSelectListener
) : RecyclerView.Adapter<CategoryProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemSectionHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products!![position])
    }

    override fun getItemCount(): Int = products!!.size

    inner class ProductViewHolder(private val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {

            if (product.name!=null){
                binding.textProductName.text = product.name
            }

            if (product.sch_id!=null){
                binding.textProductQuantityValue.text=product.sch_id.toString()
            }

            Log.d("image","******"+product.pro_img)

            if (product.pro_img!=null){
                Glide.with(requireActivity)
                    .load(product.pro_img)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.layProgess.root.visibility= View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.layProgess.root.visibility= View.GONE
                            return false
                        }
                    })
                    .into(binding.productImage)
            }else{
                binding.layProgess.root.visibility= View.GONE
            }
/*
            binding.productImage.setImageResource(product.image)
*/
/*
            binding.textProductQuantity.text = product.quantity
*/

            if (product.pro_price!=null){

                if (product.pro_price!= "Not available"){
                    binding.textPrice.text = "$${product.pro_price.toString()}"
                }
            }

            // Implement quantity controls
            binding.imageDecreaseQuantity.setOnClickListener {
                // Decrease quantity logic
            }
            binding.imageIncreaseQuantity.setOnClickListener {
                // Increase quantity logic
            }

            binding.imageSwap.setOnClickListener {
                onItemSelectListener.itemSelect(0,product.name.toString(),product.pro_id.toString())
            }
        }
    }
}
