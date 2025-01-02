package com.yesitlabs.mykaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.databinding.ImageSliderItemBinding

class ImageViewPagerAdapter(private val context: Context, private var imageList: MutableList<Int>) :
    RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageSliderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.imageView.setImageResource(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageViewHolder(var binding : ImageSliderItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }


    fun updateItem(list : MutableList<Int>){
        this.imageList = list
        notifyDataSetChanged()

    }
}
