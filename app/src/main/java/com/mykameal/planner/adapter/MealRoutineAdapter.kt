package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemClickedListener
import com.mykameal.planner.R
import com.mykameal.planner.databinding.AdapterBodyGoalsBinding
import com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData

class MealRoutineAdapter(
    private var mealRoutineModelData: List<MealRoutineModelData>,
    private var requireActivity: FragmentActivity,
    private var onItemClickedListener: OnItemClickedListener
) : RecyclerView.Adapter<MealRoutineAdapter.ViewHolder>() {

    private val selectedIds = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBodyGoalsBinding =
            AdapterBodyGoalsBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text = mealRoutineModelData[position].name

        /// Handle background and tick visibility
        if (selectedIds.contains(mealRoutineModelData[position].id.toString())) {
            holder.binding.imageRightTick.visibility = View.VISIBLE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
            onItemClickedListener.itemClicked(position, selectedIds,"-1","")
        } else {
            holder.binding.imageRightTick.visibility = View.GONE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
        }

        /// Handle click events
        holder.binding.relMainLayout.setOnClickListener {
            val currentId = mealRoutineModelData[position].id.toString()
           /* if (position == 0) {
                if (selectedIds.size == mealRoutineModelData.size) {
                    // Clear all selections
                    selectedIds.clear()
                } else {
                    // Select all items
                    selectedIds.clear()
                    selectedIds.addAll(mealRoutineModelData.map { it.id.toString() })
                }
            } else {*/
                if (selectedIds.contains(currentId)) {
                    selectedIds.remove(currentId)
                    onItemClickedListener.itemClicked(position, selectedIds,"","false")
                } else {
                    selectedIds.add(currentId)
                    onItemClickedListener.itemClicked(position, selectedIds,"","true")

                }
            /*}*/

            // Pass the updated list of selected IDs to the interface

            notifyDataSetChanged() // Refresh the UI
        }
    }

    override fun getItemCount(): Int {
        return mealRoutineModelData.size
    }


    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}