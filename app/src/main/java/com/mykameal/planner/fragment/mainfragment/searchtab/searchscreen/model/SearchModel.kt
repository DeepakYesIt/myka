package com.mykameal.planner.fragment.mainfragment.searchtab.searchscreen.model

data class SearchModel(
    val code: Int,
    val `data`: SearchModelData,
    val message: String,
    val success: Boolean
)

data class SearchModelData(
    val recipes: List<Recipe>,
    val url: String
)

data class Recipe(
    val _links: Links,
    val is_like: Int,
    val recipe: RecipeX
)

data class Links(
    val self: Self
)

data class Self(
    val href: String,
    val title: String
)

data class RecipeX(
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
)

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
