package com.mykaimeal.planner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
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
                onItemClickListener.itemClick(position,datalist!![position].name,"CookTime")
            }else{
                if (selected) {
                    selected = false
                    holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
                } else {
                    selected = true
                    holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_bg)
                    onItemClickListener.itemClick(position,datalist!![position].name,"CookTime")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return datalist!!.size
    }

    fun updateList(newList: MutableList<CookTime>?) {
        datalist!!.clear()
        newList?.let { datalist!!.addAll(it) }
        notifyDataSetChanged()
    }


    class ViewHolder(var binding: AdapterSearchFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}