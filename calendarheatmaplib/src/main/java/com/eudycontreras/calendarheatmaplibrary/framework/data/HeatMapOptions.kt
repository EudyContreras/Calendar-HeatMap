package com.eudycontreras.calendarheatmaplibrary.framework.data

import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import kotlinx.serialization.Serializable

@Serializable
data class HeatMapLabel(
    val text: String,
    val value: Int,
    val active: Boolean
)

@Serializable
data class HeatMapOptions(
    var legendLessLabel: String = LESS,
    var legendMoreLabel: String = MORE,
    var showMonthLabels: Boolean = true,
    var showCellDayText: Boolean = false,
    var showDayLabels: Boolean = true,
    var showLegend: Boolean = true,
    var legendAlignment: Alignment = Alignment.LEFT,
    var interceptorOffsetX: Float = 50.dp,
    var interceptorOffsetY: Float = 50.dp,
    var dayLabels: List<HeatMapLabel> = STANDARD_DAY_LABELS,
    var monthLabels: List<HeatMapLabel> = STANDARD_MONTH_LABELS
) {
    companion object {
        const val LESS = "Less"
        const  val MORE = "More"

        val LEGEND_AREA_HEIGHT = 40.dp
        val MONTH_LABEL_AREA_HEIGHT = 25.dp
        val DAY_LABEL_AREA_WIDTH = 30.dp

        val STANDARD_DAY_LABELS = listOf(
            HeatMapLabel("Sun", 0, false),
            HeatMapLabel("Mon", 1, true),
            HeatMapLabel("Tus", 2, false),
            HeatMapLabel("Wed", 3, true),
            HeatMapLabel("Thu", 4, false),
            HeatMapLabel("Fri", 5, true),
            HeatMapLabel("Sat", 6, false)
        )
        val STANDARD_MONTH_LABELS = listOf(
            HeatMapLabel("Jan", 0, true),
            HeatMapLabel("Feb", 1, true),
            HeatMapLabel("Mar", 2, true),
            HeatMapLabel("Apr", 3, true),
            HeatMapLabel("Maj", 4, true),
            HeatMapLabel("Jun", 5, true),
            HeatMapLabel("Jul", 6, true),
            HeatMapLabel("Aug", 7, true),
            HeatMapLabel("Sep", 8, true),
            HeatMapLabel("Oct", 9, true),
            HeatMapLabel("Nov", 10, true),
            HeatMapLabel("Dec", 11, true)
        )
    }
}