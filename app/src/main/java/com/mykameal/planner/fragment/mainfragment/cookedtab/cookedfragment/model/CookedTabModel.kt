package com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.model

data class CookedTabModel(
    val code: Int,
    val `data`: CookedTabModelData,
    val message: String,
    val success: Boolean
)

data class CookedTabModelData(
    val Breakfast: MutableList<Breakfast>?,
    val Dinner: MutableList<Dinner>?,
    val Lunch: MutableList<Lunch>?,
    val Snacks: MutableList<Snacks>?,
    val Teatime: MutableList<Teatime>?,
    val fridge:Int,
    val freezer:Int
)

data class Breakfast(
    val created_at: String,
    val date: String,
    val day: Any,
    val deleted_at: Any,
    val id: Int,
    val plan_type: Int,
    val recipe: Recipe,
    val status: Int,
    val type: String,
    val updated_at: String,
    val uri: String,
    val user_id: Int
)

data class Teatime(
    val created_at: String,
    val date: String,
    val day: Any,
    val deleted_at: Any,
    val id: Int,
    val plan_type: Int,
    val recipe: Recipe,
    val status: Int,
    val type: String,
    val updated_at: String,
    val uri: String,
    val user_id: Int
)

data class Snacks(
    val created_at: String,
    val date: String,
    val day: Any,
    val deleted_at: Any,
    val id: Int,
    val plan_type: Int,
    val recipe: Recipe,
    val status: Int,
    val type: String,
    val updated_at: String,
    val uri: String,
    val user_id: Int
)


data class Dinner(
    val created_at: String,
    val date: String,
    val day: Any,
    val deleted_at: Any,
    val id: Int,
    val plan_type: Int,
    val recipe: Recipe,
    val status: Int,
    val type: String,
    val updated_at: String,
    val uri: String,
    val user_id: Int
)

data class Lunch(
    val created_at: String,
    val date: String,
    val day: Any,
    val deleted_at: Any,
    val id: Int,
    val plan_type: Int,
    val recipe: Recipe,
    val status: Int,
    val type: String,
    val updated_at: String,
    val uri: String,
    val user_id: Int
)

data class Recipe(
    val calories: Double,
    val cautions: MutableList<Any?>,
    val cuisineType: MutableList<String>?,
    val dietLabels: MutableList<String>?,
    val digest: MutableList<Digest>?,
    val dishType: MutableList<String>?,
    val healthLabels: MutableList<String>?,
    val image: String,
    val images: Images,
    val ingredientLines: MutableList<String>?,
    val ingredients: MutableList<Ingredient>?,
    val instructionLines: MutableList<String>?,
    val label: String,
    val mealType: MutableList<String>?,
    val shareAs: String,
    val source: String,
    val totalDaily: Any?,
    val totalNutrients: Any?,
    val totalTime: Int,
    val totalWeight: Double,
    val uri: String,
    val url: String,
    val yield: Int
)


/*data class RecipeX(
    val calories: Double,
    val cautions: List<String>,
    val co2EmissionsClass: String,
    val cuisineType: List<String>,
    val dietLabels: List<String>,
    val digest: List<Digest>,
    val dishType: List<String>,
    val glycemicIndex: Double,
    val healthLabels: List<String>,
    val image: String,
    val images: Images,
    val ingredientLines: List<String>,
    val ingredients: List<Ingredient>,
    val label: String,
    val mealType: List<String>,
    val shareAs: String,
    val source: String,
    val tags: List<String>,
    val totalDaily: Any,
    val totalNutrients: Any,
    val totalTime: Int,
    val totalWeight: Double,
    val uri: String,
    val url: String,
    val yield: Int
)*/

data class Digest(
    val daily: Double,
    val hasRDI: Boolean,
    val label: String,
    val schemaOrgTag: String,
    val sub: List<Sub>,
    val tag: String,
    val total: Double,
    val unit: String
)

data class Sub(
    val daily: Double,
    val hasRDI: Boolean,
    val label: String,
    val schemaOrgTag: String,
    val tag: String,
    val total: Double,
    val unit: String
)

data class Ingredient(
    val food: String,
    val foodCategory: String,
    val foodId: String,
    val image: String,
    val measure: String,
    val quantity: Double,
    val text: String,
    val weight: Double
)

data class Images(
    val LARGE: LARGE,
    val REGULAR: REGULAR,
    val SMALL: SMALL,
    val THUMBNAIL: THUMBNAIL
)

data class LARGE(
    val height: Int,
    val url: String,
    val width: Int
)

data class REGULAR(
    val height: Int,
    val url: String,
    val width: Int
)

data class SMALL(
    val height: Int,
    val url: String,
    val width: Int
)

data class THUMBNAIL(
    val height: Int,
    val url: String,
    val width: Int
)