package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterBodyGoalsBinding
import com.yesitlabs.mykaapp.databinding.AdapterPreferencesItemBinding
import com.yesitlabs.mykaapp.model.DataModel

class PreferencesAdapter(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity,private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<PreferencesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterPreferencesItemBinding =
            AdapterPreferencesItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvPreferencesName.text = datalist[position].title

        holder.binding.relMainLayout.setOnClickListener {
            onItemClickListener.itemClick(position,datalist[position].type,datalist[position].title)
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterPreferencesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}
