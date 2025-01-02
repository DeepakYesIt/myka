package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterSearchFilterItemBinding
import com.yesitlabs.mykaapp.model.DataModel

class AdapterFilterSearchItem(
    private var datalist: List<DataModel>,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterFilterSearchItem.ViewHolder>() {

    private var selected: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSearchFilterItemBinding =
            AdapterSearchFilterItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvItem.text = datalist[position].title

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
        return datalist.size
    }

    class ViewHolder(var binding: AdapterSearchFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}