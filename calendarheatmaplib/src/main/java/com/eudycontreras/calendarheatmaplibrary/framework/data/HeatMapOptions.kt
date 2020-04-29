package com.eudycontreras.calendarheatmaplibrary.framework.data

import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.properties.Index
import kotlinx.serialization.Serializable

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

@Serializable
data class HeatMapLabel(
    val text: String,
    val value: Int,
    val active: Boolean
)

@Serializable
data class AnimationData(
    val delay: Long = 0,
    val duration: Long = 250,
    val stagger: Long = 50,
    val epiCenter: Index? = null
)

@Serializable
data class HeatMapOptions(
    var legendLessLabel: String = LESS,
    var legendMoreLabel: String = MORE,
    var showCellDayText: Boolean = false,
    var showMonthLabels: Boolean = true,
    var showDayLabels: Boolean = true,
    var showLegend: Boolean = false,
    var legendAlignment: Alignment = Alignment.LEFT,
    var cellHighlightDuration: Long = 250,
    var interceptorOffsetX: Float = 60.dp,
    var interceptorOffsetY: Float = 70.dp,
    var maxFrequencyValue: Int = Frequency.MAX_VALUE,
    var matrixRevealAnimation: AnimationData? = AnimationData(),
    var dayLabels: List<HeatMapLabel> = listOf(
        HeatMapLabel("Sun", 0, false),
        HeatMapLabel("Mon", 1, true),
        HeatMapLabel("Tus", 2, false),
        HeatMapLabel("Wed", 3, true),
        HeatMapLabel("Thu", 4, false),
        HeatMapLabel("Fri", 5, true),
        HeatMapLabel("Sat", 6, false)
    ),
    var monthLabels: List<HeatMapLabel> =  listOf(
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
) {
    companion object {
        const val LESS = "Less"
        const val MORE = "More"

        val LEGEND_AREA_HEIGHT = 35.dp
    }
}