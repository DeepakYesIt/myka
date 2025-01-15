package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.databinding.AdapterLayoutFoodItemsListBinding
import com.mykameal.planner.model.DataModel

class AdapterFoodListItem(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity, private var onItemSelectListener: OnItemClickListener) : RecyclerView.Adapter<AdapterFoodListItem.ViewHolder>() {

    private var quantity:Int=1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterLayoutFoodItemsListBinding =
            AdapterLayoutFoodItemsListBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvBreakfast.text = datalist[position].title
//        holder.binding.imageData.setBackgroundResource(datalist[position].image)

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
        return datalist.size
    }


    class ViewHolder(var binding: AdapterLayoutFoodItemsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}