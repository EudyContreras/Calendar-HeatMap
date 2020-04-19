package com.eudycontreras.calendarheatmaplibrary.framework.elements

import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.core.util.getOrDefault
import com.eudycontreras.calendarheatmaplibrary.common.Element
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableText
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
    val bounds: Bounds
): Element {

    private var shapes: List<DrawableShape> = emptyList()

    fun buildWith(cellSize: Float, offset: Float, leftOffset: Float, monthLabels: SparseArray<HeatMapLabel>, weeks: List<Week>, style: HeatMapStyle) {

        val drawables = mutableListOf<DrawableShape>()

        var horizontalOffset = leftOffset + offset

        for ((index, _) in weeks.withIndex()) {
            val label = if (monthLabels.containsKey(index)) { monthLabels[index] } else null
            if (label != null) {
                val monthLabel = DrawableText(label.text).build()
                monthLabel.x = horizontalOffset
                monthLabel.y = bounds.bottom - offset
                monthLabel.alignment = DrawableText.Alignment.LEFT
                monthLabel.textSize = style.monthLabelStyle.textSize
                monthLabel.typeFace = style.monthLabelStyle.typeFace
                monthLabel.textColor = MutableColor(style.monthLabelStyle.textColor)
                drawables.add(monthLabel)
            }
            horizontalOffset += (cellSize + offset)
        }

        shapes = drawables
    }

    override fun getShapes(): List<DrawableShape> {
        return shapes
    }
}