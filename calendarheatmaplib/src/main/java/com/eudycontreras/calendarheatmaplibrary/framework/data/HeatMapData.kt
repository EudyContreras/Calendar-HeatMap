package com.eudycontreras.calendarheatmaplibrary.framework.data

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

@Serializable
data class HeatMapData(
    val style: HeatMapStyle?,
    val options: HeatMapOptions,
    val calendar: Calendar
)

@Serializable
data class HeatMapStyle(
    @ColorInt val minColor: Int,
    @ColorInt val maxColor: Int,
    @ColorInt val emptyColor: Int,
    @Dimension val cellGap: Float,
    @Dimension val cellSize: Float
)

@Serializable
data class HeatMapOptions(
    val showMonthLabels: Boolean = true,
    val showDayLabels: Boolean = true
)

@Serializable
data class Calendar(
    val months: List<Month>
)

@Serializable
data class Month(
    val label: String,
    val weeks: List<Week>
)

@Serializable
data class Week(
    val weekNumber: Int,
    val weekDays: List<WeekDay>
)

@Serializable
data class WeekDay(
    val dayName: String,
    val frequencyData: Frequency
)

@Serializable
data class Frequency(
    val count: Int,
    @ContextualSerialization val data: Any?
)

private const val MAX_WEEKS = 53
private const val MAX_DAYS = 7