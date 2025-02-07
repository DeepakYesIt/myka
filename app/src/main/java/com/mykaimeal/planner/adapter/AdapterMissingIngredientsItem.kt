package com.mykaimeal.planner.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
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
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterIngredientsRecipeBinding
import com.mykaimeal.planner.fragment.mainfragment.hometab.missingingredientsfragment.model.MissingIngredientModelData


class AdapterMissingIngredientsItem(var datalist: MutableList<MissingIngredientModelData>?, var requireActivity: FragmentActivity, var OnItemSelectListener: OnItemSelectListener): RecyclerView.Adapter<AdapterMissingIngredientsItem.ViewHolder>() {

    private  var isCheckEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterIngredientsRecipeBinding = AdapterIngredientsRecipeBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val data=datalist!![position]

            if (data.food!=null){
                holder.binding.tvTitleName.text=data.food.toString()
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
                if (!data.measure.equals("<unit>")){
                    holder.binding.tvTitleDesc.text =""+data.quantity+" "+data.measure
                }else{
                    holder.binding.tvTitleDesc.text =""+data.quantity
                }
            }

            holder.binding.imgCheckbox.setOnClickListener{
                OnItemSelectListener.itemSelect(position,"","")

               /* if (data.status){
                    OnItemSelectListener.itemSelect(position,"","")
                }*/
            }

            /*holder.itemView.setOnClickListener {
                OnItemSelectListener.itemSelect(position,"","")
            }*/


        }catch (e:Exception){
            Log.d("@@@@ ","Error ******"+e.message.toString())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCheckEnabled(enabled: Boolean) {
        isCheckEnabled = enabled
        notifyDataSetChanged()  // Notify all items to start/stop the animation
    }
    override fun getItemCount(): Int {
        return datalist!!.size
    }

    fun updateList(recipeData: MutableList<MissingIngredientModelData>) {
        this.datalist=recipeData
        notifyDataSetChanged()

    }

    class ViewHolder(var binding: AdapterIngredientsRecipeBinding) : RecyclerView.ViewHolder(binding.root){

    }
}