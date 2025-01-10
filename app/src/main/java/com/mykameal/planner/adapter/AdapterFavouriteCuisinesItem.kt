package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemClickedListener
import com.mykameal.planner.R
import com.mykameal.planner.databinding.AdapterBodyGoalsBinding
import com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData

class AdapterFavouriteCuisinesItem(private var dietaryRestrictionsModelData: List<DietaryRestrictionsModelData>,
                                   private var requireActivity: FragmentActivity,
                                   private var onItemClickedListener: OnItemClickedListener):
    RecyclerView.Adapter<AdapterFavouriteCuisinesItem.ViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>() // Track selected positions
    private val dietaryId = mutableListOf<String>() // Track selected dietary IDs

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBodyGoalsBinding = AdapterBodyGoalsBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text=dietaryRestrictionsModelData[position].name

        holder.binding.apply {
            // Update UI based on selection state
            if (dietaryRestrictionsModelData[position].selected) {
                imageRightTick.visibility = View.VISIBLE
                relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
                if (!dietaryId.contains(dietaryRestrictionsModelData[position].id.toString())) {
                    dietaryId.add(dietaryRestrictionsModelData[position].id.toString())
                }
                onItemClickedListener.itemClicked(position, dietaryId, "-1", "")
            } else {
                imageRightTick.visibility = View.GONE
                relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
                dietaryId.remove(dietaryRestrictionsModelData[position].id.toString())
            }

            // Handle click event for selection
            relMainLayout.setOnClickListener {
               /* when (position) {
                    0 -> {
                        // "Select All" logic
                        selectedPositions.clear()
                        dietaryId.clear()

                        if (!dietaryRestrictionsModelData[position].selected) {
                            // Select all
                            dietaryRestrictionsModelData.forEachIndexed { index, model ->
                                model.selected = index == 0 || !dietaryRestrictionsModelData[position].selected // Select "All" and all others
                                if (index > 0) dietaryId.add(model.id.toString())
                            }
                            selectedPositions.addAll(dietaryRestrictionsModelData.indices)
                        } else {
                            // Deselect all
                            dietaryRestrictionsModelData.forEach { it.selected = false }
                        }
                    }
                    else -> {*/
                        // Individual item logic
                        dietaryRestrictionsModelData[position].selected = !dietaryRestrictionsModelData[position].selected
                        if (dietaryRestrictionsModelData[position].selected) {
                            dietaryId.add(dietaryRestrictionsModelData[position].id.toString())
                            selectedPositions.add(position)
                            onItemClickedListener.itemClicked(position, dietaryId, "", "true")

                        } else {
                            dietaryId.remove(dietaryRestrictionsModelData[position].id.toString())
                            selectedPositions.remove(position)
                            onItemClickedListener.itemClicked(position, dietaryId, "", "false")

                        }

                       /* // Deselect "Select All" if any item is toggled
                        if (dietaryRestrictionsModelData[0].selected) {
                            dietaryRestrictionsModelData[0].selected = false
                            selectedPositions.remove(0)
                        }*/
              /*      }
                }*/

                // Notify changes to the specific item or all items
                notifyDataSetChanged()

            }
        }
    }

    override fun getItemCount(): Int {
        return dietaryRestrictionsModelData.size
    }

    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root){
    }

}