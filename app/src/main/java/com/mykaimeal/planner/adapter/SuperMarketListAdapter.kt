package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.databinding.AdapterLayoutSupermarketBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Store

class SuperMarketListAdapter(private var storesData: MutableList<Store>?,
                             private var requireActivity: FragmentActivity,
                             private var onItemSelectListener: OnItemSelectListener
):
    RecyclerView.Adapter<SuperMarketListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterLayoutSupermarketBinding = AdapterLayoutSupermarketBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data= storesData?.get(position)

        /*if (data!!.store_name!=null){
            holder.binding.tvSuperMarketItems.text=data.store_name.toString()
        }*/

    }

    override fun getItemCount(): Int {
        return storesData!!.size
    }

    class ViewHolder(var binding: AdapterLayoutSupermarketBinding) : RecyclerView.ViewHolder(binding.root){
    }
}
