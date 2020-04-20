package com.eudycontreras.calendarheatmaplibrary.properties

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
    val x: Float
        get() = left

    val y: Float
        get() = top

    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top

    val centerX: Float
        get() = (left + right) * 0.5f

    val centerY: Float
        get() = (top + bottom) * 0.5f

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
        left = 0f
        right = 0f
        top = 0f
        bottom = 0f
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
        return (x > left && x < right && y > top && y < bottom)
    }

    fun isInside(bounds: Bounds): Boolean {
        return isInside(bounds.top, bounds.left, bounds.bottom, bounds.right)
    }

    fun isInside(top: Float, left: Float, bottom: Float, right: Float): Boolean {
        return this.top >= top && this.left >= left && this.bottom <= bottom && this.right <= right
    }
}
