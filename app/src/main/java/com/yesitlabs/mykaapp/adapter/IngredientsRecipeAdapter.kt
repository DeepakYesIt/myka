package com.yesitlabs.mykaapp.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yesitlabs.mykaapp.OnItemSelectListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterIngredientsRecipeBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails.apiresponse.IngredientsModel
import com.yesitlabs.mykaapp.model.DataModel

class IngredientsRecipeAdapter(var datalist: MutableList<IngredientsModel>?, var requireActivity: FragmentActivity, var OnItemSelectListener: OnItemSelectListener): RecyclerView.Adapter<IngredientsRecipeAdapter.ViewHolder>() {

    private  var isCheckEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterIngredientsRecipeBinding = AdapterIngredientsRecipeBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }


    fun updateList(data: MutableList<IngredientsModel>){
        datalist=data
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data=datalist!![position]

        if (data.food!=null){
            holder.binding.tvTitleName.text = data.food
        }


        if (data.status){
            holder.binding.imgCheckbox.setImageResource(R.drawable.orange_checkbox_images)
        }else{
            holder.binding.imgCheckbox.setImageResource(R.drawable.orange_uncheck_box_images)
        }

        if (data.image!=null){
            Glide.with(requireActivity)
                .load(data.image)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.layProgess.root.visibility= View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.layProgess.root.visibility= View.GONE
                        return false
                    }
                })
                .into(holder.binding.imageData)
        }else{
            holder.binding.layProgess.root.visibility= View.GONE
        }

        if (data.quantity!=null){
            holder.binding.tvTitleDesc.text =""+data.quantity+" "+data.measure
        }

        holder.itemView.setOnClickListener {
            OnItemSelectListener.itemSelect(position,"","")
        }


        /*holder.binding.tvTitleName.text=datalist[position].title
        holder.binding.tvTitleDesc.text=datalist[position].description
        holder.binding.imgIngRecipe.setImageResource(datalist[position].image)*/

        /*if (datalist[position].type=="AddedIng"){
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
        }*/
    }

    fun setCheckEnabled(enabled: Boolean) {
        isCheckEnabled = enabled
        notifyDataSetChanged()  // Notify all items to start/stop the animation
    }
    override fun getItemCount(): Int {
        return datalist!!.size
    }

    /*fun filterList(filteredList: MutableList<DataModel>) {
        this.datalist = filteredList
        notifyDataSetChanged()
    }*/


    class ViewHolder(var binding: AdapterIngredientsRecipeBinding) : RecyclerView.ViewHolder(binding.root){

    }
}