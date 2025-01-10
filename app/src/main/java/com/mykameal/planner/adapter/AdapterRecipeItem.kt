package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.databinding.AdapterRecipeItemBinding
import com.mykameal.planner.model.DataModel

class AdapterRecipeItem(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity): RecyclerView.Adapter<AdapterRecipeItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterRecipeItemBinding =
            AdapterRecipeItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text = datalist[position].title
        holder.binding.tvTitleDescriptions.text = datalist[position].description


    }


    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}