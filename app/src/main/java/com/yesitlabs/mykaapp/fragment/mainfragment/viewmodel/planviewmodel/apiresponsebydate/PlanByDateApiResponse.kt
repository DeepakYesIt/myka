import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate.DataPlayByDate

data class PlanByDateApiResponse(
    val code: Int,
    val `data`: DataPlayByDate?,
    val message: String,
    val success: Boolean
)