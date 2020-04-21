package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.SparseArray
import android.view.MotionEvent
import androidx.core.util.set
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableRectangle
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

internal class HeatMapArea (
    val data: HeatMapData,
    val options: HeatMapOptions,
    val style: HeatMapStyle,
    val bounds: Bounds
): RenderTarget, TouchConsumer {

    private val shapes: MutableList<DrawableShape> = mutableListOf()

    override var touchHandler: ((TouchConsumer, MotionEvent, Float, Float) -> Unit)? = null

    fun buildWith(cellSize: Float, offset: Float, monthIndexes: SparseArray<HeatMapLabel>): HeatMapArea {
        var horizontalOffset = (offset + bounds.left)

        for ((index, week) in data.timeSpan.weeks.withIndex()) {
            var verticalOffset = bounds.top
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
                shape.bounds = Bounds(horizontalOffset, verticalOffset, horizontalOffset + cellSize, verticalOffset + cellSize)
                shape.color = MutableColor(day.getColorValue(style))
                verticalOffset += (cellSize + offset)
                shapes.add(shape)
            }
            val label = options.monthLabels[week.getMonthLabel()]

            if (index > 0) {
                val lastWeek = data.timeSpan.weeks[index - 1]
                if (!lastWeek.hasMonthLabel(options.monthLabels)) {
                    if (week.hasMonthLabel(options.monthLabels)) {
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
        return this
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        for (shape in shapes) {
            shape.onRender(canvas, paint, shapePath, shadowPath)
        }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float) {
        for (shape in shapes) {
            if (shape is TouchConsumer) {
                shape.onTouch(event, x, y)
            }
        }
    }
}