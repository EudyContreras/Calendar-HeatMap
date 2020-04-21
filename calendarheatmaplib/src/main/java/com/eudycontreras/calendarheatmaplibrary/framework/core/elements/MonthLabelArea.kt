package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.SparseArray
import android.view.MotionEvent
import androidx.core.util.containsKey
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableText
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapLabel
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapOptions
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapStyle
import com.eudycontreras.calendarheatmaplibrary.framework.data.Week
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class MonthLabelArea(
    val options: HeatMapOptions,
    val style: HeatMapStyle,
    val bounds: Bounds
): RenderTarget, TouchableShape {

    private val shapes: MutableList<DrawableShape> = mutableListOf()

    override var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)? = null

    override var hovered: Boolean = false

    fun buildWith(cellSize: Float, offset: Float, leftOffset: Float, monthLabels: SparseArray<HeatMapLabel>, weeks: List<Week>) {
        var horizontalOffset = leftOffset + offset

        for ((index, _) in weeks.withIndex()) {
            val label = if (monthLabels.containsKey(index)) { monthLabels[index] } else null
            if (label != null) {
                val monthLabel = DrawableText(
                    label.text
                ).build()
                monthLabel.x = horizontalOffset
                monthLabel.y = bounds.bottom - offset
                monthLabel.alignment = DrawableText.Alignment.LEFT
                monthLabel.textSize = style.monthLabelStyle.textSize
                monthLabel.typeFace = style.monthLabelStyle.typeFace
                monthLabel.textColor = MutableColor(style.monthLabelStyle.textColor)
                shapes.add(monthLabel)
            }
            horizontalOffset += (cellSize + offset)
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