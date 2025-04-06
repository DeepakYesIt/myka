package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterAllIngredientsCategoryBinding

class AllIngredientsCategoryItem(
    private var datalist: MutableList<String>?,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AllIngredientsCategoryItem.ViewHolder>() {

    private var selected: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterAllIngredientsCategoryBinding =
            AdapterAllIngredientsCategoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textFruits.text = datalist!![position]

     /*   if (datalist!![0].selected==true){
            holder.binding.tvItem.setTextColor(android.graphics.Color.parseColor("#06C169"))
            holder.binding.relMainLayouts.background=null
        }else{
            holder.binding.tvItem.setTextColor(android.graphics.Color.parseColor("#000000"))
            holder.binding.relMainLayouts.setBackgroundResource(R.drawable.month_year_unselected_bg)
        }*/

   /*     holder.binding.llMain.setOnClickListener {
                if (selected) {
                    selected = false
                    holder.binding.textFruits.setBackgroundResource(R.drawable.unselect_bg)
                } else {
                    selected = true
                    holder.binding.textFruits.setBackgroundResource(R.drawable.select_bg)
                    onItemClickListener.itemClick(position,datalist!![position],"Category")
                }
        }*/
    }

    override fun getItemCount(): Int {
         return datalist!!.size
    }

    class ViewHolder(var binding: AdapterAllIngredientsCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}