package com.eudycontreras.calendarheatmaplibrary.framework

import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.framework.shapes.HeatMapCell
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class CalHeatMapBuilder(
    private val shapeRenderer: ShapeRenderer,
    private var styleContext: () -> HeatMapStyle,
    private var optionsContext: () -> HeatMapOptions
) {

    private var calHeatMapData: HeatMapData? = null

    fun getData(): HeatMapData? = calHeatMapData

    fun buildWithBounds(bounds: Bounds) {
        val style = styleContext.invoke()
        val options = optionsContext.invoke()

        val data = calHeatMapData

        if (data != null) {
            val cellSize  = data.cellSize
            val gapSize = data.cellGap ?: if (cellSize != null) { cellSize * 0.17f } else {
                (bounds.height / TimeSpan.MAX_DAYS) * 0.17f
            }
            val gapRatio = (gapSize * TimeSpan.MAX_DAYS)
            val size = data.cellSize ?: ((bounds.height  - gapRatio) / TimeSpan.MAX_DAYS)

            var horizontalOffset = gapSize
            for (week in data.timeSpan.weeks) {
                var verticalOffset = 0f
                for(day in week.weekDays) {
                    val shape = HeatMapCell()
                    shape.bounds = Bounds(horizontalOffset, verticalOffset, size, size)
                    shape.color = MutableColor(day.getColorValue(style))
                    verticalOffset += (size + gapSize)
                    shapeRenderer.addShape(shape)
                }
                horizontalOffset += (size + gapSize)
            }
        }
    }

    fun buildWithData(calHeatMapData: HeatMapData) {
        this.calHeatMapData = calHeatMapData
    }
}