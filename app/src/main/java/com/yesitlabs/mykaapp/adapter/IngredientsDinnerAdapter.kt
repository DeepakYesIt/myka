package com.yesitlabs.mykaapp.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.OnItemLongClickListener
import com.yesitlabs.mykaapp.databinding.AdapterIngredientsItemBinding
import com.yesitlabs.mykaapp.model.DataModel

class IngredientsDinnerAdapter(
    private var datalist: List<DataModel>,
    private var requireActivity: FragmentActivity,
    private var onItemClickListener: OnItemClickListener,
    private var onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<IngredientsDinnerAdapter.ViewHolder>() {

    private var checkStatus: String? = null
    private var checkTypeStatus: String? = null
    private var ziggleAnimation: ObjectAnimator? = null
    private var isZiggleEnabled = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterIngredientsItemBinding =
            AdapterIngredientsItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvBreakfast.text=datalist[position].title
        holder.binding.relBreakfast.setBackgroundResource(datalist[position].image)

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

        if (datalist[position].isOpen){
            holder.binding.missingIngredientsImg.visibility=View.VISIBLE
            holder.binding.checkBoxImg.visibility=View.GONE
        }else{
            holder.binding.missingIngredientsImg.visibility=View.GONE
            holder.binding.checkBoxImg.visibility=View.VISIBLE
        }

        holder.binding.missingIngredientsImg.setOnClickListener{
            checkTypeStatus="missingIng"
            onItemClickListener.itemClick(position, checkStatus, checkTypeStatus)
        }

        holder.binding.imgHeartRed.setOnClickListener{
            checkTypeStatus="heart"
            onItemClickListener.itemClick(position, checkStatus, checkTypeStatus)
        }

        holder.binding.relMainLayouts.setOnClickListener{
            checkTypeStatus="recipeDetails"
            onItemClickListener.itemClick(position, checkStatus, checkTypeStatus)
        }

        holder.binding.imageMinus.setOnClickListener {
            checkTypeStatus="minus"
            if (datalist[position].isOpen) {
                checkStatus = "1"
            } else {
                checkStatus = "0"
            }
            onItemClickListener.itemClick(position, checkStatus,checkTypeStatus)
        }

        holder.itemView.setOnLongClickListener{
            onItemLongClickListener.itemLongClick(position, checkStatus, datalist[position].type)
            true
        }
    }

    fun setZiggleEnabled(enabled: Boolean) {
        isZiggleEnabled = enabled
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    class ViewHolder(var binding: AdapterIngredientsItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    private fun stopZiggle(view: View) {
        ziggleAnimation?.cancel()
        ziggleAnimation = null
        view.rotation = 0f
        isZiggleEnabled = false
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

}