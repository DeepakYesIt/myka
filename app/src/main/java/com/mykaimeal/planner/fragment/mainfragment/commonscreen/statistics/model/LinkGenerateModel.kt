package com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics.model

data class LinkGenerateModel(
    val code: Int?,
    val `data`: String?,
    val message: String?,
    val success: Boolean?
)



data class StatisticsGraphScreen(
    val graph_data: GraphData,
    val month: String,
    val saving: Double,
    val total_spent: Double
)

data class GraphData(
    val week_1: Int,
    val week_2: Double,
    val week_3: Int,
    val week_4: Int
)