package com.mykameal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemSelectListener
import com.mykameal.planner.databinding.AdapterCookbookItemBinding
import com.mykameal.planner.model.DataModel

class AdapterCookBookItem(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity,private var onItemSelectListener:OnItemSelectListener) : RecyclerView.Adapter<AdapterCookBookItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterCookbookItemBinding =
            AdapterCookbookItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textTittles.text = datalist[position].title
        holder.binding.imageShapeable.setImageResource(datalist[position].image)

        holder.binding.cardViews.setOnClickListener{
            onItemSelectListener.itemSelect(position,"","Christmas")
        }

//        holder.binding.cardViews.setOnClickListener{
//            holder.binding.cardViews.setBackgroundResource(R.drawable.outline_green_box_bg)
//        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterCookbookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}