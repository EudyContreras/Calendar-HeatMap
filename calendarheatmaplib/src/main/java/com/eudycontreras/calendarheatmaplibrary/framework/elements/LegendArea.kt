package com.eudycontreras.calendarheatmaplibrary.framework.elements

import android.graphics.Paint
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.Element
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableText
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapOptions
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapStyle
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class LegendArea(
    private val options: HeatMapOptions,
    val bounds: Bounds
): Element {

    private val levelCount: Int = 4

    private var shapes: List<DrawableShape> = emptyList()

    fun buildWith(cellSize: Float, offset: Float, style: HeatMapStyle) {
        val lessText = DrawableText(options.legendLessLabel).build()
        val moreText = DrawableText(options.legendMoreLabel)

        lessText.x = bounds.x + offset
        lessText.y = bounds.y + cellSize + offset
        lessText.alignment = DrawableText.Alignment.LEFT
        lessText.textSize = style.legendLabelStyle.textSize
        lessText.typeFace = style.legendLabelStyle.typeFace
        lessText.textColor = MutableColor(style.legendLabelStyle.textColor)

        val drawables = mutableListOf<DrawableShape>()

        var leftOffset = lessText.x + (offset * 2) + lessText.textBounds.width()

        for (level in 0..levelCount) {
            val shape = DrawableRectangle()
            shape.x = leftOffset
            shape.y = lessText.y - cellSize
            shape.width = cellSize
            shape.height = cellSize
            shape.color = getColor(level, 0f, levelCount.toFloat(), style)
            drawables.add(shape)
            leftOffset += (cellSize + offset)
        }

        moreText.x = leftOffset + offset
        moreText.y = bounds.y + cellSize + offset
        moreText.alignment = DrawableText.Alignment.LEFT
        moreText.textSize = style.legendLabelStyle.textSize
        moreText.typeFace = style.legendLabelStyle.typeFace
        moreText.textColor = MutableColor(style.legendLabelStyle.textColor)

        drawables.add(lessText)
        drawables.add(moreText)
        shapes = drawables
    }

    override fun getShapes(): List<DrawableShape> {
        return shapes
    }

    @Suppress("SameParameterValue")
    private fun getColor(value: Int, min: Float, max: Float, style: HeatMapStyle): MutableColor {
        val range = mapRange(value.toFloat(), min, max, MIN_OFFSET, MAX_OFFSET)
        val colorMin = MutableColor.fromColor(style.minCellColor)
        val colorMax = MutableColor.fromColor(style.maxCellColor)
        return MutableColor.interpolateColor(colorMin, colorMax, range)
    }
}