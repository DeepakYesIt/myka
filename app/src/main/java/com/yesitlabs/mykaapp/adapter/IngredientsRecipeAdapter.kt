package com.yesitlabs.mykaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterIngredientsRecipeBinding
import com.yesitlabs.mykaapp.model.DataModel

class IngredientsRecipeAdapter(private var datalist: List<DataModel>, private var requireActivity: FragmentActivity): RecyclerView.Adapter<IngredientsRecipeAdapter.ViewHolder>() {

    private  var isCheckEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterIngredientsRecipeBinding = AdapterIngredientsRecipeBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitleName.text=datalist[position].title
        holder.binding.tvTitleDesc.text=datalist[position].description
        holder.binding.imgIngRecipe.setImageResource(datalist[position].image)

        if (datalist[position].type=="AddedIng"){
            holder.binding.imgCheckbox.setImageResource(R.drawable.orange_checkbox_images)
        }else {
            if (isCheckEnabled) {
                holder.binding.imgCheckbox.setImageResource(R.drawable.orange_checkbox_images)
            } else {
                holder.binding.imgCheckbox.setImageResource(R.drawable.orange_uncheck_box_images)
            }
        }

        holder.binding.imgCheckbox.setOnClickListener{
            if (datalist[position].type=="AddedIng"){
                holder.binding.imgCheckbox.setImageResource(R.drawable.orange_checkbox_images)
            }else{
                if (datalist[position].isOpen === false) {
                    datalist[position].isOpen=true
                    holder.binding.imgCheckbox.setImageResource(R.drawable.orange_checkbox_images)
                } else {
                    datalist[position].isOpen=false
                    holder.binding.imgCheckbox.setImageResource(R.drawable.orange_uncheck_box_images)
                }
            }
        }
    }

    fun setCheckEnabled(enabled: Boolean) {
        isCheckEnabled = enabled
        notifyDataSetChanged()  // Notify all items to start/stop the animation
    }
    override fun getItemCount(): Int {
        return datalist.size
    }

    fun filterList(filteredList: MutableList<DataModel>) {
        this.datalist = filteredList
        notifyDataSetChanged()
    }


    class ViewHolder(var binding: AdapterIngredientsRecipeBinding) : RecyclerView.ViewHolder(binding.root){

    }
}