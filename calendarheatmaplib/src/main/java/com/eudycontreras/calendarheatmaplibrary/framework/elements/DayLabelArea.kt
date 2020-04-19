package com.eudycontreras.calendarheatmaplibrary.framework.elements

import android.graphics.Paint
import com.eudycontreras.calendarheatmaplibrary.common.Element
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableText
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
    val bounds: Bounds
): Element {

    private var shapes: List<DrawableShape> = emptyList()

    fun buildWith(cellSize: Float, offset: Float, topOffset: Float, style: HeatMapStyle) {
        val labels = options.dayLabels

        val drawables = mutableListOf<DrawableShape>()

        var verticalOffset = topOffset
        for (label in labels) {
            if (label.active) {
                val dayLabel = DrawableText(label.text).build()
                dayLabel.x = bounds.right
                dayLabel.y = verticalOffset + ((cellSize / 2) + (dayLabel.height / 2))
                dayLabel.alignment = DrawableText.Alignment.RIGHT
                dayLabel.textSize = style.dayLabelStyle.textSize
                dayLabel.typeFace = style.dayLabelStyle.typeFace
                dayLabel.textColor = MutableColor(style.dayLabelStyle.textColor)
                drawables.add(dayLabel)
            }
            verticalOffset += (cellSize + offset)
        }

        shapes = drawables
    }

    override fun getShapes(): List<DrawableShape> {
        return shapes
    }
}