package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.databinding.AdapterPreferencesItemBinding
import com.yesitlabs.mykaapp.databinding.AdapterPrepareCookItemBinding
import com.yesitlabs.mykaapp.model.DataModel

class AdapterPrepareCookItem(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity): RecyclerView.Adapter<AdapterPrepareCookItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterPrepareCookItemBinding =
            AdapterPrepareCookItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text = datalist[position].title
        holder.binding.tvTitleDesc.text = datalist[position].description
        holder.binding.imgIngRecipe.setImageResource(datalist[position].image)

    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterPrepareCookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}