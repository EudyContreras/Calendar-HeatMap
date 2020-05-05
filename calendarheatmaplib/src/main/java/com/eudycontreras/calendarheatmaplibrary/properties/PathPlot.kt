package com.eudycontreras.calendarheatmaplibrary.properties

import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
class PathPlot(val path: Path){

    val points: ArrayList<PathPoint> = ArrayList()

    var startX: Float = MIN_OFFSET
    var startY: Float = MIN_OFFSET

    var width: Float = MIN_OFFSET
    var height: Float = MIN_OFFSET

    var contentBounds: Bounds = Bounds()

    var pathCreated: Boolean = false

    fun translate(offsetX: Float, offsetY: Float) {
        path.offset(offsetX, offsetY)
    }

    fun build() {
        path.rewind()
        path.moveTo(startX, startY)

        for(point in points) {
            when (point) {
                is PathPoint.Point -> {
                    path.rLineTo(point.x, point.y)
                }
                is PathPoint.Corner -> {
                    path.rQuadTo(point.startX, point.startY, point.endX, point.endY)
                }
            }
        }
        pathCreated = true
        path.close()
    }
}