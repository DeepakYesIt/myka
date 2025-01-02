package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.OnItemClickedListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterBodyGoalsBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.yesitlabs.mykaapp.model.DataModel

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


        // Handle selection UI and ID addition
        if (position in selectedPositions) {
            if (!dietaryId.contains(datalist[position].id.toString())) {
                dietaryId.add(datalist[position].id.toString())
            }
            holder.binding.imageRightTick.visibility = View.VISIBLE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
        } else {
            dietaryId.remove(datalist[position].id.toString())
            holder.binding.imageRightTick.visibility = View.GONE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
        }

        // Handle item click
        holder.binding.relMainLayout.setOnClickListener {
            if (position == 0) {
                selectedPositions.clear()
                selectedPositions.add(0)
                dietaryId.clear()
                dietaryId.add(datalist[position].id.toString())
            } else {
                if (0 in selectedPositions) {
                    selectedPositions.remove(0)
                    dietaryId.clear()
                }
                if (position in selectedPositions) {
                    selectedPositions.remove(position)
                    dietaryId.remove(datalist[position].id.toString())
                } else {
                    selectedPositions.add(position)
                    dietaryId.add(datalist[position].id.toString())
                }
            }
            notifyDataSetChanged()
            onItemClickListener.itemClicked(position, dietaryId, "2", "")
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