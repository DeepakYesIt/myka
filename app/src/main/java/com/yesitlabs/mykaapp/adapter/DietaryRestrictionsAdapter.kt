package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterBodyGoalsBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.yesitlabs.mykaapp.model.DataModel

class DietaryRestrictionsAdapter(private var datalist: List<DietaryRestrictionsModelData>, private var requireActivity: FragmentActivity, private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<DietaryRestrictionsAdapter.ViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding:AdapterBodyGoalsBinding = AdapterBodyGoalsBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTitleName.text=datalist[position].name

        if (position in selectedPositions) {
            holder.binding.imageRightTick.visibility= View.VISIBLE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
            onItemClickListener.itemClick(position,"2","")
        } else {
            holder.binding.imageRightTick.visibility= View.GONE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
        }

        holder.binding.relMainLayout.setOnClickListener {
            if (position == 0) {
                selectedPositions.clear()
                selectedPositions.add(0)
            } else {
                if (0 in selectedPositions) {
                    selectedPositions.remove(0)
                }
                if (position in selectedPositions) {
                    selectedPositions.remove(position)
                } else {
                    selectedPositions.add(position)
                }
            }
            notifyDataSetChanged()
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