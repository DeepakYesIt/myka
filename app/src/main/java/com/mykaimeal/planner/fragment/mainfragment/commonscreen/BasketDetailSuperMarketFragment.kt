package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mykaimeal.planner.adapter.ItemSectionAdapter
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.AdapterSuperMarket
import com.mykaimeal.planner.databinding.FragmentBasketDetailSuperMarketBinding
import com.mykaimeal.planner.model.DataModel

class BasketDetailSuperMarketFragment : Fragment(), OnItemClickListener, OnItemSelectListener {

    private lateinit var binding: FragmentBasketDetailSuperMarketBinding
    private lateinit var itemSectionAdapter: ItemSectionAdapter
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var rcvBottomDialog: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBasketDetailSuperMarketBinding.inflate(layoutInflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        return binding.root
    }

    private fun initialize() {

        /*binding.relTescoMarket.setOnClickListener{
            bottomSheetDialog()
        }*/

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rlGoToCheckout.setOnClickListener{
            findNavController().navigate(R.id.checkoutScreenFragment)
        }

     /*   binding.rlAddToTescoBasketButton.setOnClickListener {
            findNavController().navigate(R.id.tescoCartItemFragmentFragment)
        }
*/
        val data1 = DataModel().apply {
            title = "Fresh Boneless Chicken Breast, 2.75-3.8 lb"
            image = R.drawable.meat_image
            valuePrice = 13.80
        }

        val data2 = DataModel().apply {

            title = "Sara Lee Artesano Brioche Bakery Bread"
            image = R.drawable.sara_lee_bakery_bread_image
            valuePrice = 3.48
        }

        val data3 = DataModel().apply {

            title = "Great Value, Large White Eggs, 12 Count\n"
            image = R.drawable.great_eggs_carate_image

            valuePrice = 4.17
        }

        val data4 = DataModel().apply {

            title = "Great Value Milk Whole Vitamin D, Half Gallon..."
            image = R.drawable.great_value_milk_image
            valuePrice = 1.79
        }

        val data5 = DataModel().apply {

            title = "Great Value Sweet Cream Unsalted Butter..."
            image = R.drawable.butter_sweet_cream_image
            valuePrice = 3.96
        }

         val data6 = DataModel().apply {
            title = "Great Value Greek Plain Nonfat Yogurt, 32 oz Tub"
            image = R.drawable.great_plain_image
             valuePrice = 2.76
        }


        val categorizedItems: HashMap<String, List<DataModel>> = hashMapOf(
            "Meat" to listOf(data1),
            "Bakery" to listOf(data2),
            "Dairy" to listOf(data3, data4, data5,data6)
        )

        binding.recyclerItemList.layoutManager = LinearLayoutManager(requireActivity())
        itemSectionAdapter = ItemSectionAdapter(categorizedItems,this)
        binding.recyclerItemList.adapter = itemSectionAdapter
    }

    private fun bottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        bottomSheetDialog!!.setContentView(R.layout.bottom_sheet_select_super_market_near_me)
        rcvBottomDialog = bottomSheetDialog!!.findViewById<RecyclerView>(R.id.rcvBottomDialog)
        val textTitle: TextView? = bottomSheetDialog!!.findViewById<TextView>(R.id.textTitle)
        bottomSheetDialog!!.show()

        superMarketModel()

    }

    private fun superMarketModel() {
        val dataList1: MutableList<DataModel> = mutableListOf()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()
        val data8 = DataModel()
        val data9 = DataModel()

        data1.title = "Tesco"
        data1.isOpen = false
        data1.type = "SuperMarket"
        data1.price = "25"
        data1.image = R.drawable.super_market_tesco_image

        data2.title = "Coop"
        data2.isOpen = false
        data2.price = "28"
        data2.image = R.drawable.super_market_coop_image

        data3.title = "Iceland"
        data3.isOpen = false
        data3.price = "30"
        data3.image = R.drawable.super_market_iceland_image

        data4.title = "Albertsons"
        data4.isOpen = false
        data4.price = "32"
        data4.image = R.drawable.super_market_albertsons

        data5.title = "Aldi"
        data5.isOpen = false
        data5.price = "35"
        data5.image = R.drawable.super_market_aldi_image

        data6.title = "Costco"
        data6.isOpen = false
        data6.price = "35"
        data6.image = R.drawable.super_market_costco_image

        data7.title = "Albertsons"
        data7.isOpen = false
        data7.price = "32"
        data7.image = R.drawable.super_market_albertsons

        data8.title = "Aldi"
        data8.isOpen = false
        data8.price = "35"
        data8.image = R.drawable.super_market_aldi_image

        data9.title = "Costco"
        data9.isOpen = false
        data9.price = "35"
        data9.image = R.drawable.super_market_costco_image

        dataList1.add(data1)
        dataList1.add(data2)
        dataList1.add(data3)
        dataList1.add(data4)
        dataList1.add(data5)
        dataList1.add(data6)
        dataList1.add(data7)
        dataList1.add(data8)
        dataList1.add(data9)

       /* adapterSuperMarket = AdapterSuperMarket(dataList1, requireActivity(), this)
        rcvBottomDialog!!.adapter = adapterSuperMarket*/
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

        if (status == "2") {
            findNavController().navigate(R.id.tescoCartItemFragmentFragment)
        }
    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {

        if (status=="2"){
            findNavController().navigate(R.id.basketProductDetailsFragment)
        }
    }


}