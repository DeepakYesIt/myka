package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemLongClickListener
import com.mykaimeal.planner.databinding.AdapterAddressItemBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.GetAddressListModelData

class AdapterGetAddressItem(private var addressList: MutableList<GetAddressListModelData>?,
                            private var requireActivity: FragmentActivity,
                            private var onItemClickedListener: OnItemLongClickListener
):
    RecyclerView.Adapter<AdapterGetAddressItem.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterAddressItemBinding = AdapterAddressItemBinding.inflate(inflater, parent,false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemList= addressList?.get(position)

        if (itemList!!.type!=null){
            holder.binding.tvSetName.text=itemList.type.toString()
        }

        val addressParts = listOf(
            itemList.apart_num,
            itemList.street_name,
            itemList.city,
            itemList.state,
            itemList.country,
            itemList.zipcode
        )

        val isAddressComplete = addressParts.all { !it.isNullOrBlank() }
        val latitude = itemList.latitude
        val longitude = itemList.longitude

        if (isAddressComplete) {
            val fullAddress = addressParts.joinToString(" ")
            holder.binding.tvFullAddress.text = fullAddress

            if (latitude != null && longitude != null && fullAddress.isNotBlank()) {
                holder.binding.imagePencilIcon.setOnClickListener {
                    onItemClickedListener.itemLongClick(itemList.id, latitude.toString(), longitude.toString(), fullAddress)
                }
            }
        }



    }

    override fun getItemCount(): Int {
        return addressList!!.size
    }


    class ViewHolder(var binding: AdapterAddressItemBinding) : RecyclerView.ViewHolder(binding.root){
    }

}