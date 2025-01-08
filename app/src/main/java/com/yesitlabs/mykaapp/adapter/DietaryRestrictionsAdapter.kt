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

class DietaryRestrictionsAdapter(private var datalist: List<DietaryRestrictionsModelData>, private var requireActivity: FragmentActivity,
                                 private var onItemClickListener: OnItemClickedListener
): RecyclerView.Adapter<DietaryRestrictionsAdapter.ViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()
    private val dietaryId = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding:AdapterBodyGoalsBinding = AdapterBodyGoalsBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTitleName.text=datalist[position].name

        // Inside your adapter's onBindViewHolder method
        holder.binding.apply {
            // Handle selection UI based on the data model's 'selected' property
            if (datalist[position].selected) {
                imageRightTick.visibility = View.VISIBLE
                relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
                if (!dietaryId.contains(datalist[position].id.toString())) {
                    dietaryId.add(datalist[position].id.toString())
                }
                onItemClickListener.itemClicked(position, dietaryId, "2", "")

            } else {
                imageRightTick.visibility = View.GONE
                relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
                dietaryId.remove(datalist[position].id.toString())
            }

            // Handle item click
            relMainLayout.setOnClickListener {
                if (position == 0) {
                    // Handle "select all" or first item case
                    selectedPositions.clear()
                    datalist.forEach { it.selected = false } // Reset all selections
                    dietaryId.clear()
                    datalist[position].selected = true // Mark the first item as selected
                    selectedPositions.add(0)
                    dietaryId.add(datalist[position].id.toString())
                } else {
                    // Remove "select all" if another item is clicked
                    if (0 in selectedPositions) {
                        selectedPositions.remove(0)
                        datalist[0].selected = false
                        dietaryId.clear()
                    }

                    // Toggle selection state for the clicked item
                    if (position in selectedPositions) {
                        selectedPositions.remove(position)
                        datalist[position].selected = false
                        dietaryId.remove(datalist[position].id.toString())
                    } else {
                        selectedPositions.add(position)
                        datalist[position].selected = true
                        dietaryId.add(datalist[position].id.toString())
                    }
                }

                notifyDataSetChanged() // Refresh the UI
                onItemClickListener.itemClicked(position, dietaryId, "2", "")
            }
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun filterList(filteredList: MutableList<DietaryRestrictionsModelData>) {
        this.datalist = filteredList
        notifyDataSetChanged()
    }


    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root){
    }

}