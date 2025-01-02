package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.databinding.AdapterIngredientsRecipeBinding
import com.yesitlabs.mykaapp.databinding.AdapterMealTypeItemBinding
import com.yesitlabs.mykaapp.model.DataModel

class AdapterPlanBreakFast(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity,private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<AdapterPlanBreakFast.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterMealTypeItemBinding = AdapterMealTypeItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvBreakfast.text=datalist[position].title
        holder.binding.tvRatingReviews.text=datalist[position].rating
        holder.binding.textAmounts.text="$"+datalist[position].price+"* per /s"
        holder.binding.relBreakfast.setBackgroundResource(datalist[position].image)

        holder.binding.tvAddToPlan.setOnClickListener{
            onItemClickListener.itemClick(position,"1",datalist[position].type)
        }

        holder.binding.imgBasket.setOnClickListener{
            onItemClickListener.itemClick(position,"2",datalist[position].type)
        }

        holder.binding.relBreakfast.setOnClickListener{
            onItemClickListener.itemClick(position,"3",datalist[position].type)

        }

    }


    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterMealTypeItemBinding) : RecyclerView.ViewHolder(binding.root){

    }
}