package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.*
import android.util.SparseArray
import android.view.MotionEvent
import androidx.core.util.containsKey
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableText
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

internal class MonthLabelArea(
    val style: HeatMapStyle,
    val bounds: Bounds
) : RenderTarget, TouchableShape {


    override var hovered: Boolean = false

    override var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)? = null

    private val shapes: MutableList<DrawableShape> = mutableListOf()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    fun buildWith(
        measurements: Measurements,
        monthLabels: SparseArray<HeatMapLabel>,
        weeks: List<Week>
    ) {
        val offset: Float = measurements.cellGap
        val cellSize: Float = measurements.cellSize
        val leftOffset: Float = measurements.dayLabelAreaWidth

        var horizontalOffset = leftOffset + offset

        for ((index, _) in weeks.withIndex()) {
            val label = if (monthLabels.containsKey(index)) {
                monthLabels[index]
            } else null
            if (label != null) {
                val monthLabel = DrawableText(
                    text = label.text
                ).build(paint)
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