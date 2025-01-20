package com.mykameal.planner.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.databinding.AdapterLayoutFoodItemsListBinding
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.Breakfast

class AdapterFoodListItem(private var itemList: List<Breakfast>, private var requireActivity: FragmentActivity, private var onItemSelectListener: OnItemClickListener) : RecyclerView.Adapter<AdapterFoodListItem.ViewHolder>() {

    private var quantity:Int=1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterLayoutFoodItemsListBinding = AdapterLayoutFoodItemsListBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvBreakfast.text = itemList[position].recipe.label

        if (itemList[position].is_like!=null){
            if (itemList[position].is_like ==0 ){
                holder.binding.imgHeartRed.setImageResource(R.drawable.heart_white_icon)
            }else{
                holder.binding.imgHeartRed.setImageResource(R.drawable.heart_red_icon)
            }
        }

        if (itemList[position].created_date!=null){
            if (itemList[position].created_date!=""){
                holder.binding.textTimeAgo.text="Serves "+itemList[position].created_date.toString()
            }
        }


        if (itemList[position].servings!=null){
            holder.binding.tvServes.text=itemList[position].servings.toString()
        }

        if (itemList[position].recipe.images.SMALL.url !=null){
            Glide.with(requireActivity)
                .load(itemList[position].recipe.images.SMALL.url)
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
                .into(holder.binding.imageData)
        }else{
            holder.binding.layProgess.root.visibility= View.GONE
        }

        holder.binding.cardViews.setOnClickListener{
            onItemSelectListener.itemClick(position,"","Christmas")
        }

        holder.binding.imageMinusItem.setOnClickListener{
            if (quantity > 1) {
                quantity--
                updateValue(holder.binding.tvServes)
            }else{
                Toast.makeText(requireActivity,"Minimum serving atleast value is one",
                    Toast.LENGTH_LONG).show()
            }
        }

        holder.binding.imagePlusItem.setOnClickListener{
            if (quantity < 99) {
                quantity++
                updateValue(holder.binding.tvServes)
            }
        }

        holder.binding.imgAppleRemove.setOnClickListener {
            onItemSelectListener.itemClick(position, "1", "")
        }

//        holder.binding.cardViews.setOnClickListener{
//            holder.binding.cardViews.setBackgroundResource(R.drawable.outline_green_box_bg)
//        }

    }

    private fun updateValue(tvServes: TextView) {
        tvServes.text ="Serves"+ String.format("%02d", quantity)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    class ViewHolder(var binding: AdapterLayoutFoodItemsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}