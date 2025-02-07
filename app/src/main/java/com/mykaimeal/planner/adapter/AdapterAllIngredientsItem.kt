package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.databinding.AdapterAllIngredientsItemBinding
import com.mykaimeal.planner.model.DataModel

class AdapterAllIngredientsItem(
    private var datalist: List<DataModel>,
    private var requireActivity: FragmentActivity
) : RecyclerView.Adapter<AdapterAllIngredientsItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterAllIngredientsItemBinding =
            AdapterAllIngredientsItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textTittles.text = datalist[position].title
        holder.binding.imageShapeable.setImageResource(datalist[position].image)

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun filterList(filteredList: MutableList<DataModel>) {
        this.datalist = filteredList
        notifyDataSetChanged()
    }


    class ViewHolder(var binding: AdapterAllIngredientsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}