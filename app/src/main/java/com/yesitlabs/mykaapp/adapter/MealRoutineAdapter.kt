package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterBodyGoalsBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData

class MealRoutineAdapter(
    private var mealRoutineModelData: List<MealRoutineModelData>,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MealRoutineAdapter.ViewHolder>() {

    private var selectedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterBodyGoalsBinding =
            AdapterBodyGoalsBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text = mealRoutineModelData[position].name

        if (selectedPositions.contains(position)) {
            holder.binding.imageRightTick.visibility = View.VISIBLE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
            onItemClickListener.itemClick(position, "2", "")
        } else {
            holder.binding.imageRightTick.visibility = View.GONE
            holder.binding.relMainLayout.setBackgroundResource(R.drawable.gray_box_border_bg)
        }

        holder.binding.relMainLayout.setOnClickListener {
            if (position == 0) {
                if (selectedPositions.size == mealRoutineModelData.size) {
                    selectedPositions.clear()
                } else {
                    selectedPositions = (0 until mealRoutineModelData.size).toMutableSet()
                    holder.binding.imageRightTick.visibility = View.VISIBLE
                    holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
                    onItemClickListener.itemClick(position, "2", "")
                }
            } else {
                selectedPositions.clear()
                selectedPositions.add(position)
                holder.binding.imageRightTick.visibility = View.VISIBLE
                holder.binding.relMainLayout.setBackgroundResource(R.drawable.orange_box_bg)
                onItemClickListener.itemClick(position, "2", "")
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mealRoutineModelData.size
    }


    class ViewHolder(var binding: AdapterBodyGoalsBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}