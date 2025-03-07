package com.mykaimeal.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mykaimeal.planner.OnItemClickedListener
import com.mykaimeal.planner.databinding.AdapterAddressItemBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.GetAddressListModelData

class AdapterGetAddressItem(private var addressList: MutableList<GetAddressListModelData>?,
                            private var requireActivity: FragmentActivity,
                            private var onItemClickedListener: OnItemClickedListener
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

        if (itemList.apart_num!=null){
            if (itemList.street_name!=null){
                if (itemList.city!=null){
                    if (itemList.state!=null){
                        if (itemList.country!=null){
                            if (itemList.zipcode!=null){
                                holder.binding.tvFullAddress.text=itemList.apart_num+" "+itemList.street_name+" "+itemList.city+" "+
                                        itemList.state+" "+itemList.country+" "+itemList.zipcode
                            }
                        }
                    }
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