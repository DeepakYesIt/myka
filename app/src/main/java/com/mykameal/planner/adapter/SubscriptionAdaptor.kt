package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.model.SubscriptionsModel
import com.mykameal.planner.databinding.AdapterSubscriptionItemBinding

class SubscriptionAdaptor(private var datalist: List<SubscriptionsModel>, private var requireActivity: FragmentActivity): RecyclerView.Adapter<SubscriptionAdaptor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSubscriptionItemBinding = AdapterSubscriptionItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textTitle1.text=datalist[position].textTitle1
        holder.binding.textAmount.text=datalist[position].textAmount
        holder.binding.textAmount2.text=datalist[position].textAmount2

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    class ViewHolder(var binding: AdapterSubscriptionItemBinding) : RecyclerView.ViewHolder(binding.root){

    }
}