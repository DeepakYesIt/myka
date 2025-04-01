package com.mykaimeal.planner.adapter

import android.graphics.drawable.Drawable
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
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterBasketIngItemBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Ingredient

class IngredientsShoppingAdapter(private var ingredientsData: MutableList<Ingredient>?,
                                 private var requireActivity: FragmentActivity,
                                 private var onItemSelectListener: OnItemSelectListener
):
    RecyclerView.Adapter<IngredientsShoppingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBasketIngItemBinding = AdapterBasketIngItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data= ingredientsData?.get(position)


        if (data != null) {

            if (data.sch_id!=null){
                holder.binding.textCount.text=data.sch_id.toString()
            }

            if (data.pro_price!=null){
                if (data.pro_price!="Not available"){
                    holder.binding.tvFoodPrice.text=data.pro_price.toString()
                }else{
                    holder.binding.tvFoodPrice.text="$00"

                }
            }

            if (data.name!=null){
                holder.binding.tvFoodName.text=data.name
            }

            if (data.pro_img!=null){
                Glide.with(requireActivity)
                    .load(data.pro_img)
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

        holder.binding.imageMinusIcon.setOnClickListener{
            if (ingredientsData?.get(position)?.sch_id.toString().toInt() > 1) {
                onItemSelectListener.itemSelect(position,"Minus","ShoppingIngredients")
            }else{
                Toast.makeText(requireActivity,"Minimum serving at least value is one", Toast.LENGTH_LONG).show()
            }
        }

        holder.binding.imageAddIcon.setOnClickListener{
            if (ingredientsData?.get(position)?.sch_id.toString().toInt() < 1000) {
                onItemSelectListener.itemSelect(position,"Plus","ShoppingIngredients")
            }
        }

    }

    override fun getItemCount(): Int {
        return ingredientsData!!.size
    }

    fun updateList(ingredientList: MutableList<Ingredient>) {
        ingredientsData=ingredientList
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: AdapterBasketIngItemBinding) : RecyclerView.ViewHolder(binding.root){
    }

}