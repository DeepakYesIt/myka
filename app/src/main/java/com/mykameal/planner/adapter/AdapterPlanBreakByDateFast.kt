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
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.OnItemSelectPlanTypeListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.AdapterMealTypeHorizentalBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate.BreakfastModelPlanByDate

class AdapterPlanBreakByDateFast(var datalist: MutableList<BreakfastModelPlanByDate>?, var requireActivity: FragmentActivity, private var onItemClickListener: OnItemSelectPlanTypeListener, var type:String): RecyclerView.Adapter<AdapterPlanBreakByDateFast.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterMealTypeHorizentalBinding = AdapterMealTypeHorizentalBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    fun updateList(updateList: MutableList<BreakfastModelPlanByDate>,type:String){
        datalist=updateList
        this.type=type
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data= datalist?.get(position)

        if (data?.recipe?.images?.SMALL?.url != null) {
            Glide.with(requireActivity)
                .load(data.recipe.images.SMALL.url)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.binding.layProgess.root.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder.binding.layProgess.root.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.binding.imageData)
        } else {
            holder.binding.layProgess.root.visibility = View.GONE
        }

        if (data?.recipe?.label!=null){
            holder.binding.tvBread.text = data.recipe.label
        }

        if (data?.recipe?.totalTime!=null){
            holder.binding.tvTotaltime.text =""+ data.recipe.totalTime +" min"
        }

        if (data?.recipe?.calories != null) {
            holder.binding.tvCalories.text = "" + data.recipe.calories.toInt()
        }

        if (data?.recipe?.totalNutrients?.FAT?.quantity != null) {
            holder.binding.tvFat.text = "" + data.recipe.totalNutrients.FAT.quantity.toInt()
        }

        if (data?.recipe?.totalNutrients?.PROCNT?.quantity != null) {
            holder.binding.tvProtein.text = "" + data.recipe.totalNutrients.PROCNT.quantity.toInt()
        }

        if (data?.recipe?.totalNutrients?.CHOCDF?.quantity != null) {
            holder.binding.tvCarbs.text = "" + data.recipe.totalNutrients.CHOCDF.quantity.toInt()
        }


        holder.binding.tvSwap.setOnClickListener {
            onItemClickListener.itemSelectPlayByDate(position,"1",type)
        }


    }


    override fun getItemCount(): Int {
        return datalist!!.size
    }


    class ViewHolder(var binding: AdapterMealTypeHorizentalBinding) : RecyclerView.ViewHolder(binding.root){

    }
}