package com.mykaimeal.planner.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.OnItemSelectUnSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.ItemSectionHeaderBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket.model.Product

class CategoryProductAdapter(
    private var requireActivity: FragmentActivity,
    private var products: MutableList<Product>,
    private var onItemSelectListener: OnItemSelectUnSelectListener
) : RecyclerView.Adapter<CategoryProductAdapter.ProductViewHolder>() {

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
    fun updateList(productss: MutableList<Product>) {
        products=productss
        notifyDataSetChanged()

    }

    inner class ProductViewHolder(private val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {

            if (product.pro_name!=null){
                val foodName = product.pro_name
                val result = foodName.mapIndexed { index, c ->
                    if (index == 0 || c.isUpperCase()) c.uppercaseChar() else c
                }.joinToString("")
                binding.textProductName.text=result
            }

            if (product.pro_id != null) {
                if (product.pro_id != "Not available") {
                    binding.imageSwap.visibility = View.VISIBLE
                } else {
                    binding.imageSwap.visibility = View.GONE
                }
            }

            if (product.sch_id != null) {
                binding.textProductQuantityValue.text = product.sch_id.toString()
            }

            Log.d("image", "******" + product.pro_img)

            if (product.pro_img != null) {
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
                            binding.layProgess.root.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.layProgess.root.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.productImage)
            } else {
                binding.layProgess.root.visibility = View.GONE
            }
            /*
                        binding.productImage.setImageResource(product.image)
            */
            /*
                        binding.textProductQuantity.text = product.quantity
            */

            if (product.pro_price != null) {
                if (product.pro_price != "Not available") {
                    binding.textPrice.text = "${product.pro_price.toString()}"
                }
            }

            // Implement quantity controls
            binding.imageDecreaseQuantity.setOnClickListener {
                if (product.sch_id.toString().toInt() > 1) {
                    onItemSelectListener.itemSelectUnSelect(position,"Minus","Product",position)
                }else{
                    Toast.makeText(requireActivity,"Minimum serving at least value is one", Toast.LENGTH_LONG).show()
                }
                // Decrease quantity logic
            }
            binding.imageIncreaseQuantity.setOnClickListener {
                // Increase quantity logic
                if (product.sch_id.toString().toInt() < 1000) {
                    onItemSelectListener.itemSelectUnSelect(position,"Plus","Product", position)
                }
            }

            binding.imageSwap.setOnClickListener {
                onItemSelectListener.itemSelectUnSelect(product.id,product.name.toString(),"Product", position)
              /*  onItemSelectListener.itemSelect(
                    product.id,
                    product.name.toString(),
                    product.pro_id.toString()
                )*/
        }
    }
}
}
