package com.mykameal.planner.adapter

import android.annotation.SuppressLint
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
    var mealRoutineModelData: List<MealRoutineModelData>,
    var requireActivity: FragmentActivity,
    var onItemClickedListener: OnItemClickedListener
) : RecyclerView.Adapter<MealRoutineAdapter.ViewHolder>() {

    private var selectedIds = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBodyGoalsBinding =
            AdapterBodyGoalsBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text = mealRoutineModelData[position].name

        holder.binding.apply {
            // Handle background and tick visibility based on the 'selected' property
            if (mealRoutineModelData[position].selected) {
                imageRightTick.visibility = View.VISIBLE
                relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
            } else {
                imageRightTick.visibility = View.GONE
                relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
            }

            // Handle click events
            relMainLayout.setOnClickListener {
                if (position == 0) { // "Select All" logic
                    if (mealRoutineModelData[position].selected) {
                        // Deselect "Select All" and all other items
                        mealRoutineModelData.forEach { it.selected = false }
                        onItemClickedListener.itemClicked(position, null, "-1", "false")
                    } else {
                        // Select "Select All" and all other items
                        mealRoutineModelData.forEach { it.selected = true }
                        selectedIds = mealRoutineModelData.map { it.id.toString() }.toMutableList()
                        onItemClickedListener.itemClicked(position, selectedIds, "-1", "true")
                    }
                } else {
                    // Toggle selection for individual items
                    mealRoutineModelData[position].selected = !mealRoutineModelData[position].selected

                    // Deselect "Select All" if any individual item is deselected
                    if (!mealRoutineModelData[position].selected) {
                        mealRoutineModelData[0].selected = false
                    }

                    // Select "Select All" if all items (except the "Select All" button) are selected
                    val areAllItemsSelected = mealRoutineModelData.drop(1).all { it.selected }
                    mealRoutineModelData[0].selected = areAllItemsSelected

                    // Pass the updated selected IDs to the listener
                    selectedIds = mealRoutineModelData.filter { it.selected }.map { it.id.toString() }.toMutableList()
                    onItemClickedListener.itemClicked(position, selectedIds, "", if (mealRoutineModelData[position].selected) "true" else "false"
                    )
                }

                notifyDataSetChanged() // Refresh the UI
            }
        }


        /*   /// Handle background and tick visibility
           if (selectedIds.contains(mealRoutineModelData[position].id.toString())) {
               holder.binding.imageRightTick.visibility = View.VISIBLE
               holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
               onItemClickedListener.itemClicked(position, selectedIds, "-1", "")
           } else {
               holder.binding.imageRightTick.visibility = View.GONE
               holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
           }

            /// Handle click events
           holder.binding.relMainLayout.setOnClickListener {
               val currentId = mealRoutineModelData[position].id.toString()

               if (position == 0) { // "Select All" logic
                   if (selectedIds.size == mealRoutineModelData.size) {
                       // If all items are selected, clear all selections
                       selectedIds.clear()
                   } else {
                       // Select all items
                       selectedIds.clear()
                       selectedIds.addAll(mealRoutineModelData.map { it.id.toString() })
                   }
               } else {
                   // Toggle selection for individual items
                   if (selectedIds.contains(currentId)) {
                       selectedIds.remove(currentId)

                       // If an item is deselected, deselect "Select All" as well
                       if (selectedIds.contains(mealRoutineModelData[0].id.toString())) {
                           selectedIds.remove(mealRoutineModelData[0].id.toString())
                       }
                       onItemClickedListener.itemClicked(position, selectedIds, "", "false")
                   } else {
                       selectedIds.add(currentId)
                       onItemClickedListener.itemClicked(position, selectedIds, "", "true")

                   }

                   // If all items (except the "Select All" button) are selected, mark "Select All" as selected
                   if (selectedIds.size == mealRoutineModelData.size - 1 && !selectedIds.contains(mealRoutineModelData[0].id.toString())) {
                       selectedIds.add(mealRoutineModelData[0].id.toString())
                       onItemClickedListener.itemClicked(position, selectedIds, "", "true")
                   }
               }

               notifyDataSetChanged() // Refresh the UI
           }*/




        /*  /// Handle background and tick visibility
          if (selectedIds.contains(mealRoutineModelData[position].id.toString())) {
              holder.binding.imageRightTick.visibility = View.VISIBLE
              holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
              onItemClickedListener.itemClicked(position, selectedIds, "-1", "")
          } else {
              holder.binding.imageRightTick.visibility = View.GONE
              holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
          }

          /// Handle click events
          holder.binding.relMainLayout.setOnClickListener {
              val currentId = mealRoutineModelData[position].id.toString()
              if (position == 0) {
                  if (selectedIds.size == mealRoutineModelData.size) {
                      // Clear all selections
                      selectedIds.clear()
                  } else {
                      // Select all items
                      selectedIds.clear()
                      selectedIds.addAll(mealRoutineModelData.map { it.id.toString() })
                  }
              } else {
                  if (selectedIds.contains(currentId)) {
                      selectedIds.remove(currentId)
                      onItemClickedListener.itemClicked(position, selectedIds, "", "false")
                  } else {
                      selectedIds.add(currentId)
                      onItemClickedListener.itemClicked(position, selectedIds, "", "true")

                  }
              }

              // Pass the updated list of selected IDs to the interface

              notifyDataSetChanged() // Refresh the UI
          }*/
    }

    override fun getItemCount(): Int {
        return mealRoutineModelData.size
    }


    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}