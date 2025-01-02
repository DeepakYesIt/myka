package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.databinding.AdapterSearchRecipeBinding
import com.yesitlabs.mykaapp.model.DataModel

class SearchRecipeAdapter(
    private var datalist: List<DataModel>,
    private var requireActivity: FragmentActivity
) : RecyclerView.Adapter<SearchRecipeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterSearchRecipeBinding =
            AdapterSearchRecipeBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvRecipeName.text = datalist[position].title
        holder.binding.imgRecipe.setImageResource(datalist[position].image)

    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class ViewHolder(var binding: AdapterSearchRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}