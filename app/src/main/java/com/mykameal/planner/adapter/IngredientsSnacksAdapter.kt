package com.mykameal.planner.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipDescription
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
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.OnItemLongClickListener
import com.mykameal.planner.OnItemSelectUnSelectListener
import com.mykameal.planner.R
import com.mykameal.planner.databinding.AdapterIngredientsItemBinding
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.Breakfast
import java.util.Collections

class IngredientsSnacksAdapter(var datalist:MutableList<Breakfast>?, private var requireActivity: FragmentActivity,
                               private var onItemClickListener: OnItemSelectUnSelectListener, private var onItemLongClickListener: OnItemLongClickListener, var type:String): RecyclerView.Adapter<IngredientsSnacksAdapter.ViewHolder>() {

    private var checkStatus:String?=null
    private var checkTypeStatus: String? = null
    private var ziggleAnimation: ObjectAnimator? = null
    private  var isZiggleEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterIngredientsItemBinding = AdapterIngredientsItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(datalist, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(datalist, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item= datalist?.get(position)

        if (item?.recipe?.label!=null){
            holder.binding.tvBreakfast.text = item.recipe.label
        }

        if (item?.recipe?.totalTime!=null){
            holder.binding.tvTime.text = ""+ item.recipe.totalTime +" min "
        }

        if (item?.is_like!=null){
            if (item.is_like ==0 ){
                holder.binding.imgHeartRed.setImageResource(R.drawable.heart_white_icon)
            }else{
                holder.binding.imgHeartRed.setImageResource(R.drawable.heart_red_icon)
            }
        }

        if (item?.recipe?.images?.SMALL?.url != null) {
            Glide.with(requireActivity)
                .load(item.recipe.images.SMALL.url)
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

        if (isZiggleEnabled) {
            holder.binding.imageMinus.visibility= View.VISIBLE
            holder.binding.relWatchTimer.visibility= View.GONE
            holder.binding.imgHeartRed.visibility= View.GONE
            startZiggleAnimation(holder)
        }else{
            holder.binding.imageMinus.visibility= View.GONE
            holder.binding.relWatchTimer.visibility= View.VISIBLE
            holder.binding.imgHeartRed.visibility= View.VISIBLE
            stopZiggle(holder.itemView)
        }

        /*    if (datalist[position].isOpen){
                holder.binding.missingIngredientsImg.visibility=View.VISIBLE
                holder.binding.checkBoxImg.visibility=View.GONE
            }else{
                holder.binding.missingIngredientsImg.visibility=View.GONE
                holder.binding.checkBoxImg.visibility=View.VISIBLE
            }*/

        holder.binding.missingIngredientsImg.setOnClickListener{
            checkTypeStatus="missingIng"
            onItemClickListener.itemSelectUnSelect(position,checkTypeStatus,"Snacks",position)
        }

        holder.binding.imgHeartRed.setOnClickListener{
            checkTypeStatus="heart"
            onItemClickListener.itemSelectUnSelect(position,checkTypeStatus,"Snacks",position)
        }

        holder.binding.relMainLayouts.setOnClickListener{
            checkTypeStatus="recipeDetails"
            onItemClickListener.itemSelectUnSelect(position,checkTypeStatus,"Snacks",position)
        }

         holder.binding.imageMinus.setOnClickListener {
             checkTypeStatus="minus"
           /*  if (datalist[position].isOpen) {
                 checkStatus = "1"
             } else {
                 checkStatus = "0"
             }*/
             onItemClickListener.itemSelectUnSelect(datalist?.get(position)!!.id,checkTypeStatus,"Snacks",position)

         }

        holder.itemView.setOnLongClickListener{
            val clipData = ClipData(
                item?.recipe?.label, // Use the title as the drag data
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                ClipData.Item(item?.recipe?.label)
            )

            val shadowBuilder = View.DragShadowBuilder(holder.itemView)
            holder.itemView.startDragAndDrop(clipData, shadowBuilder, null, 0)
            onItemLongClickListener.itemLongClick(position, checkStatus, type)
            true
            /*onItemLongClickListener.itemLongClick(position, checkStatus, datalist[position].type)
            true*/
        }
    }

    fun setZiggleEnabled(enabled: Boolean) {
        isZiggleEnabled = enabled
        notifyDataSetChanged()  // Notify all items to start/stop the animation
    }

    override fun getItemCount(): Int {
        return datalist!!.size
    }

    class ViewHolder(var binding: AdapterIngredientsItemBinding) : RecyclerView.ViewHolder(binding.root){
    }

    private fun startZiggleAnimation(holder: ViewHolder) {
        val startAngle = -5f // -2 degrees
        val stopAngle = 5f   // 2 degrees

        ziggleAnimation = ObjectAnimator.ofFloat(holder.itemView, "rotation", startAngle, stopAngle)
        ziggleAnimation!!.duration = 80
        ziggleAnimation!!.repeatMode = ValueAnimator.REVERSE
        ziggleAnimation!!.repeatCount = ValueAnimator.INFINITE
        ziggleAnimation!!.start()
    }

    private fun stopZiggle(view: View) {
        ziggleAnimation?.cancel()
        ziggleAnimation = null
        view.rotation = 0f
        isZiggleEnabled = false
    }

    fun updateList(mealList: MutableList<Breakfast>) {
        this.datalist=mealList
        notifyDataSetChanged()

    }

    fun removeItem(position: Int) {
        datalist?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, datalist!!.size) // Optional, updates the positions of remaining items

    }

}