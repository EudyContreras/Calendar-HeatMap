package com.eudycontreras.calendarheatmaplibrary.framework

import android.graphics.Rect
import android.util.SparseArray
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeManager
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.*
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Dimension
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

/**
 * TODO list:
 * - Refactor code for better standards
 * - Put relevant styling and settings data inside the
 * data wrappers for easier user customization.
 */
internal class CalHeatMapBuilder(
    private val shapeManager: ShapeManager,
    private var styleContext: () -> HeatMapStyle,
    private var optionsContext: () -> HeatMapOptions,
    private var viewportProvider: () -> Rect
) {
    private var calHeatMapData: HeatMapData = generatePlaceholderData()

    fun getData(): HeatMapData? = calHeatMapData

    fun buildWithBounds(
        heatMap: CalHeatMap,
        bounds: Bounds,
        measurements: Measurements,
        bubbleLayout: BubbleLayout<WeekDay>?
    ) {
        val data = calHeatMapData
        val style = styleContext.invoke()
        val options = optionsContext.invoke()

        val legendArea = buildLegendArea(options, style, bounds, measurements)
        val dayLabelArea = buildDayLabelArea(options, style, bounds, measurements)
        val monthLabelArea = buildMonthLabelArea(options, style, bounds, measurements)
        val cellInfoBubble: CellInfoBubble? = buildCellBubble(bounds, bubbleLayout, measurements)

        val monthIndexes = SparseArray<HeatMapLabel>()

        val heatMapArea = buildHeatMapArea(
            options, heatMap, style, bounds,
            paddingLeft = dayLabelArea?.bounds?.width ?: MIN_OFFSET,
            paddingTop = monthLabelArea?.bounds?.height ?: MIN_OFFSET,
            paddingBottom = legendArea?.bounds?.height ?: MIN_OFFSET,
            viewportArea = Dimension(
                width = measurements.viewportWidth,
                height = measurements.viewportHeight
            ),
            cellInfoBubble = cellInfoBubble
        ).buildWith(
            measurements = measurements,
            monthIndexes = monthIndexes,
            monthLabels = options.monthLabels
        )

        val interceptor: CellInterceptor = buildInterceptor(options, style, heatMapArea.bounds, measurements, cellInfoBubble)

        legendArea?.buildWith(measurements, options)
        dayLabelArea?.buildWith(measurements, options.dayLabels)
        monthLabelArea?.buildWith(measurements, monthIndexes, data.timeSpan.weeks)

        shapeManager.addShape(heatMapArea)
        shapeManager.addShape(monthLabelArea)
        shapeManager.addShape(dayLabelArea)
        shapeManager.addShape(legendArea)
        shapeManager.addShape(interceptor)
        shapeManager.addShape(cellInfoBubble)
    }

    private fun buildInterceptor(
        options: HeatMapOptions,
        style: HeatMapStyle,
        bounds: Bounds,
        measurements: Measurements,
        cellInfoBubble: CellInfoBubble?
    ): CellInterceptor {
        val gapSize: Float = measurements.cellGap
        val cellSize: Float = measurements.cellSize

        val interceptor = CellInterceptor(
            infoBubble = cellInfoBubble,
            markerRadius = cellSize / 3,
            lineThickness = style.interceptorLineThickness
        ).apply {
            visible = true
            showTopLine = false
            viewPort = Dimension(measurements.viewportWidth, measurements.viewportHeight)
            lineColor = MutableColor(AndroidColor.WHITE)
            markerFillColor = MutableColor(style.minCellColor)
            markerStrokeColor = MutableColor(AndroidColor.WHITE)
            shiftOffsetX = options.interceptorOffsetX
            shiftOffsetY = options.interceptorOffsetY
            elevation = style.interceptorElevation
        }
        interceptor.build(
            bounds = bounds.copy(
                left = bounds.left + gapSize,
                right = bounds.right - gapSize,
                bottom = bounds.bottom - gapSize
            )
        )
        return interceptor
    }

    private fun buildCellBubble(
        bounds: Bounds,
        bubbleLayout: BubbleLayout<WeekDay>?,
        measurements: Measurements
    ): CellInfoBubble? {
        if (bubbleLayout != null) {
            val offset = measurements.cellGap * 2
            return CellInfoBubble(
                bounds = bounds,
                topOffset = measurements.cellSize + offset,
                sideOffset = measurements.cellGap,
                measuremendts = measurements,
                bubbleLayout = bubbleLayout
            )
        }
        return null
    }

    private fun buildHeatMapArea(
        options: HeatMapOptions,
        heatMap: CalHeatMap,
        style: HeatMapStyle,
        bounds: Bounds,
        paddingLeft: Float,
        paddingTop: Float,
        paddingBottom: Float,
        viewportArea: Dimension,
        cellInfoBubble: CellInfoBubble?
    ): HeatMapArea {
        return HeatMapArea(
            cellInfoBubble,
            viewportProvider, viewportArea, options, heatMap, calHeatMapData, style, bounds.copy(
                left = bounds.left + paddingLeft,
                top = bounds.top + paddingTop,
                right = bounds.right,
                bottom = bounds.bottom - paddingBottom
            )
        )
    }

    private fun buildLegendArea(
        options: HeatMapOptions,
        style: HeatMapStyle,
        bounds: Bounds,
        measurements: Measurements
    ): LegendArea? {
        if (options.showLegend) {
            return LegendArea(
                style, bounds.copy(
                    left = if (options.showDayLabels) {
                        measurements.dayLabelAreaWidth
                    } else MIN_OFFSET,
                    top = bounds.height - measurements.legendAreaHeight
                )
            )
        }
        return null
    }

    private fun buildDayLabelArea(
        options: HeatMapOptions,
        style: HeatMapStyle,
        bounds: Bounds,
        measurements: Measurements
    ): DayLabelArea? {
        if (options.showDayLabels) {
            return DayLabelArea(
                style, bounds.copy(
                    left = bounds.left,
                    top = if (options.showMonthLabels) {
                        measurements.monthLabelAreaHeight
                    } else MIN_OFFSET,
                    right = measurements.dayLabelAreaWidth
                )
            )
        }
        return null
    }

    private fun buildMonthLabelArea(
        options: HeatMapOptions,
        style: HeatMapStyle,
        bounds: Bounds,
        measurements: Measurements
    ): MonthLabelArea? {
        if (options.showMonthLabels) {
            return MonthLabelArea(
                style, bounds.copy(
                    left = if (options.showDayLabels) {
                        measurements.dayLabelAreaWidth
                    } else MIN_OFFSET,
                    bottom = measurements.monthLabelAreaHeight
                )
            )
        }
        return null
    }

    fun buildWithData(calHeatMapData: HeatMapData) {
        this.calHeatMapData = calHeatMapData
    }

    private fun generatePlaceholderData(): HeatMapData {
        val weeks: MutableList<Week> = mutableListOf()
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
                val monthIndex = if (weekCounter >= 3 && day > 5 && monthCounter < 10) {
                    (monthCounter + 1)
                } else monthCounter
                days.add(
                    WeekDay(
                        index = day,
                        date = Date(dayCounter, monthIndex, yearCounter),
                        dateString = "$dayCounter-$monthIndex-$yearCounter",
                        frequencyData = Frequency(count = 0, data = null)
                    )
                )
            }
            if (weekCounter < 3) {
                weekCounter++
            } else {
                weekCounter = 0
                if (monthCounter < 11) {
                    monthCounter++
                    dayCounter = 0
                } else {
                    monthCounter = 0
                    yearCounter++
                }
            }
            weeks.add(Week(weekNumber = index.toInt(), weekDays = days))
        }

        return HeatMapData(
            options = HeatMapOptions(),
            timeSpan = TimeSpan(
                dateMin = Date(0, 0, 0),
                dateMax = Date(0, 0, 0),
                weeks = weeks
            )
        )
    }
}