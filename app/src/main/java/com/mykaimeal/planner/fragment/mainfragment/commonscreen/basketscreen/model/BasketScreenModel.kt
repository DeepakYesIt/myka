package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model

import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.Recipe

data class BasketScreenModel(
    val code: Int?,
    val `data`: BasketScreenModelData?,
    val message: String?,
    val success: Boolean?
)


data class BasketScreenModelData(
    val ingredient: MutableList<Ingredient>?,
    val recipe: MutableList<Recipes>?,
    val stores: MutableList<Store>?
)

data class Ingredient(
    val created_at: String?,
    val deleted_at: Any,
    val food: String?,
    val foodCategory: String?,
    val id: Int?,
    val image: String?,
    val measure: String?,
    val name: String?,
    val quantity: Int?,
    val serving: Int?,
    val updated_at: String?,
    val user_id: Int?
)

data class Store(
    val address: Address?,
    val distance: Double?,
    val operational_hours: OperationalHours?,
    val store_name: String?,
    val store_uuid: String?
)

data class Address(
    val city: String?,
    val country: String?,
    val lat: Double?,
    val lon: Double?,
    val state: String?,
    val street_addr: String?,
    val zipcode: String?
)

data class OperationalHours(
    val Friday: String?,
    val Monday: String?,
    val Saturday: String?,
    val Sunday: String?,
    val Thursday: String?,
    val Tuesday: String?,
    val Wednesday: String?
)

data class Recipes(
    val created_at: String?,
    val `data`: DataX?,
    val deleted_at: Any?,
    val id: Int?,
    val serving: String?,
    val type: String?,
    val updated_at: String?,
    val uri: String?,
    val user_id: Int?
)

data class DataX(
    val _links: Links?,
    val recipe: Recipe?
)

data class Links(
    val self: Self?
)

data class Self(
    val href: String?,
    val title: String?
)