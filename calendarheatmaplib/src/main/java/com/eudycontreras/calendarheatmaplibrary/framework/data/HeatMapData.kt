package com.eudycontreras.calendarheatmaplibrary.framework.data

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Color
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
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
) {
    companion object {
        const val CELL_SIZE_RATIO = 0.30f
    }
}

@Serializable
data class HeatMapStyle(
    @ColorInt var minColor: Int = AndroidColor.TRANSPARENT,
    @ColorInt var maxColor: Int = AndroidColor.TRANSPARENT,
    @ColorInt var emptyColor: Int = AndroidColor.TRANSPARENT
)

@Serializable
data class HeatMapOptions(
    var showMonthLabels: Boolean = true,
    var showDayLabels: Boolean = true
)

@Serializable
data class TimeSpan(
    val dateMin: Date,
    val dateMax: Date,
    val weeks: List<Week>
) {
    companion object {
        const val MAX_DAYS = 7
    }
}

@Serializable
data class Date(
    val day: Int,
    val month: Month,
    val year: Int
)


@Serializable
data class Month(
    val value: Int,
    val label: String
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
    val date: Date,
    val activeLabel: Boolean,
    val frequencyData: Frequency
)

@Serializable
data class Frequency(
    val count: Int,
    @ContextualSerialization val data: Any?
) {
    companion object {
        const val MIN_VALUE = 0
        const val MAX_VALUE = 50
    }
}

fun HeatMapData.getColumnCount(): Int {
    return timeSpan.weeks.count()
}

fun WeekDay.getColorValue(style: HeatMapStyle): Color {
    val value = this.frequencyData.count

    return if (value <= 0) {
        MutableColor.fromColor(style.emptyColor)
    } else {
        val min = Frequency.MIN_VALUE.toFloat()
        val max = Frequency.MAX_VALUE.toFloat()
        val range = mapRange(value.toFloat(), min, max, MIN_OFFSET, MAX_OFFSET)
        val colorMin = MutableColor.fromColor(style.minColor)
        val colorMax = MutableColor.fromColor(style.maxColor)
        MutableColor.interpolateColor(colorMin, colorMax, range)
    }
}