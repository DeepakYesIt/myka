package com.mykameal.planner.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.R
import com.mykameal.planner.databinding.AdapterCookInstructionsItemBinding
import com.mykameal.planner.databinding.AdapterCreateIngredientsItemBinding
import com.mykameal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.RecyclerViewCookIngModel
import com.mykameal.planner.fragment.mainfragment.addrecipetab.createrecipefromimage.model.RecyclerViewItemModel

class AdapterCookIngredientsItem(private var datalist: MutableList<RecyclerViewCookIngModel>, private var requireActivity: FragmentActivity,
                                 private var cookIngName: CookIngName
) : RecyclerView.Adapter<AdapterCookIngredientsItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterCookInstructionsItemBinding =
            AdapterCookInstructionsItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val item = datalist[position]

        holder.binding.tvInstructions.text="Step-"+item.count.toString()

        if (item.description!=null){
            holder.binding.etAddIngredients.setText(item.description.toString())
            updateBackground(holder.binding.llLayouts, item.description)
        }else{
            holder.binding.etAddIngredients.setText("")
            updateBackground(holder.binding.llLayouts, "")
        }


        // Update the model when quantity changes
        holder.binding.etAddIngredients.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.description = s.toString()
                updateBackground(holder.binding.llLayouts, s.toString())
                cookIngName.cookIngName(s.toString(), position, holder.binding.imgCross)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.binding.imgCross.setOnClickListener{
            removeStep(position)
        }
    }

    private fun updateBackground(llLayouts: LinearLayout, text: String) {
        if (text.isNotEmpty()) {
            llLayouts.setBackgroundResource(R.drawable.create_select_bg) // Change this drawable
        } else {
            llLayouts.setBackgroundResource(R.drawable.create_unselect_bg)  // Default background
        }
    }

    // Function to check if all EditTexts are filled
    fun isAllIngFieldFilled(): Boolean {
        return datalist.all { it.description.isNotEmpty() }
    }

    // Function to remove a step and update step numbers
    private fun removeStep(position: Int) {
        if (position == 0) return // Prevent deleting Step-1
        datalist.removeAt(position)
        notifyItemRemoved(position)
        updateStepNumbers()
    }

    // Function to update step numbers dynamically
    private fun updateStepNumbers() {
        for (i in datalist.indices) {
            datalist[i].count = i + 1 // Reset step numbers sequentially
        }
        notifyDataSetChanged()
    }

    fun highlightEmptyIngFields(recyclerView: RecyclerView) {
        for (i in datalist.indices) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? ViewHolder
            holder?.binding!!.etAddIngredients.let { editText ->
                if (datalist[i].description.trim().isEmpty()) {
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun addNewItem() {
        val newStep = RecyclerViewCookIngModel(datalist.size + 1) // Next step number
        datalist.add(newStep)
        notifyItemInserted(datalist.size - 1)
    }

    fun update(recyclerViewCookIngModels: MutableList<RecyclerViewCookIngModel>) {
        this.datalist= recyclerViewCookIngModels
        notifyDataSetChanged()

    }

    class ViewHolder(var binding: AdapterCookInstructionsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface CookIngName {
        // all are the abstract methods.
        fun cookIngName(description: String?, pos: Int, cross: ImageView?)
        fun crossCookIngName(cross: ImageView?, textView: TextView?, pos: Int)
    }
}