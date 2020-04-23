package com.eudycontreras.calendarheatmaplibrary.properties

import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import kotlin.math.abs

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

data class Bounds(
    var left: Float = 0f,
    var top: Float = 0f,
    var right: Float = 0f,
    var bottom: Float = 0f
) {
    var x: Float
        get() = left
        set(value) {
            val offset = left - value
            left = value
            right -= offset
        }

    var y: Float
        get() = top
        set(value) {
            val offset = top - value
            top = value
            bottom -= offset
        }

    var width: Float
        get() = right - left
        set(value) {
            right = left + value
        }

    var height: Float
        get() = bottom - top
        set(value) {
            bottom = top + value
        }

    var centerX: Float
        get() = (left + right) * 0.5f
        set(value) {
            val width = width
            left = (value - (width / 2))
            right = (value + (width / 2))
        }

    var centerY: Float
        get() = (top + bottom) * 0.5f
        set(value) {
            val height = height
            top = (value - (height / 2))
            bottom = (value + (height / 2))
        }

    fun getVerticalOverlap(other: Bounds): Float {
        val top1 = this.top
        val top2 = other.top

        val bottom1 = this.bottom
        val bottom2 = other.bottom

        return if (top1 > top2) {
            abs(bottom1 - top2)
        } else {
            abs(bottom2 - top1)
        }
    }

    fun reset() {
        left = MIN_OFFSET
        right = MIN_OFFSET
        top = MIN_OFFSET
        bottom = MIN_OFFSET
    }

    fun update(bounds: Bounds) {
        this.left = bounds.left
        this.top = bounds.top
        this.right = bounds.right
        this.bottom = bounds.bottom
    }

    fun intercepts(other: Bounds): Boolean {
        return x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y
    }

    fun verticalIntercepts(other: Bounds): Boolean {
        return  y < other.y + other.height && y + height > other.y
    }

    fun horizontalIntercepts(other: Bounds): Boolean {
        return x < other.x + other.width && x + width > other.x
    }

    fun interceptsAny(other: List<Bounds>): Boolean {
        return other.any { it.intercepts(this) }
    }

    fun isInside(x: Float, y: Float): Boolean {
        return (x in left..right && y in top..bottom)
    }

    fun isInside(bounds: Bounds): Boolean {
        return isInside(bounds.top, bounds.left, bounds.bottom, bounds.right)
    }

    fun isInside(top: Float, left: Float, bottom: Float, right: Float): Boolean {
        return this.top >= top && this.left >= left && this.bottom <= bottom && this.right <= right
    }
}
