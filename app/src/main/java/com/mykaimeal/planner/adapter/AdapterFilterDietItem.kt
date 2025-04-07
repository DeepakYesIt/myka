package com.mykaimeal.planner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterSearchFilterItemBinding
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.Diet

class AdapterFilterDietItem(
    private var datalist: MutableList<Diet>?,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterFilterDietItem.ViewHolder>() {

    private var selected: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSearchFilterItemBinding =
            AdapterSearchFilterItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvItem.text = datalist!![position].name

        if (datalist!![position].selected==true){
            holder.binding.tvItem.setTextColor(android.graphics.Color.parseColor("#06C169"))
            holder.binding.relMainLayouts.background=null
        }else{
            holder.binding.tvItem.setTextColor(android.graphics.Color.parseColor("#000000"))
            holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
        }

        holder.binding.relMainLayouts.setOnClickListener {
            if (datalist!![position].selected==true){
                onItemClickListener.itemClick(position,datalist!![position].name,"Diet")
            }else{
                if (selected) {
                    selected = false
                    holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
                } else {
                    selected = true
                    holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_bg)
                    onItemClickListener.itemClick(position,datalist!![position].name,"Diet")
                }
            }
        }
    }

    override fun getItemCount(): Int {

        return datalist!!.size

//        return if (isExpanded) datalist!!.size else datalist!!.take(5).size + 1
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