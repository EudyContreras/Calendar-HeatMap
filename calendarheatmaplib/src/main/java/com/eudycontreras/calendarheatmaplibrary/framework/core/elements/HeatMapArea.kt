package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.*
import android.os.Build
import android.util.SparseArray
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.animation.OvershootInterpolator
import androidx.core.util.set
import com.eudycontreras.calendarheatmaplibrary.animations.MatrixRevealAnimation
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Text
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Dimension
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import kotlin.math.abs

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal class HeatMapArea(
    var cellInfoBubble: CellInfoBubble?,
    val viewportProvider: () -> Rect,
    val viewportArea: Dimension,
    val options: HeatMapOptions,
    val heatMap: CalHeatMap,
    val data: HeatMapData,
    val style: HeatMapStyle,
    val bounds: Bounds
) : RenderTarget, TouchConsumer {

    private var revealed: Boolean = false

    private var frontShape: DrawableShape? = null

    private var shapes: Array<Array<HeatMapCell>> = emptyArray()

    override var touchHandler: ((TouchConsumer, Int, Bounds, Float, Float) -> Unit)? = null

    private val touchConsumer: ((TouchConsumer, Int, WeekDay, Float, Float) -> Unit) = { consumer, eventAction: Int, day, x, y ->
        when (eventAction) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_BUTTON_PRESS -> {
                if (consumer is HeatMapCell) {
                    if (eventAction == MotionEvent.ACTION_BUTTON_PRESS) {
                        if (!consumer.hovered && !consumer.isHighlighting) {
                            cellInfoBubble?.revealInfoBubble()
                        }
                    }
                    if (consumer.bounds.isInside(x, y)) {
                        if (!consumer.hovered && !consumer.isHighlighting) {

                            consumer.hovered = true
                            frontShape = consumer
                            cellInfoBubble?.setInterceptedData(day)
                            if (eventAction != MotionEvent.ACTION_BUTTON_PRESS) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                                    heatMap.hapticFeeback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
                                } else {
                                    heatMap.hapticFeeback(HapticFeedbackConstants.CLOCK_TICK)
                                }
                                cellInfoBubble?.bringToFront()
                            }
                            heatMap.addAnimation(consumer.applyHighlight(options.cellHighlightDuration))
                        }
                    } else {
                        if (consumer.hovered) {
                            consumer.hovered = false
                            heatMap.addAnimation(consumer.removeHighlight(options.cellHighlightDuration))
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_BUTTON_RELEASE, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL -> {
                if (consumer is HeatMapCell) {
                    if (consumer.hovered) {
                        heatMap.addAnimation(consumer.removeHighlight(options.cellHighlightDuration))
                        consumer.hovered = false
                    }
                }
            }
        }
    }

    private var revealAnimation: MatrixRevealAnimation<HeatMapCell>? =
        options.matrixRevealAnimation?.let {
            MatrixRevealAnimation<HeatMapCell>().apply {
                delay = it.delay
                duration = it.duration
                stagger = it.stagger
                fromIndex = it.epiCenter
                interpolator = OvershootInterpolator()
            }
        }

    private fun animateReveal() {
        val viewport = viewportProvider().let {
            Bounds(
                left = abs(it.left).toFloat() + bounds.left,
                right = abs(it.left).toFloat() + bounds.left + viewportArea.width,
                top = bounds.top,
                bottom = bounds.bottom
            )
        }
        val shapes = shapes.filter {
            it.any { col -> col.isInViewport(viewport) }
        }.toTypedArray()
        if (shapes.isEmpty())
            return

        if (!revealed) {
            revealed = true
            revealAnimation?.animate(heatMap, shapes)
        }
    }

    private fun setUpAnimation() {
        heatMap.onFullyVisible = { _, visible ->
            if (visible) {
                animateReveal()
            }
        }
    }

    fun buildWith(
        measurements: Measurements,
        monthIndexes: SparseArray<HeatMapLabel>,
        monthLabels: List<HeatMapLabel>
    ): HeatMapArea {
        val offset = measurements.cellGap
        val cellSize = measurements.cellSize

        var renderIndex = 0
        var horizontalOffset = (offset + bounds.left)

        shapes = Array(data.getColumnCount()) {
            Array(data.getRowCount()) { HeatMapCell() }
        }
        val viewport = viewportProvider().let {
            Bounds(
                left = abs(it.left).toFloat() + bounds.left,
                right = abs(it.left).toFloat() + bounds.left + viewportArea.width,
                top = bounds.top,
                bottom = bounds.bottom
            )
        }
        for ((rowIndex, week) in data.timeSpan.weeks.withIndex()) {
            var verticalOffset = bounds.top
            val rows: Array<HeatMapCell> = shapes[rowIndex]

            for ((colIndex, day) in week.weekDays.withIndex()) {
                val shape = HeatMapCell()
                shape.touchHandler = { touchConsumer, action, _, x, y ->
                    touchConsumer(touchConsumer, action, day, x, y)
                }
                shape.renderIndex = renderIndex
                shape.bounds = Bounds(
                    left = horizontalOffset,
                    top = verticalOffset,
                    right = horizontalOffset + cellSize,
                    bottom = verticalOffset + cellSize
                )
                shape.color = MutableColor(day.getColorValue(style, options.maxFrequencyValue))
                shape.cellText = createCellText(shape, day, cellSize)
                shape.render = !shape.bounds.intercepts(viewport)
                shape.elevation = style.cellElevation
                verticalOffset += (cellSize + offset)
                renderIndex++
                rows[colIndex] = shape
            }
            shapes[rowIndex] = rows

            if (options.showMonthLabels) {
                getMonthLabels(week, rowIndex, monthLabels, monthIndexes)
            }
            horizontalOffset += (cellSize + offset)
        }

        setUpAnimation()
        return this
    }

    private fun getMonthLabels(
        week: Week,
        rowIndex: Int,
        monthLabels: List<HeatMapLabel>,
        monthIndexes: SparseArray<HeatMapLabel>
    ) {
        val label = monthLabels[week.getMonthLabel()]

        if (rowIndex > 0) {
            val lastWeek = data.timeSpan.weeks[rowIndex - 1]
            if (!lastWeek.hasMonthLabel(monthLabels)) {
                if (week.hasMonthLabel(monthLabels)) {
                    monthIndexes[rowIndex] = label
                }
            } else {
                if (week.weekDays[0].date.day == 1) {
                    monthIndexes[rowIndex] = label
                }
            }
        } else {
            if (week.hasMonthLabel(monthLabels)) {
                monthIndexes[rowIndex] = label
            }
        }
    }

    private fun createCellText(
        shape: HeatMapCell,
        day: WeekDay,
        cellSize: Float
    ): Text? {
        if (options.showCellDayText) {
            return Text(day.date.day.toString(), Paint()).apply {
                textSize = (cellSize / 2f)
                typeFace = Typeface.DEFAULT_BOLD
            }.build().apply {
                x = shape.bounds.centerX
                y = shape.bounds.centerY + (height / 2)
                alignment = Alignment.CENTER
                textColor = if (shape.color.isBright(TEXT_COLOR_BRIGHT_THRESHOLD)) {
                    shape.color.adjust(TEXT_COLOR_DARKEN_OFFSET)
                } else {
                    shape.color.adjust(TEXT_COLOR_BRIGHTEN_OFFSET)
                }
            }
        }
        return null
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        for (cols in shapes) {
            for (shape in cols) {
                if (shape != frontShape) {
                    shape.onRender(canvas, paint, shapePath, shadowPath)
                }
            }
        }
        frontShape?.onRender(canvas, paint, shapePath, shadowPath)
    }

    override fun onTouch(event: Int, bounds: Bounds, x: Float, y: Float) {
        for (cols in shapes) {
            for (shape in cols) {
                shape.onTouch(event, bounds, x, y)
            }
        }
    }

    private companion object {
        const val TEXT_COLOR_BRIGHT_THRESHOLD = 200
        const val TEXT_COLOR_DARKEN_OFFSET = 0.8f
        const val TEXT_COLOR_BRIGHTEN_OFFSET = 1.5f
    }
}