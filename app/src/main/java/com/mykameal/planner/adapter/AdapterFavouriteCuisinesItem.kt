package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemClickedListener
import com.mykameal.planner.R
import com.mykameal.planner.databinding.AdapterBodyGoalsBinding
import com.mykameal.planner.fragment.commonfragmentscreen.favouriteCuisines.model.FavouriteCuisinesModelData

class AdapterFavouriteCuisinesItem(private var favouriteCuisineModelData: List<FavouriteCuisinesModelData>,
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

        holder.binding.tvTitleName.text=favouriteCuisineModelData[position].name

        // Inside your adapter's onBindViewHolder method
        holder.binding.apply {
            // Update UI based on the data model's 'selected' property
            if (favouriteCuisineModelData[position].selected) {
                imageRightTick.visibility = View.VISIBLE
                relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
                if (!dietaryId.contains(favouriteCuisineModelData[position].id.toString())) {
                    dietaryId.add(favouriteCuisineModelData[position].id.toString())
                }
                onItemClickedListener.itemClicked(position, dietaryId, "-1", "")
            } else {
                imageRightTick.visibility = View.GONE
                relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
                dietaryId.remove(favouriteCuisineModelData[position].id.toString())
            }

            // Handle item click
            relMainLayout.setOnClickListener {
                if (position == 0) {
                    // Handle "None" (first item) case
                    if (favouriteCuisineModelData[position].selected) {
                        // Deselect "None"
                        favouriteCuisineModelData[position].selected = false
                        selectedPositions.remove(0)
                        dietaryId.clear()
                        onItemClickedListener.itemClicked(position, dietaryId, "2", "false")
                    } else {
                        // Select "None" and clear all other selections
                        selectedPositions.clear()
                        favouriteCuisineModelData.forEach { it.selected = false }
                        dietaryId.clear()

                        favouriteCuisineModelData[position].selected = true
                        selectedPositions.add(0)
                        dietaryId.add(favouriteCuisineModelData[position].id.toString())
                        onItemClickedListener.itemClicked(position, dietaryId, "2", "true")
                    }
                } else {
                    // Deselect "select all" if another item is clicked
                    if (selectedPositions.contains(0)) {
                        selectedPositions.remove(0)
                        favouriteCuisineModelData[0].selected = false
                        dietaryId.clear()
                    }

                    // Toggle the current item's selection state
                    if (favouriteCuisineModelData[position].selected) {
                        // Deselect the item
                        favouriteCuisineModelData[position].selected = false
                        selectedPositions.remove(position)
                        dietaryId.remove(favouriteCuisineModelData[position].id.toString())
                        onItemClickedListener.itemClicked(position, dietaryId, "2", "false")
                    } else {
                        // Select the item
                        favouriteCuisineModelData[position].selected = true
                        selectedPositions.add(position)
                        dietaryId.add(favouriteCuisineModelData[position].id.toString())
                        onItemClickedListener.itemClicked(position, dietaryId, "2", "true")
                    }
                }
                notifyDataSetChanged() // Refresh the UI
            }
        }

        /*holder.binding.apply {
            // Update UI based on selection state
            if (favouriteCuisineModelData[position].selected) {
                imageRightTick.visibility = View.VISIBLE
                relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
                if (!dietaryId.contains(favouriteCuisineModelData[position].id.toString())) {
                    dietaryId.add(favouriteCuisineModelData[position].id.toString())
                }
                onItemClickedListener.itemClicked(position, dietaryId, "-1", "")
            } else {
                imageRightTick.visibility = View.GONE
                relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
                dietaryId.remove(favouriteCuisineModelData[position].id.toString())
            }

            // Handle click event for selection
            relMainLayout.setOnClickListener {
                when (position) {
                    0 -> {
                        // "Select All" logic
                        selectedPositions.clear()
                        dietaryId.clear()

                        if (!favouriteCuisineModelData[position].selected) {
                            // Select all
                            favouriteCuisineModelData.forEachIndexed { index, model ->
                                model.selected = index == 0 || !favouriteCuisineModelData[position].selected // Select "All" and all others
                                if (index > 0) dietaryId.add(model.id.toString())
                            }
                            selectedPositions.addAll(favouriteCuisineModelData.indices)
                        } else {
                            // Deselect all
                            favouriteCuisineModelData.forEach { it.selected = false }
                        }
                    }
                    else -> {
                        // Individual item logic
                        favouriteCuisineModelData[position].selected = !favouriteCuisineModelData[position].selected
                        if (favouriteCuisineModelData[position].selected) {
                            dietaryId.add(favouriteCuisineModelData[position].id.toString())
                            selectedPositions.add(position)
                            onItemClickedListener.itemClicked(position, dietaryId, "", "true")

                        } else {
                            dietaryId.remove(favouriteCuisineModelData[position].id.toString())
                            selectedPositions.remove(position)
                            onItemClickedListener.itemClicked(position, dietaryId, "", "false")

                        }
                        // Deselect "Select All" if any item is toggled
                        if (favouriteCuisineModelData[0].selected) {
                            favouriteCuisineModelData[0].selected = false
                            selectedPositions.remove(0)
                        }
                    }
                }

                // Notify changes to the specific item or all items
                notifyDataSetChanged()

            }
        }*/
    }

    override fun getItemCount(): Int {
        return favouriteCuisineModelData.size
    }

    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root){
    }

}