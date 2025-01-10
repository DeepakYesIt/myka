package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.databinding.AdapterProductDetailsSelectItemBinding
import com.mykameal.planner.model.DataModel

class AdapterProductsDetailsSelectItem(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity
) : RecyclerView.Adapter<AdapterProductsDetailsSelectItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterProductDetailsSelectItemBinding =
            AdapterProductDetailsSelectItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textProductName.text = datalist[position].title
        holder.binding.textProductQuantity.text = datalist[position].quantity
        holder.binding.textPrice.text = datalist[position].price
        holder.binding.productImage.setImageResource(datalist[position].image)

    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterProductDetailsSelectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}