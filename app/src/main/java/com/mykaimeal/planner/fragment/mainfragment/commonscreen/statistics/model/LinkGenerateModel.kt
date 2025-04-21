package com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics.model

data class LinkGenerateModel(
    val code: Int?,
    val `data`: String?,
    val message: String?,
    val success: Boolean?
)

data class StatisticsGraphModel(
    val code: Int,
    val `data`: StatisticsGraphModelData,
    val message: String,
    val success: Boolean
)

data class StatisticsGraphModelData(
    val graph_data: GraphData,
    val month: String,
    val saving: Int,
    val total_spent: Int
)

data class GraphData(
    val week_1: Int,
    val week_2: Int,
    val week_3: Int,
    val week_4: Int
)

data class SpendingChartItem(
    val label: String,    // e.g., "01 April"
    val amount: Int,      // e.g., 300
    val color: Int        // bar color
)