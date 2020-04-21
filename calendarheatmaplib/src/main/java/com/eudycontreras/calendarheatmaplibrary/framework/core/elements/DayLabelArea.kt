package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableText
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapOptions
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapStyle
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class DayLabelArea(
    val options: HeatMapOptions,
    val style: HeatMapStyle,
    val bounds: Bounds
): RenderTarget, TouchableShape {

    private val shapes: MutableList<DrawableShape> = mutableListOf()

    override var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)? = null

    override var hovered: Boolean = false

    fun buildWith(offset: Float, topOffset: Float) {
        val labels = options.dayLabels

        var verticalOffset = topOffset

        for (label in labels) {
            if (label.active) {
                val dayLabel = DrawableText(
                    label.text
                ).build()
                dayLabel.x = bounds.right
                dayLabel.y = verticalOffset + ((cellSize / 2) + (dayLabel.height / 2))
                dayLabel.alignment = DrawableText.Alignment.RIGHT
                dayLabel.textSize = style.dayLabelStyle.textSize
                dayLabel.typeFace = style.dayLabelStyle.typeFace
                dayLabel.textColor = MutableColor(style.dayLabelStyle.textColor)
                shapes.add(dayLabel)
            }
            verticalOffset += (cellSize + offset)
        }
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        for (shape in shapes) {
            shape.onRender(canvas, paint, shapePath, shadowPath)
        }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        for (shape in shapes) {
            if (shape is TouchableShape) {
                shape.onTouch(event, x, y, shapeRenderer)
            }
        }
    }

    override fun onLongPressed(
        event: MotionEvent,
        x: Float,
        y: Float,
        shapeRenderer: ShapeRenderer
    ) {
        for (shape in shapes) {
            if (shape is TouchableShape) {
                shape.onLongPressed(event, x, y, shapeRenderer)
            }
        }
    }
}