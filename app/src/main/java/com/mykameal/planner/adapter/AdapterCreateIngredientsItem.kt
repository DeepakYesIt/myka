package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.databinding.AdapterCreateIngredientsItemBinding

class AdapterCreateIngredientsItem(private var datalist: MutableList<String>, private var requireActivity: FragmentActivity, private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterCreateIngredientsItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterCreateIngredientsItemBinding =
            AdapterCreateIngredientsItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun addNewItem() {
        datalist.add("") // Add a blank item
        notifyItemInserted(datalist.size - 1)
    }

    class ViewHolder(var binding: AdapterCreateIngredientsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}