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

    enum class Type{
        QUAD,
        LINE
    }

    val points: ArrayList<PathPoint> = ArrayList()

    var startX: Float = MIN_OFFSET

    var startY: Float = MIN_OFFSET

    var width: Float = MIN_OFFSET

    var height: Float = MIN_OFFSET

    var contentBounds: Bounds = Bounds()

    var pathCreated: Boolean = false

    fun translate(dx: Float, dy: Float) {
        val offsetX = dx - startX
        val offsetY = dy - startY
        path.offset(offsetX, offsetY)
        startX = dx
        startY = dy
    }

    fun build() {
        path.rewind()
        path.moveTo(startX, startY)

        for(point in points) {
            when (point.type) {
                Type.LINE -> {
                    if(point.relative) {
                        path.rLineTo(point.startX, point.startY)
                    }else{
                        path.lineTo(point.startX, point.startY)
                    }
                }
                Type.QUAD -> {
                    if(point.relative) {
                        path.rQuadTo(point.startX, point.startY, point.endX, point.endY)
                    }else{
                        path.quadTo(point.startX, point.startY, point.endX, point.endY)
                    }
                }
            }
        }
        pathCreated = true
        path.close()
    }
}