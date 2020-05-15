package com.eudycontreras.calendarheatmaplibrary.properties

import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

sealed class PathPoint {

    data class Point(var x: Float, var y: Float) : PathPoint()

    data class Corner(var corner: PathCorner, val cornerRadius: Float) : PathPoint() {
        var startX: Float = MIN_OFFSET
        var startY: Float = MIN_OFFSET
        var endX: Float = MIN_OFFSET
        var endY: Float = MIN_OFFSET

        init {
            when (corner) {
                PathCorner.TOP_LEFT -> {
                    startX = MIN_OFFSET
                    startY = -cornerRadius
                    endX = cornerRadius
                    endY = -cornerRadius
                }
                PathCorner.TOP_RIGHT -> {
                    startX = cornerRadius
                    startY = MIN_OFFSET
                    endX = cornerRadius
                    endY = cornerRadius
                }
                PathCorner.BOTTOM_LEFT -> {
                    startX = -cornerRadius
                    startY = MIN_OFFSET
                    endX = -cornerRadius
                    endY = -cornerRadius
                }
                PathCorner.BOTTOM_RIGHT -> {
                    startX = MIN_OFFSET
                    startY = cornerRadius
                    endX = -cornerRadius
                    endY = cornerRadius
                }
            }
        }
    }
}

enum class PathCorner {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}