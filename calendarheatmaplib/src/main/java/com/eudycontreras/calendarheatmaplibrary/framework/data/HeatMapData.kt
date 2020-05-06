package com.eudycontreras.calendarheatmaplibrary.framework.data

import androidx.annotation.Dimension
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Color
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.SerialName
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
    val timeSpan: TimeSpan,
    val options: HeatMapOptions? = null
) {
    companion object {
        const val CELL_SIZE_RATIO = 0.15f
    }
}

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
    val month: Int,
    val year: Int? = null
) {
    override fun toString(): String {
        return "$year-${month + 1}-$day"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Date) return false
        if (day != other.day) return false
        if (month != other.month) return false
        if (year != null && other.year != null) {
            if (year != other.year) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = day
        result = 31 * result + month
        return result
    }
}

@Serializable
data class Week(
    val weekNumber: Int,
    val weekDays: List<WeekDay>
)

@Serializable
data class WeekDay(
    val index: Int,
    val date: Date,
    val dateString: String,
    val frequencyData: Frequency
)

@Serializable
data class Frequency(
    val count: Int,
    @ContextualSerialization val data: Any?
) {
    val countString: String
        get() = if (count > 0) { count.toString().padStart(2, '0')
        } else "No"

    companion object {
        const val MIN_VALUE = 0
        const val MAX_VALUE = 50
    }

    override fun toString(): String {
        return "Frequency: $count"
    }
}

@Serializable
enum class Alignment {
    @SerialName("LEFT")
    LEFT,
    @SerialName("RIGHT")
    RIGHT,
    @SerialName("CENTER")
    CENTER
}

fun HeatMapData.getColumnCount(): Int {
    return timeSpan.weeks.count()
}

fun HeatMapData.getRowCount(): Int {
    return timeSpan.weeks.first().weekDays.count()
}

fun Week.hasMonthLabel(months: List<HeatMapLabel>): Boolean {
    return weekDays.groupBy { months[it.date.month] }.count() <= 1
}

fun Week.getMonthLabel(): Int {
    return weekDays.map { it.date.month }.first()
}

fun WeekDay.getColorValue(
    style: HeatMapStyle,
    max: Int = Frequency.MAX_VALUE
): Color {
    val value = this.frequencyData.count

    return if (value <= 0) {
        MutableColor.fromColor(style.emptyCellColor)
    } else {
        val range = mapRange(value.toFloat(), Frequency.MIN_VALUE.toFloat(), max.toFloat(), MIN_OFFSET, MAX_OFFSET)
        val colorMin = MutableColor.fromColor(style.minCellColor)
        val colorMax = MutableColor.fromColor(style.maxCellColor)
        MutableColor.interpolateColor(colorMin, colorMax, range)
    }
}