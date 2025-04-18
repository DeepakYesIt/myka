package com.mykaimeal.planner.adapter

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
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.MealType

class AdapterFilterMealItem(
    private var datalist: MutableList<MealType>?,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickedListener
) : RecyclerView.Adapter<AdapterFilterMealItem.ViewHolder>() {

    private val selectedItems = mutableListOf<String>() // Declare this in your adapter or pass it from outside
    private var isExpanded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSearchFilterItemBinding =
            AdapterSearchFilterItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist!![position]

        holder.binding.tvItem.text = datalist!![position].name

        // Update UI based on selection state
        if (item.name=="More") {
            holder.binding.tvItem.setTextColor(Color.parseColor("#06C169"))
            holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_bg)
        } else {
            holder.binding.tvItem.setTextColor(Color.parseColor("#000000"))
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
            onItemClickListener.itemClicked(position,selectedItems,item.name,"MealType")

        }
    }


    override fun getItemCount(): Int {
        return if (isExpanded) datalist!!.size else 5.coerceAtMost(datalist!!.size)


      /*  return datalist!!.size*/
//        return if (isExpanded) datalist!!.size else datalist!!.take(5).size + 1
    }

    fun updateList(newList: MutableList<MealType>?) {
        datalist!!.clear()
        newList?.let { datalist!!.addAll(it) }
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: AdapterSearchFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}