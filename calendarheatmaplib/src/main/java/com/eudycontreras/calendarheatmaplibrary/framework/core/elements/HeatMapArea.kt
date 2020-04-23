package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.util.set
import com.eudycontreras.calendarheatmaplibrary.animations.AnimationEvent
import com.eudycontreras.calendarheatmaplibrary.animations.MatrixRevealAnimation
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Index
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class HeatMapArea (
    options: HeatMapOptions,
    val heatMap: CalHeatMap,
    val data: HeatMapData,
    val style: HeatMapStyle,
    val bounds: Bounds
): RenderTarget, TouchConsumer {

    private var revealed: Boolean = false

    private val highlightInterpolator = DecelerateInterpolator()

    private var shapes: Array<Array<DrawableRectangle>> = emptyArray()

    override var touchHandler: ((TouchConsumer, MotionEvent, Bounds, Float, Float) -> Unit)? = null

    private var revealAnimation: MatrixRevealAnimation<DrawableRectangle>? = options.matrixRevealAnimation?.let {
        MatrixRevealAnimation<DrawableRectangle>().apply {
            delay = it.delay
            duration = it.duration
            stagger = it.stagger
            fromIndex = Index(0, 0)
            interpolator = OvershootInterpolator()
        }
    }

    private fun animateReveal() {
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

    fun buildWith(measurements: Measurements, monthIndexes: SparseArray<HeatMapLabel>, monthLabels: List<HeatMapLabel>): HeatMapArea {
        val offset = measurements.cellGap
        val cellSize = measurements.cellSize

        var horizontalOffset = (offset + bounds.left)

        shapes = Array(data.getColumnCount()) {
            Array(data.getRowCount()) {
                DrawableRectangle()
            }
        }
        for ((index, week) in data.timeSpan.weeks.withIndex()) {
            var verticalOffset = bounds.top
            val rows: Array<DrawableRectangle> = Array(data.getRowCount()) {
                DrawableRectangle()
            }
            for((rowIndex, day) in week.weekDays.withIndex()) {
                val shape = DrawableRectangle()
                shape.touchHandler = { consumer: TouchConsumer, event: MotionEvent, bounds: Bounds, x: Float, y: Float ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            if (consumer is DrawableRectangle) {
                                if (consumer.bounds.isInside(x, y)) {
                                    if (!consumer.hovered) {
                                        consumer.hovered = true
                                        heatMap.addAnimation(consumer.applyHighlight(highlightInterpolator))
                                    }
                                } else {
                                    if (consumer.hovered) {
                                        consumer.hovered = false
                                        heatMap.addAnimation(consumer.removeHighlight(highlightInterpolator))
                                    }
                                }
                            }
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_BUTTON_RELEASE, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL -> {
                            if (consumer is DrawableRectangle) {
                                if (consumer.hovered) {
                                    heatMap.addAnimation(consumer.removeHighlight(highlightInterpolator))
                                    consumer.hovered = false
                                }
                            }
                        }
                    }
                }
                shape.bounds = Bounds(horizontalOffset, verticalOffset, horizontalOffset + cellSize, verticalOffset + cellSize)
                shape.color = MutableColor(day.getColorValue(style))
                shape.render = false
                shape.elevation = style.cellElevation
                verticalOffset += (cellSize + offset)
                rows[rowIndex] = shape
            }
            shapes[index] = rows
            val label = monthLabels[week.getMonthLabel()]

            if (index > 0) {
                val lastWeek = data.timeSpan.weeks[index - 1]
                if (!lastWeek.hasMonthLabel(monthLabels)) {
                    if (week.hasMonthLabel(monthLabels)) {
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
            horizontalOffset += (cellSize + offset)
        }

        setUpAnimation()
        return this
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        for (cols in shapes) {
            for (shape in cols) {
                shape.onRender(canvas, paint, shapePath, shadowPath)
            }
        }
    }

    override fun onTouch(event: MotionEvent, bounds: Bounds, x: Float, y: Float) {
        for (cols in shapes) {
            for (shape in cols) {
                shape.onTouch(event, bounds, x, y)
            }
        }
    }
}