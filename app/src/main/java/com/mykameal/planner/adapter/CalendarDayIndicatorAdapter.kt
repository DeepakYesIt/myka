package com.mykameal.planner.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.R
import com.mykameal.planner.databinding.CalendarItemIndicatorDayBinding
import com.mykameal.planner.model.CalendarDataModel

class CalendarDayIndicatorAdapter(private val days: List<CalendarDataModel.Day>, private val onDaySelected: (CalendarDataModel.Day) -> Unit) : RecyclerView.Adapter<CalendarDayIndicatorAdapter.ViewHolder>() {

    private var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: CalendarItemIndicatorDayBinding = CalendarItemIndicatorDayBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val day = days[position]

        val dayInitial = day.dayName.first().toString()

        holder.binding.tvDayName.text = dayInitial
        holder.binding.tvDayDate.text = day.date.toString()

        if (position == selectedPosition) {
            holder.binding.llMainLayouts.setBackgroundResource(R.drawable.calendar_select_bg)
            holder.binding.tvDayName.setTextColor(Color.parseColor("#ffffff"))
            holder.binding.tvDayDate.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.binding.llMainLayouts.setBackgroundResource(R.drawable.calendar_unselect_bg)
            holder.binding.tvDayName.setTextColor(Color.parseColor("#3C4541"))
            holder.binding.tvDayDate.setTextColor(Color.parseColor("#3C4541"))
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onDaySelected(day)
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }


    class ViewHolder(var binding: CalendarItemIndicatorDayBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}