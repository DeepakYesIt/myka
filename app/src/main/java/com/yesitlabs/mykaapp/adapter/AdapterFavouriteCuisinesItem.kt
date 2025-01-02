package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickedListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterBodyGoalsBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData

class AdapterFavouriteCuisinesItem(private var dietaryRestrictionsModelData: List<DietaryRestrictionsModelData>,
                                   private var requireActivity: FragmentActivity,
                                   private var onItemClickedListener: OnItemClickedListener):
    RecyclerView.Adapter<AdapterFavouriteCuisinesItem.ViewHolder>() {

    val selectedIds: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBodyGoalsBinding = AdapterBodyGoalsBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text=dietaryRestrictionsModelData[position].name

        ///handle change adapter background
        if (dietaryRestrictionsModelData[position].isOpen){
            holder.binding.imageRightTick.visibility= View.VISIBLE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
        }else{
            holder.binding.imageRightTick.visibility= View.GONE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
        }

        /// handle click event
        holder.binding.relMainLayout.setOnClickListener {
            val currentId = dietaryRestrictionsModelData[position].id
            if (dietaryRestrictionsModelData[position].isOpen) {
                dietaryRestrictionsModelData[position].isOpen = false
                selectedIds.remove(currentId.toString()) // Remove from selected list
            } else {
                dietaryRestrictionsModelData[position].isOpen = true
                selectedIds.add(currentId.toString()) // Add to selected list
            }

            // Pass the updated selectedIds list to the interface
            onItemClickedListener.itemClicked(position,selectedIds, "2", "")

            notifyDataSetChanged() // Refresh the UI
        }
    }

    override fun getItemCount(): Int {
        return dietaryRestrictionsModelData.size
    }

    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root){
    }

}