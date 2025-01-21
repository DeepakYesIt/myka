package com.mykameal.planner.adapter

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
import com.mykameal.planner.OnItemSelectListener
import com.mykameal.planner.R
import com.mykameal.planner.apiInterface.BaseUrl
import com.mykameal.planner.databinding.AdapterCookbookItemBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data

class AdapterCookBookItem(var datalist: MutableList<Data>, private var requireActivity: FragmentActivity, private var onItemSelectListener:OnItemSelectListener) : RecyclerView.Adapter<AdapterCookBookItem.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterCookbookItemBinding =
            AdapterCookbookItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.binding.textTittles.text = datalist[position].name

            if (datalist[position].status==1){
                holder.binding.cardViews.setBackgroundResource(R.drawable.cardview_border_active)
            }else{
                holder.binding.cardViews.setBackgroundResource(R.drawable.cardview_border)
            }

            if (datalist[position].id==0){
                Glide.with(requireActivity)
                    .load(datalist[position].user_id)
                    .error(R.drawable.mask_group_icon) // Fallback if the image fails to load
                    .placeholder(R.drawable.mask_group_icon) // Placeholder while loading
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("GlideError", "Image Load Failed: ${e?.message}", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d("GlideSuccess", "Image Loaded Successfully")
                            return false
                        }
                    })
                    .into(holder.binding.imageShapeable)


            }else{
                Glide.with(requireActivity)
                    .load(BaseUrl.imageBaseUrl+datalist[position].image)
                    .error(R.drawable.mask_group_icon)
                    .placeholder(R.drawable.mask_group_icon)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
//                        holder.binding.layProgess.root.visibility= View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
//                        holder.binding.layProgess.root.visibility= View.GONE
                            return false
                        }
                    })
                    .into(holder.binding.imageShapeable)
            }


            holder.binding.cardViews.setOnClickListener{
                onItemSelectListener.itemSelect(position,"","Christmas")
            }



        }catch (e:Exception){
            Log.d("@Error ","*****"+e.message)
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