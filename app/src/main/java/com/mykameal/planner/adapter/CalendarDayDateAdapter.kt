package com.yesitlabs.mykaapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.databinding.CalendarItemDayBinding
import com.yesitlabs.mykaapp.model.DateModel

class CalendarDayDateAdapter(val days: MutableList<DateModel>, private val onDaySelected: (String) -> Unit) : RecyclerView.Adapter<CalendarDayDateAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: CalendarItemDayBinding = CalendarItemDayBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val day = days[position]

        holder.binding.tvDayName.text = BaseApplication.getFirstLetterOfDay(day.day)
        holder.binding.tvDayDate.text = BaseApplication.formatOnlyDate(day.date)

        if (position == selectedPosition) {
            holder.binding.llMainLayouts.setBackgroundResource(R.drawable.calendar_select_bg)
            holder.binding.tvDayName.setTextColor(Color.parseColor("#ffffff"))
            holder.binding.tvDayDate.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.binding.llMainLayouts.setBackgroundResource(R.drawable.calendar_unselect_bg)
            holder.binding.tvDayName.setTextColor(Color.parseColor("#999999"))
            holder.binding.tvDayDate.setTextColor(Color.parseColor("#3C4541"))
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onDaySelected(day.date)
        }



    }

    override fun getItemCount(): Int {
        return days.size
    }


    class ViewHolder(var binding: CalendarItemDayBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}