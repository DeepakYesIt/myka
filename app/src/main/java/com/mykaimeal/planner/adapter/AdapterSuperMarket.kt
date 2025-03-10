package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.databinding.AdapterSuperMarketItemBinding
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse.SuperMarketModelData

class AdapterSuperMarket(private var datalist: List<SuperMarketModelData>?, private var requireActivity: FragmentActivity, private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<AdapterSuperMarket.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSuperMarketItemBinding = AdapterSuperMarketItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textName.text=datalist!![position].store_name
        /*holder.binding.textAmounts.text="$"+datalist[position].price
        holder.binding.imgSuperMarket.setImageResource(datalist[position].image)*/
    }

    override fun getItemCount(): Int {
        return datalist!!.size
    }

    class ViewHolder(var binding: AdapterSuperMarketItemBinding) : RecyclerView.ViewHolder(binding.root){

    }
}