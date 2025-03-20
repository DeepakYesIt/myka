package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model

data class BasketDetailsModel(
    val code: Int,
    val `data`: BasketDetailsModelData?,
    val message: String?,
    val success: Boolean?
)


data class BasketDetailsModelData(
    val attributes: MutableList<Any>?,
    val description: String?,
    val formatted_price: String?,
    val image: String?,
    val is_available: Boolean?,
    val min_price: Int?,
    val name: String?,
    val original_price: Int?,
    val price: Int?,
    val product_id: String?,
    val qty_available: Any?,
    val should_fetch_customizations: Boolean?,
    val supports_image_scaling: Boolean?,
    val unit_of_measurement: String?,
    val unit_size: Int?
)