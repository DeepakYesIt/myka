package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.databinding.AdapterMealItemBinding
import com.mykameal.planner.model.DataModel

class SearchMealAdapter(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity): RecyclerView.Adapter<SearchMealAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterMealItemBinding = AdapterMealItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvRecipeName.text=datalist[position].title
        holder.binding.imgRecipe.setImageResource(datalist[position].image)

    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterMealItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

}