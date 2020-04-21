package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.util.SparseArray
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.*
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
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
 * - Take overlay input in order to render the tooltip information. Or better yet
 * allow the user to specify a layout for the tooltip. The layout should take the frequency data
 * draw the the given layout inside of the tooltip somehow.
 * - Animate the hover trace somehow. Apply an animation on enter and exit from a cell.
 * - Animate the showing of the cells using propagation animations provided by the framework
 * - Allow panning from side to side. Calculate panning shift base on the motion event position
 * and the offset to each end of the spectrum.
 * - Interpolate interceptor center base on motion and
 * - Allow showing the days for the cells
 */
internal class CalHeatMapBuilder(
    private val shapeRenderer: ShapeRenderer,
    private var styleContext: () -> HeatMapStyle,
    private var optionsContext: () -> HeatMapOptions,
    private var contextProvider: (() -> Context)?
) {
    private var calHeatMapData: HeatMapData = generatePlaceholderData()

    fun getData(): HeatMapData? = calHeatMapData

    fun buildWithBounds(bounds: Bounds, measurements: Measurements) {
        val data = calHeatMapData
        val style = styleContext.invoke()
        val options = optionsContext.invoke()

        val legendArea = buildLegendArea(options, style, bounds, measurements)
        val dayLabelArea = buildDayLabelArea(options, style, bounds, measurements)
        val monthLabelArea = buildMonthLabelArea(options, style, bounds, measurements)

        val monthIndexes = SparseArray<HeatMapLabel>()

        val heatMapArea = buildHeatMapArea(style, bounds,
            paddingLeft = dayLabelArea?.bounds?.width ?: MIN_OFFSET,
            paddingTop = monthLabelArea?.bounds?.height ?: MIN_OFFSET,
            paddingBottom = legendArea?.bounds?.height ?: MIN_OFFSET
        ).buildWith(
            measurements = measurements,
            monthIndexes = monthIndexes,
            monthLabels = options.monthLabels
        )

        val interceptor: CellInterceptor = buildInterceptor(options, style, heatMapArea.bounds, measurements)

        legendArea?.buildWith(measurements, options)
        dayLabelArea?.buildWith(measurements, options.dayLabels)
        monthLabelArea?.buildWith(measurements, monthIndexes, data.timeSpan.weeks)

        shapeRenderer.addShape(heatMapArea)
        shapeRenderer.addShape(monthLabelArea)
        shapeRenderer.addShape(dayLabelArea)
        shapeRenderer.addShape(legendArea)
        shapeRenderer.addShape(interceptor)
    }

    private fun buildInterceptor(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds, measurements: Measurements): CellInterceptor {
        val gapSize: Float = measurements.cellGap
        val cellSize: Float = measurements.cellSize

        val interceptor = CellInterceptor(
            markerRadius = cellSize / 2,
            lineThickness = style.interceptorLineThickness
        )
        interceptor.visible = true
        interceptor.lineColor =  MutableColor(AndroidColor.WHITE)
        interceptor.markerFillColor = MutableColor(style.minCellColor)
        interceptor.markerStrokeColor = MutableColor(AndroidColor.WHITE)
        interceptor.shiftOffsetX = options.interceptorOffsetX
        interceptor.shiftOffsetY = options.interceptorOffsetY
        interceptor.elevation = style.interceptorElevation
        interceptor.showHorizontalLine = true
        interceptor.showVerticalLine = true
        interceptor.build(bounds = bounds.copy(
            left = bounds.left + gapSize,
            right = bounds.right - gapSize,
            bottom = bounds.bottom - gapSize
        ))
        return interceptor
    }

    private fun buildHeatMapArea(style: HeatMapStyle, bounds: Bounds, paddingLeft: Float, paddingTop: Float, paddingBottom: Float): HeatMapArea {
        return HeatMapArea(calHeatMapData, style, bounds.copy(
            left = bounds.left + paddingLeft,
            top = bounds.top + paddingTop,
            right = bounds.right,
            bottom = bounds.bottom - paddingBottom
        ))
    }

    private fun buildLegendArea(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds, measurements: Measurements): LegendArea? {
        if (options.showLegend) {
            return LegendArea(style, bounds.copy(
                left = if (options.showDayLabels) {
                    measurements.dayLabelAreaWidth
                } else MIN_OFFSET,
                top = bounds.height - measurements.legendAreaHeight
            ))
        }
        return null
    }

    private fun buildDayLabelArea(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds, measurements: Measurements): DayLabelArea? {
        if (options.showDayLabels) {
            return DayLabelArea(style, bounds.copy(
                left = bounds.left,
                top = if (options.showMonthLabels) {
                    measurements.monthLabelAreaHeight
                } else MIN_OFFSET,
                right = measurements.dayLabelAreaWidth
            ))
        }
        return null
    }

    private fun buildMonthLabelArea(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds, measurements: Measurements): MonthLabelArea? {
        if (options.showMonthLabels) {
            return MonthLabelArea(style, bounds.copy(
                left = if (options.showDayLabels) {
                    measurements.dayLabelAreaWidth
                } else MIN_OFFSET,
                bottom = measurements.monthLabelAreaHeight
            ))
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
                val monthIndex = if (weekCounter >= 3 && day > 5 && monthCounter < 10) { (monthCounter + 1) } else monthCounter
                days.add(
                    WeekDay(
                        index = day,
                        date = Date(dayCounter, monthIndex, yearCounter),
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
                dateMin = Date(0,0, 0),
                dateMax = Date(0,0, 0),
                weeks = weeks
            )
        )
    }
}