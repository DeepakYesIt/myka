package com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model

data class CheckoutScreenModel(
    val code: Int?,
    val `data`: CheckoutScreenModelData?,
    val message: String?,
    val success: Boolean?
)

data class CheckoutScreenModelData(
    val address: MutableList<Addres>?,
    val bagfee: Double?,
    val card: String?,
    val Store: String?,
    val store_image: String?,
    val delivery: Double?,
    val note: Note?,
    val phone: Any?,
    val service: Double?,
    val subtotal: Double?,
    val total: Double?,
    val ingredient_count: Int?,
)

data class Addres(
    val apart_num: String?,
    val city: String?,
    val country: String?,
    val created_at: String?,
    val deleted_at: Any,
    val id: Int?,
    val latitude: String?,
    val longitude: String?,
    val primary: Int?,
    val state: String?,
    val street_name: String?,
    val street_num: String?,
    val type: String?,
    val updated_at: String?,
    val user_id: Int?,
    val zipcode: String?
)

data class Note(
    val description: String?,
    val id: Int?,
    val pickup: String?,
    val user_id: Int?
)