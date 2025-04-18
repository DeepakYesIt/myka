package com.mykaimeal.planner.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemClickedListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterSearchFilterItemBinding
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.Diet

class AdapterFilterDietItem(
    private var datalist: MutableList<Diet>?,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickedListener
) : RecyclerView.Adapter<AdapterFilterDietItem.ViewHolder>() {

    private val selectedItems = mutableListOf<String>() // Declare this in your adapter or pass it from outside

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSearchFilterItemBinding =
            AdapterSearchFilterItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = datalist?.get(position)

        holder.binding.tvItem.text = item?.name

        // Style for "More" item
        if (item?.name == "More") {
            holder.binding.tvItem.setTextColor(Color.parseColor("#06C169"))
            holder.binding.relMainLayouts.background = null
        } else {
            holder.binding.tvItem.setTextColor(Color.parseColor("#000000"))
            if (item?.selected == true) {
                holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_bg)
            } else {
                holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
            }
        }

        holder.binding.relMainLayouts.setOnClickListener {
            if (item?.name != "More") {
                item?.selected = !(item?.selected ?: false) // toggle selection
                if (item?.selected == true) {
                    holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_bg)
                    if (!selectedItems.contains(item?.name)) {
                        item?.name?.let { it1 -> selectedItems.add(it1) }
                    }
                } else {
                    holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
                    selectedItems.remove(item?.name)
                }

                onItemClickListener.itemClicked(position, selectedItems, item?.name, "Diet")
            } else {
                // Optional: handle "More" click if needed
                onItemClickListener.itemClicked(position, selectedItems, item.name, "Diet")
            }
        }


     /*   val item = datalist!![position]

        holder.binding.tvItem.text = item.name

        if (item.name=="More") {
            holder.binding.tvItem.setTextColor(android.graphics.Color.parseColor("#06C169"))
            holder.binding.relMainLayouts.background = null
        } else {
            holder.binding.tvItem.setTextColor(android.graphics.Color.parseColor("#000000"))
            holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
        }

        holder.binding.relMainLayouts.setOnClickListener {
            item.selected = !(item.selected ?: false) // Toggle selection safely

            if (item.selected == true) {
                holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_bg)
                if (!selectedItems.contains(item.name)) {
                    selectedItems.add(item.name.toString())
                }
            } else {
                holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
                selectedItems.remove(item.name)
            }
            // Notify listener (if needed)
            onItemClickListener.itemClicked(position, selectedItems, item.name, "Diet")
        }*/
    }

    override fun getItemCount(): Int {
        return datalist!!.size
    }

    fun updateList(newList: MutableList<Diet>?) {
        datalist!!.clear()
        newList?.let { datalist!!.addAll(it) }
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: AdapterSearchFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}