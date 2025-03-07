package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterSearchFilterItemBinding
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.CookTime

class AdapterFilterCookTimeItem(
    private var datalist: MutableList<CookTime>?,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterFilterCookTimeItem.ViewHolder>() {

    private var selected: Boolean = false
    private var isExpanded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSearchFilterItemBinding =
            AdapterSearchFilterItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvItem.text = datalist!![position].name

        holder.binding.relMainLayouts.setOnClickListener {
            if (selected) {
                selected = false
                holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_bg)
            } else {
                selected = true
                holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
            }
        }
    }

    override fun getItemCount(): Int {
        return datalist!!.size
//        return if (isExpanded) datalist!!.size else datalist!!.take(3).size + 1
    }

    class ViewHolder(var binding: AdapterSearchFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}