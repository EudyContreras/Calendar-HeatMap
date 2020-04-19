package com.eudycontreras.calendarheatmaplibrary.framework.data

import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.extensions.sp
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
    @Dimension var cellGap: Float? = null,
    @Dimension var cellSize: Float? = null,
    val timeSpan: TimeSpan,
    val options: HeatMapOptions? = null
) {
    companion object {
        const val CELL_SIZE_RATIO = 0.15f
    }
}

data class TextStyle(
    var typeFace: Typeface,
    var textSize: Float,
    @ColorInt var textColor: Int
)

data class HeatMapStyle(
    var minCellColor: Int = AndroidColor.TRANSPARENT,
    var maxCellColor: Int = AndroidColor.TRANSPARENT,
    var emptyCellColor: Int = AndroidColor.TRANSPARENT,
    var dayLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT_BOLD, 12.sp, AndroidColor.DKGRAY),
    var monthLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT_BOLD, 12.sp, AndroidColor.DKGRAY),
    var legendLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT_BOLD, 12.sp, AndroidColor.DKGRAY)
)

@Serializable
data class HeatMapLabel(
    val text: String,
    val value: Int,
    val active: Boolean
)

@Serializable
data class HeatMapOptions(
    val legendLessLabel: String = LESS,
    val legendMoreLabel: String = MORE,
    var showMonthLabels: Boolean = true,
    var showDayLabels: Boolean = true,
    var showLegend: Boolean = true,
    var legendAlignment: Alignment = Alignment.LEFT,
    val dayLabels: List<HeatMapLabel> = STANDARD_DAY_LABELS,
    val monthLabels: List<HeatMapLabel> = STANDARD_MONTH_LABELS
) {
    companion object {
        const val LESS = "Less"
        const  val MORE = "More"

        val LEGEND_AREA_HEIGHT = 45.dp
        val MONTH_LABEL_AREA_HEIGHT = 45.dp
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
) {
    companion object {
        const val TAG = 0x01
    }
}

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
    val date: Date,
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
        const val TAG = 0x00
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

fun Week.hasMonthLabel(): Boolean {
    return weekDays.groupBy { it.date.month.label }.count() <= 1
}

fun Week.getMonthLabel(): Int {
    return weekDays.map { it.date.month.value }.first()
}

fun WeekDay.getColorValue(
    style: HeatMapStyle,
    min: Float = Frequency.MIN_VALUE.toFloat(),
    max: Float = Frequency.MAX_VALUE.toFloat()
): Color {
    val value = this.frequencyData.count

    return if (value <= 0) {
        MutableColor.fromColor(style.emptyCellColor)
    } else {
        val range = mapRange(value.toFloat(), min, max, MIN_OFFSET, MAX_OFFSET)
        val colorMin = MutableColor.fromColor(style.minCellColor)
        val colorMax = MutableColor.fromColor(style.maxCellColor)
        MutableColor.interpolateColor(colorMin, colorMax, range)
    }
}