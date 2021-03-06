package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.*
import android.os.Build
import android.util.SparseArray
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.animation.OvershootInterpolator
import androidx.core.util.set
import com.eudycontreras.calendarheatmaplibrary.Action
import com.eudycontreras.calendarheatmaplibrary.animations.MatrixRevealAnimation
import com.eudycontreras.calendarheatmaplibrary.common.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.extensions.toMutableColor
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

    override var touchHandler: ((TouchConsumer, Int, Bounds, Float, Float, Float, Float, Float, Float) -> Unit)? = null

    private val touchConsumer: ((TouchConsumer, Int, WeekDay, Float, Float, Float, Float, Float, Float) -> Unit) = { consumer, eventAction: Int, day, x, y, minX, maxX, _, _ ->
        when (eventAction) {
            MotionEvent.ACTION_BUTTON_PRESS -> {
                onInteraction(consumer, x, y, onIn = {
                    cellInfoBubble?.setInterceptedData(day)
                    cellInfoBubble?.revealInfoBubble(x, y, minX, maxX)
                })
            }
            MotionEvent.ACTION_MOVE -> {
                onInteraction(consumer, x, y, onIn = {
                    cellInfoBubble?.setInterceptedData(day)
                    if (cellInfoBubble?.isRevealed != true) {
                        addFeedback()
                        cellInfoBubble?.revealInfoBubble(x, y, minX, maxX)
                    } else {
                        addFeedback()
                        cellInfoBubble?.bringToFront()
                    }
                })
            }
            else -> {
                if (consumer is HeatMapCell) {
                    if (consumer.hovered) {
                        heatMap.addAnimation(consumer.removeHighlight(options.cellHighlightDuration))
                        consumer.hovered = false
                    }
                }
                cellInfoBubble?.concealInfoBubble(x, y,  minX, maxX)
            }
        }
    }

    private fun onInteraction(consumer: TouchConsumer, x: Float, y: Float, onIn: Action? = null, onOut: Action? = null) {
        if (consumer is HeatMapCell) {
            if (consumer.bounds.isInside(x, y)) {
                if (!consumer.hovered && !consumer.isHighlighting) {
                    consumer.hovered = true
                    frontShape = consumer
                    heatMap.addAnimation(consumer.applyHighlight(options.cellHighlightDuration))
                    onIn?.invoke()
                }
            } else {
                if (consumer.hovered) {
                    consumer.hovered = false
                    heatMap.addAnimation(consumer.removeHighlight(options.cellHighlightDuration))
                    onOut?.invoke()
                }
            }
        }
    }

    private fun addFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            heatMap.hapticFeeback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
        } else {
            heatMap.hapticFeeback(HapticFeedbackConstants.CLOCK_TICK)
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
        heatMap.onFullyVisible = {
            animateReveal()
        }
    }

    fun buildWith(
        measurements: Measurements,
        monthIndexes: SparseArray<HeatMapLabel>,
        monthLabels: List<HeatMapLabel>
    ): HeatMapArea {
        val offset = measurements.cellGap
        val cellSize = measurements.cellSize
        val hasMonthLabels = options.showMonthLabels && options.monthLabels.any { it.active }

        var horizontalOffset = (offset + bounds.left)

        shapes = Array(data.getColumnCount()) {
            Array(data.getRowCount()) { HeatMapCell(measurements.cellGap) }
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
                val shape = HeatMapCell(measurements.cellGap)
                shape.touchHandler = { touchConsumer, action, _, x, y, minX, maxX, minY, maxY ->
                    touchConsumer(touchConsumer, action, day, x, y, minX, maxX, minY, maxY)
                }
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
                rows[colIndex] = shape
            }
            shapes[rowIndex] = rows

            if (hasMonthLabels) {
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
        if (options.showCellDayText && options.dayLabels.any { it.active }) {
            return Text(day.date.day.toString(), Paint()).apply {
                textSize = (cellSize / 2f)
                typeFace = Typeface.DEFAULT_BOLD
            }.build().apply {
                x = shape.bounds.centerX
                y = shape.bounds.centerY + (height / 2)
                alignment = Alignment.CENTER
                typeFace = style.cellTypeFace
                textColor = style.cellTextColor?.toMutableColor() ?: shape.getAdjustedColor(shape.color)
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

    override fun onTouch(eventAction: Int, bounds: Bounds, x: Float, y: Float, minX: Float, maxX: Float, minY: Float, maxY: Float) {
        for (cols in shapes) {
            for (shape in cols) {
                shape.onTouch(eventAction, bounds, x, y, minX, maxX, minY, maxY)
            }
        }
    }
}