package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterBodyGoalsBinding
import com.mykaimeal.planner.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData


class BodyGoalAdapter(private var datalist: List<BodyGoalModelData>, private var requireActivity: FragmentActivity, private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<BodyGoalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBodyGoalsBinding = AdapterBodyGoalsBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text = datalist[position].name

        // Bind UI based on whether the item is selected or matches the last checked position
        if (datalist[position].selected) {
            holder.binding.imageRightTick.visibility = View.VISIBLE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
            onItemClickListener.itemClick(datalist[position].id,"-1","")
        } else {
            holder.binding.imageRightTick.visibility = View.GONE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
        }

        // Handle click event for the item
        holder.binding.relMainLayout.setOnClickListener {
            // Check if the clicked item is already selected
            if (datalist[position].selected) {
                // Deselect all items
                datalist.forEach { it.selected = false }
                onItemClickListener.itemClick(datalist[position].id, position.toString(), "false")
                notifyDataSetChanged()
            } else {
                // Deselect all items
                datalist.forEach { it.selected = false }
                onItemClickListener.itemClick(datalist[position].id, position.toString(), "true")
                // Select the new item
                datalist[position].selected = true
                // Notify the adapter about the changes
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root){

    }

}