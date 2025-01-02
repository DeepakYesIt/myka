package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.eatingOut

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.adapter.AdapterCookingSchedule
import com.yesitlabs.mykaapp.adapter.BodyGoalAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentEatingOutBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.eatingOut.viewmodel.EatingOutViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EatingOutFragment : Fragment(),View.OnClickListener,OnItemClickListener {

    private var binding: FragmentEatingOutBinding? = null
    private var dietaryRestrictionsAdapter: AdapterCookingSchedule? = null
    private val dataList = ArrayList<DataModel>()
    private var status:String=""
    private var bodyGoalAdapter: BodyGoalAdapter? = null
    private var dropDownstatus:Boolean=true
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private lateinit var eatingOutViewModel: EatingOutViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEatingOutBinding.inflate(inflater, container, false)

        eatingOutViewModel = ViewModelProvider(this)[EatingOutViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.progressBar10.max=10
            totalProgressValue=10
            updateProgress(9)
        } else {
            binding!!.progressBar10.max=11
            totalProgressValue=11
            updateProgress(10)
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        if (BaseApplication.isOnline(requireContext())) {
            eatingOutApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        initialize()

        return binding!!.root
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar10.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imbBackEatingOut.setOnClickListener(this)
        binding!!.tvSkipBtn.setOnClickListener(this)
        binding!!.tvNextBtn.setOnClickListener(this)
//        binding!!.rlEatingDropDown.setOnClickListener(this)
//        binding!!.relDaily.setOnClickListener(this)
//        binding!!.relAFewTimes.setOnClickListener(this)
//        binding!!.relOnceAWeek.setOnClickListener(this)
//        binding!!.relRarely.setOnClickListener(this)

    }

    private fun stillSkipDialog() {
        val dialogStillSkip: Dialog = context?.let { Dialog(it) }!!
        dialogStillSkip.setContentView(R.layout.alert_dialog_still_skip)
        dialogStillSkip.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogCancelBtn = dialogStillSkip.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val tvDialogSkipBtn = dialogStillSkip.findViewById<TextView>(R.id.tvDialogSkipBtn)
        dialogStillSkip.show()
        dialogStillSkip.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogStillSkip.dismiss()
        }

        tvDialogSkipBtn.setOnClickListener {
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.reasonsForTakeAwayFragment)
//            val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun eatingOutApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            eatingOutViewModel.getEatingOut {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, BodyGoalModel::class.java)
                        Log.d("@@@ Response profile", "message :- ${it.data}")
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data)
                        } else {
                            if (bodyModel.code == ErrorMessage.code) {
                                showAlertFunction(bodyModel.message, true)
                            }else{
                                showAlertFunction(bodyModel.message, false)
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }
                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }
        }
    }

    private fun showDataInUi(bodyModelData: List<BodyGoalModelData>) {

        if (bodyModelData!=null && bodyModelData.size>0){
            bodyGoalAdapter = BodyGoalAdapter(bodyModelData, requireActivity(), this)
            binding!!.rcyEatingOut.adapter = bodyGoalAdapter
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

//    private fun eatingOutModel() {
//        val data1 = DataModel()
//        val data2 = DataModel()
//        val data3 = DataModel()
//        val data4 = DataModel()
//        val data5 = DataModel()
//
//        data1.title = "No food prepared"
//        data1.isOpen= false
//        data1.type = "EatingOut"
//
//        data2.title = "Convenience"
//        data2.isOpen= false
//        data2.type = "EatingOut"
//
//        data3.title = "Cravings"
//        data3.isOpen= false
//        data3.type = "EatingOut"
//
//        data4.title = "Social Occasions"
//        data4.isOpen= false
//        data4.type = "EatingOut"
//
//        data5.title = "Add More"
//        data5.isOpen= false
//        data5.type = "EatingOut"
//
//        dataList.add(data1)
//        dataList.add(data2)
//        dataList.add(data3)
//        dataList.add(data4)
//        dataList.add(data5)
//
//        dietaryRestrictionsAdapter = AdapterCookingSchedule(dataList, requireActivity(),this)
//        binding!!.rcyEatingOut.adapter = dietaryRestrictionsAdapter
//    }

    override fun onClick(item: View?) {
        when (item!!.id) {
            R.id.imbBackEatingOut -> {
                findNavController().navigateUp()
            }

            R.id.tvSkipBtn->{
                stillSkipDialog()
            }

            R.id.tvNextBtn->{
                if (status=="2"){
                    findNavController().navigate(R.id.reasonsForTakeAwayFragment)
//                    val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
//                    startActivity(intent)
                }
            }

//            R.id.rlEatingDropDown->{
//                if (dropDownstatus){
//                    dropDownstatus=false
//                    val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_up_icon)
//                    drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
//                    binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
//                    binding!!.rcyEatingOut.visibility=View.VISIBLE
//                }else{
//                    dropDownstatus=true
//                    val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
//                    drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
//                    binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
//                    binding!!.rcyEatingOut.visibility=View.GONE
//                }
//            }

//            R.id.relDaily -> {
//                status="2"
//                binding!!.relDaily.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageDaily.visibility=View.VISIBLE
//                binding!!.relAFewTimes.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageAFewTimes.visibility=View.GONE
//                binding!!.relOnceAWeek.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageOnceAWeek.visibility=View.GONE
//                binding!!.relRarely.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageRarely.visibility=View.GONE
//                status()
//            }
//
//            R.id.relAFewTimes->{
//                status="2"
//                binding!!.relDaily.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageDaily.visibility=View.GONE
//                binding!!.relAFewTimes.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageAFewTimes.visibility=View.VISIBLE
//                binding!!.relOnceAWeek.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageOnceAWeek.visibility=View.GONE
//                binding!!.relRarely.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageRarely.visibility=View.GONE
//                status()
//            }
//
//            R.id.relOnceAWeek->{
//                status="2"
//                binding!!.relDaily.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageDaily.visibility=View.GONE
//                binding!!.relAFewTimes.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageAFewTimes.visibility=View.GONE
//                binding!!.relOnceAWeek.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageOnceAWeek.visibility=View.VISIBLE
//                binding!!.relRarely.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageRarely.visibility=View.GONE
//                status()
//            }

//            R.id.relRarely->{
//                status="2"
//                binding!!.relDaily.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageDaily.visibility=View.GONE
//                binding!!.relAFewTimes.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageAFewTimes.visibility=View.GONE
//                binding!!.relOnceAWeek.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageOnceAWeek.visibility=View.GONE
//                binding!!.relRarely.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageRarely.visibility=View.VISIBLE
//                status()
//            }
        }
    }

    private fun status(){
        if (status != "2") {
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)

        }
    }

    override fun itemClick(position: Int?, status1: String?, type: String?) {
        if (status1 == "1") {
            status()
        } else {
            status="2"
            status()
        }
    }
}