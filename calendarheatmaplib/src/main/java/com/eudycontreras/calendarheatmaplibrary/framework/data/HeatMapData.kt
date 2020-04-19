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
    @Dimension var cellGap: Float? = null,
    @Dimension var cellSize: Float? = null,
    val timeSpan: TimeSpan,
    val style: HeatMapStyle? = null,
    val options: HeatMapOptions? = null
)

@Serializable
data class HeatMapStyle(
    @ColorInt var minColor: Int = 0x00000000,
    @ColorInt var maxColor: Int = 0x00000000,
    @ColorInt var emptyColor: Int = 0x00000000
)

@Serializable
data class HeatMapOptions(
    var showMonthLabels: Boolean = true,
    var showDayLabels: Boolean = true
)

@Serializable
data class TimeSpan(
    val months: List<Month>
)

@Serializable
data class Month(
    val index: Int,
    val year: Int,
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
    val index: Int,
    val label: String,
    val activeLabel: Boolean,
    val frequencyData: Frequency
)

@Serializable
data class Frequency(
    val count: Int,
    @ContextualSerialization val data: Any?
)

private const val MAX_WEEKS = 53
private const val MAX_DAYS = 7