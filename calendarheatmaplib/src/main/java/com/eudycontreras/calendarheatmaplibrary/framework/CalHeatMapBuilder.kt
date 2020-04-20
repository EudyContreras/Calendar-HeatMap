package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.util.SparseArray
import android.view.MotionEvent
import androidx.core.util.set
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.CellInterceptor
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.framework.elements.DayLabelArea
import com.eudycontreras.calendarheatmaplibrary.framework.elements.LegendArea
import com.eudycontreras.calendarheatmaplibrary.framework.elements.MonthLabelArea
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class CalHeatMapBuilder(
    private val shapeRenderer: ShapeRenderer,
    private var styleContext: () -> HeatMapStyle,
    private var optionsContext: () -> HeatMapOptions,
    private var contextProvider: (() -> Context)?
) {

    private var calHeatMapData: HeatMapData = generatePlaceholderData()

    fun getData(): HeatMapData? = calHeatMapData

    fun buildWithBounds(bounds: Bounds) {
        val style = styleContext.invoke()
        val options = optionsContext.invoke()
        val context = contextProvider?.invoke()

        val data = calHeatMapData

        val cellSize  = data.cellSize
        val gapSize = data.cellGap ?: if (cellSize != null) { cellSize * HeatMapData.CELL_SIZE_RATIO } else {
            (bounds.height / TimeSpan.MAX_DAYS) * HeatMapData.CELL_SIZE_RATIO
        }

        val legendArea = buildLegendArea(options, bounds)
        val dayLabelArea = buildDayLabelArea(options, bounds)
        val monthLabelArea = buildMonthLabelArea(options, bounds)

        val legendAreaHeight = legendArea?.bounds?.height ?: 0f
        val monthAreaHeight = monthLabelArea?.bounds?.height ?: 0f
        val dayAreaWidth = dayLabelArea?.bounds?.width ?: 0f

        val gapRatio = (gapSize * TimeSpan.MAX_DAYS)
        var matrixHeight = bounds.height

        matrixHeight -= legendAreaHeight
        matrixHeight -= monthAreaHeight

        val size = data.cellSize ?: ((matrixHeight  - gapRatio) / TimeSpan.MAX_DAYS)

        legendArea?.buildWith(size, gapSize, style)
        dayLabelArea?.buildWith(size, gapSize, monthAreaHeight, style)

        var horizontalOffset = (gapSize + bounds.left) + dayAreaWidth

        val cellShapes = mutableListOf<DrawableRectangle>()
        val monthIndexes = SparseArray< HeatMapLabel>()

        val interceptor: CellInterceptor = buildInterceptor(options, bounds, legendArea, style, gapSize)

        for ((index, week) in data.timeSpan.weeks.withIndex()) {
            var verticalOffset = monthAreaHeight
            for(day in week.weekDays) {
                val shape = DrawableRectangle()
                shape.touchHandler = { consumer: TouchConsumer, event: MotionEvent, x: Float, y: Float ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            if (consumer is DrawableRectangle) {
                                if (consumer.bounds.isInside(x, y)) {
                                   if (!consumer.hovered) {
                                       consumer.applyHighlight()
                                       consumer.hovered = true
                                   }
                                } else {
                                    if (consumer.hovered) {
                                        consumer.removeHighlight()
                                        consumer.hovered = false
                                    }
                                }
                            }
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_BUTTON_RELEASE, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL -> {
                            if (consumer is DrawableRectangle) {
                                consumer.removeHighlight()
                            }
                        }
                    }
                }
                shape.bounds = Bounds(horizontalOffset, verticalOffset, horizontalOffset + size, verticalOffset + size)
                shape.color = MutableColor(day.getColorValue(style))
                verticalOffset += (size + gapSize)
                cellShapes.add(shape)
            }
            val label = options.monthLabels[week.getMonthLabel()]
            if (index > 0) {
                val lastWeek = data.timeSpan.weeks[index - 1]
                if (!lastWeek.hasMonthLabel()) {
                    if (week.hasMonthLabel()) {
                        monthIndexes[index] = label
                    }
                } else {
                    if (week.weekDays[0].date.day == 1) {
                        monthIndexes[index] = label
                    }
                }
            } else {
                monthIndexes[index] = label
            }
            horizontalOffset += (size + gapSize)
        }

        monthLabelArea?.buildWith(size, gapSize, dayAreaWidth, monthIndexes, data.timeSpan.weeks, style)

        shapeRenderer.addShape(cellShapes)
        shapeRenderer.addShape(interceptor)
        shapeRenderer.addShape(monthLabelArea?.getShapes())
        shapeRenderer.addShape(dayLabelArea?.getShapes())
        shapeRenderer.addShape(legendArea?.getShapes())
    }

    private fun buildInterceptor(options: HeatMapOptions, bounds: Bounds, legendArea: LegendArea?, style: HeatMapStyle, gapSize: Float): CellInterceptor {

        val interceptor = CellInterceptor()
        interceptor.visible = true
        interceptor.lineColor =  MutableColor(AndroidColor.WHITE)
        interceptor.markerFillColor = MutableColor(style.minCellColor)
        interceptor.markerStrokeColor = MutableColor(AndroidColor.WHITE)
        interceptor.shiftOffsetX = 50.dp
        interceptor.shiftOffsetY = 50.dp
        interceptor.lineThickness = 2.dp
        interceptor.markerRadius = 12.dp
        interceptor.elevation = 4.dp
        interceptor.showHorizontalLine = true
        interceptor.showVerticalLine = true
        interceptor.build(bounds = bounds.copy(
            left = if (options.showDayLabels) { HeatMapOptions.DAY_LABEL_AREA_WIDTH + gapSize } else bounds.left + gapSize,
            right = bounds.right - (gapSize * 2),
            top = if (options.showMonthLabels) { HeatMapOptions.MONTH_LABEL_AREA_HEIGHT } else bounds.top,
            bottom = if (options.showLegend) { (legendArea?.bounds?.top ?: bounds.bottom) - gapSize } else bounds.bottom - gapSize
        ))
        return interceptor
    }

    private fun buildLegendArea(options: HeatMapOptions, bounds: Bounds): LegendArea? {
        if (options.showLegend) {
            return LegendArea(options, bounds.copy(
                left = if (options.showDayLabels) { HeatMapOptions.DAY_LABEL_AREA_WIDTH } else 0f,
                top = bounds.height - HeatMapOptions.LEGEND_AREA_HEIGHT
            ))
        }
        return null
    }

    private fun buildMonthLabelArea(options: HeatMapOptions, bounds: Bounds): MonthLabelArea? {
        if (options.showMonthLabels) {
            return MonthLabelArea(options, bounds.copy(
                left = if (options.showDayLabels) { HeatMapOptions.DAY_LABEL_AREA_WIDTH } else 0f,
                bottom = HeatMapOptions.MONTH_LABEL_AREA_HEIGHT
            ))
        }
        return null
    }

    private fun buildDayLabelArea(options: HeatMapOptions, bounds: Bounds): DayLabelArea? {
        if (options.showDayLabels) {
            return DayLabelArea(options, bounds.copy(
                top = if (options.showMonthLabels) { HeatMapOptions.MONTH_LABEL_AREA_HEIGHT } else 0f,
                right = HeatMapOptions.DAY_LABEL_AREA_WIDTH
            ))
        }
        return null
    }


    fun buildWithData(calHeatMapData: HeatMapData) {
        this.calHeatMapData = calHeatMapData
    }

    private fun generatePlaceholderData(): HeatMapData {
        val weeks: MutableList<Week> = mutableListOf()
        val months = HeatMapOptions.STANDARD_MONTH_LABELS.map { it.text }
        val daysInWeek = 7
        val weeksInYear = 52
        var yearCounter = 0
        var monthCounter = 0
        var weekCounter = 0
        var dayCounter = 0

        for (index in 0L..weeksInYear) {

            val days: MutableList<WeekDay> = mutableListOf()

            for (day in 0 until daysInWeek) {
                dayCounter++
                val monthIndex = if (weekCounter >= 3 && day > 5 && monthCounter < 10) { (monthCounter + 1) } else monthCounter
                days.add(
                    WeekDay(
                        index = day,
                        date = Date(dayCounter, Month(monthCounter, months[monthIndex]), yearCounter),
                        frequencyData = Frequency(count = 0, data = null)
                    )
                )
            }
            if (weekCounter < 3) {
                weekCounter ++
            } else {
                weekCounter = 0
                if (monthCounter < 11) {
                    monthCounter ++
                    dayCounter = 0
                } else {
                    monthCounter = 0
                    yearCounter ++
                }
            }

            weeks.add(Week(weekNumber = index.toInt(), weekDays = days))
        }

        return HeatMapData(
            options = HeatMapOptions(),
            timeSpan = TimeSpan(
                dateMin = Date(0, Month(0, ""), 0),
                dateMax = Date(0, Month(0, ""), 0),
                weeks = weeks
            )
        )
    }
}