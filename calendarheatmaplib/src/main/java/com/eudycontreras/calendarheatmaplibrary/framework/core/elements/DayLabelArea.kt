package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.*
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Text
import com.eudycontreras.calendarheatmaplibrary.framework.data.Alignment
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapLabel
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapStyle
import com.eudycontreras.calendarheatmaplibrary.framework.data.Measurements
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
    val style: HeatMapStyle,
    val bounds: Bounds
) : RenderTarget {

    private val shapes: MutableList<DrawableShape> = mutableListOf()

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    fun buildWith(measurements: Measurements, labels: List<HeatMapLabel>) {
        val offset: Float = measurements.cellGap
        val cellSize: Float = measurements.cellSize
        val topOffset: Float = measurements.monthLabelAreaHeight

        var verticalOffset = topOffset

        for (label in labels) {
            if (label.active) {
                val dayLabel = Text(text = label.text, paint = paint
                ).build()
                dayLabel.x = bounds.right
                dayLabel.y = verticalOffset + ((cellSize / 2) + (dayLabel.height / 2))
                dayLabel.alignment = Alignment.RIGHT
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
}