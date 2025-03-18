package com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.model

import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Recipes

data class ShoppingListModel(
    val code: Int,
    val `data`: ShoppingListModelData,
    val message: String,
    val success: Boolean
)

data class ShoppingListModelData(
    val ingredient: MutableList<ShoppingIngredient>?,
    val recipe: MutableList<Recipes>?
)

data class ShoppingIngredient(
    val created_at: String?,
    val deleted_at: Any?,
    val food: String?,
    val foodCategory: String?,
    val food_id: Any?,
    val id: Int?,
    val image: String?,
    val measure: String?,
    val name: String?,
    val order_id: Any?,
    val product_id: Any?,
    val quantity: Int?,
    val serving: Int?,
    val status: Int?,
    val store_id: Any?,
    val updated_at: String?,
    val user_id: Int?
)