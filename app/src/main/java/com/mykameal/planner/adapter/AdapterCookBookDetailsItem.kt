package com.mykameal.planner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.databinding.AdapterCookbookDetailsItemBinding
import com.mykameal.planner.model.DataModel

class AdapterCookBookDetailsItem(private var datalist: List<DataModel>, var requireActivity: FragmentActivity,private var onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<AdapterCookBookDetailsItem.ViewHolder>() {

    private var isOpened:Boolean?=false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterCookbookDetailsItemBinding =
            AdapterCookbookDetailsItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (datalist[position].type=="ChristmasCollection"){
            holder.binding.imgThreeDot.visibility=View.GONE
        }else{
            holder.binding.imgThreeDot.visibility=View.VISIBLE

        }

        holder.binding.tvBreakfast.text = datalist[position].title

        holder.binding.imgThreeDot.setOnClickListener{
            if (isOpened==true){
                isOpened=false
                holder.binding.cardViewItems.visibility=View.GONE
            }else{
                isOpened=true
                holder.binding.cardViewItems.visibility=View.VISIBLE
            }
        }

        holder.binding.tvRemoveRecipe.setOnClickListener{
            onItemClickListener.itemClick(position,"","remove")
        }

        holder.binding.tvMoveRecipe.setOnClickListener{
            onItemClickListener.itemClick(position,"","move")

        }




        holder.binding.tvAddToPlan.setOnClickListener{
            onItemClickListener.itemClick(position,"","plan")
        }

        holder.binding.basketImg.setOnClickListener{
            onItemClickListener.itemClick(position,"","basket")
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterCookbookDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}