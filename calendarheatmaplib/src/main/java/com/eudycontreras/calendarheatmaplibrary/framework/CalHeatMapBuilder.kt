package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.util.SparseArray
import android.view.MotionEvent
import androidx.core.util.set
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.*
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.CellInterceptor
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.DayLabelArea
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.HeatMapArea
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.LegendArea
import com.eudycontreras.calendarheatmaplibrary.framework.core.elements.MonthLabelArea
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.getTextMeasurement
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import kotlin.math.max

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

/**
 * TODO list:
 * - Remove hard code dimensions for the different areas. Base the dimensions
 * on text sizes
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

    fun buildWithBounds(bounds: Bounds) {
        val style = styleContext.invoke()
        val options = optionsContext.invoke()

        val data = calHeatMapData

        val cellSize  = data.cellSize
        val gapSize = data.cellGap ?: if (cellSize != null) { cellSize * HeatMapData.CELL_SIZE_RATIO } else {
            (bounds.height / TimeSpan.MAX_DAYS) * HeatMapData.CELL_SIZE_RATIO
        }

        val legendArea = buildLegendArea(options, style, bounds, gapSize)?.buildWith(gapSize)
        val dayLabelArea = buildDayLabelArea(options, style, bounds, gapSize)
        val monthLabelArea = buildMonthLabelArea(options, style, bounds)

        val legendAreaHeight = legendArea?.bounds?.height ?: 0f
        val monthAreaHeight = monthLabelArea?.bounds?.height ?: 0f
        val dayAreaWidth = dayLabelArea?.bounds?.width ?: 0f

        val gapRatio = (gapSize * TimeSpan.MAX_DAYS)
        val size = data.cellSize ?: ((bounds.height - (legendAreaHeight + monthAreaHeight) - gapRatio) / TimeSpan.MAX_DAYS)

        dayLabelArea?.buildWith(size, gapSize, monthAreaHeight)

        val interceptor: CellInterceptor = buildInterceptor(options, bounds, legendArea, style, gapSize, size)

        val monthIndexes = SparseArray<HeatMapLabel>()

        val heatMapArea = buildHeatMapArea(options, style, bounds,
            paddingLeft = dayAreaWidth,
            paddingTop = monthAreaHeight,
            paddingBottom = legendAreaHeight
        ).buildWith(
            cellSize = size,
            offset = gapSize,
            monthIndexes = monthIndexes
        )

        monthLabelArea?.buildWith(size, gapSize, dayAreaWidth, monthIndexes, data.timeSpan.weeks)

        shapeRenderer.addShape(heatMapArea)
        shapeRenderer.addShape(monthLabelArea)
        shapeRenderer.addShape(dayLabelArea)
        shapeRenderer.addShape(legendArea)
        shapeRenderer.addShape(interceptor)
    }

    private fun buildInterceptor(options: HeatMapOptions, bounds: Bounds, legendArea: LegendArea?, style: HeatMapStyle, gapSize: Float, cellSize: Float): CellInterceptor {
        val interceptor = CellInterceptor(
            markerRadius = cellSize / 2,
            lineThickness = 2.dp
        )
        interceptor.visible = true
        interceptor.lineColor =  MutableColor(AndroidColor.WHITE)
        interceptor.markerFillColor = MutableColor(style.minCellColor)
        interceptor.markerStrokeColor = MutableColor(AndroidColor.WHITE)
        interceptor.shiftOffsetX = options.interceptorOffsetX
        interceptor.shiftOffsetY = options.interceptorOffsetY
        interceptor.elevation = 3.dp
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

    private fun buildHeatMapArea(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds, paddingLeft: Float, paddingTop: Float, paddingBottom: Float): HeatMapArea {
        return HeatMapArea(calHeatMapData, options, style, bounds.copy(
            left = bounds.left + paddingLeft,
            top = bounds.top + paddingTop,
            right = bounds.right,
            bottom = bounds.bottom - paddingBottom
        ))
    }

    private fun buildLegendArea(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds, gapSize: Float): LegendArea? {
        val legendAreaTextHeight = if (options.showLegend) {
            val textMeasurement = getTextMeasurement(
                paint = shapeRenderer.paint,
                text = options.legendLessLabel,
                textSize = style.legendLabelStyle.textSize,
                typeFace = style.legendLabelStyle.typeFace
            )
            textMeasurement.height() + gapSize
        } else MIN_OFFSET

        val legendAreaHeight = if (options.showLegend) {
            max(HeatMapOptions.LEGEND_AREA_HEIGHT, legendAreaTextHeight + (gapSize * 2))
        } else MIN_OFFSET

        if (options.showLegend) {
            return LegendArea(options, style, bounds.copy(
                left = if (options.showDayLabels) { HeatMapOptions.DAY_LABEL_AREA_WIDTH } else 0f,
                top = bounds.height - legendAreaHeight
            ))
        }
        return null
    }

    private fun buildDayLabelArea(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds, gapSize: Float): DayLabelArea? {
        val dayLabelAreaTextHeight = if (options.showLegend) {
            val textMeasurement = getTextMeasurement(
                paint = shapeRenderer.paint,
                text = options.dayLabels.map { it.text }.maxBy { it.length },
                textSize = style.dayLabelStyle.textSize,
                typeFace = style.dayLabelStyle.typeFace
            )
            textMeasurement.height() + gapSize
        } else MIN_OFFSET

        val dayAreaWidth = if (options.showDayLabels) {
            max(HeatMapOptions.DAY_LABEL_AREA_WIDTH, dayLabelAreaTextHeight + (gapSize * 2))
        } else MIN_OFFSET

        if (options.showDayLabels) {
            return DayLabelArea(options, style, bounds.copy(
                top = if (options.showMonthLabels) { HeatMapOptions.MONTH_LABEL_AREA_HEIGHT } else 0f,
                right = dayAreaWidth
            ))
        }
        return null
    }

    private fun buildMonthLabelArea(options: HeatMapOptions, style: HeatMapStyle, bounds: Bounds): MonthLabelArea? {
        if (options.showMonthLabels) {
            return MonthLabelArea(options, style, bounds.copy(
                left = if (options.showDayLabels) { HeatMapOptions.DAY_LABEL_AREA_WIDTH } else 0f,
                bottom = HeatMapOptions.MONTH_LABEL_AREA_HEIGHT
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