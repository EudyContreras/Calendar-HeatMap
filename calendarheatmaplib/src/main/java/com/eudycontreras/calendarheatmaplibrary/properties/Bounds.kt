package com.eudycontreras.calendarheatmaplibrary.properties

import com.eudycontreras.calendarheatmaplibrary.properties.Bounds.Side.*
import kotlin.math.abs

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

data class Bounds(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f
) {
    val left: Float
        get() = x

    val right: Float
        get() = x + width

    val top: Float
        get() = y

    val bottom: Float
        get() = y + height

    val centerX: Float
        get() = (left + right) * 0.5f

    val centerY: Float
        get() = (top + bottom) * 0.5f

    fun toLeftOf(other: Bounds, gap: Float = 0f): Bounds {
        return copy(
            x = other.x + other.width + gap
        )
    }

    fun toRightOF(other: Bounds, gap: Float = 0f): Bounds {
        return copy(
            x = other.x - width + gap
        )
    }

    fun toTopOf(other: Bounds, gap: Float = 0f): Bounds {
        return copy(
            y = other.y - height + gap
        )
    }

    fun toBottomOf(other: Bounds, gap: Float = 0f): Bounds {
        return copy(
            y = other.y + other.height + gap
        )
    }

    fun pad(amount: Float, side: Side = ALL) {
        return when (side) {
            TOP ->  y += amount
            LEFT -> y += amount
            RIGHT -> width -= amount
            BOTTOM -> height -= amount
            ALL -> {
                pad(amount, LEFT)
                pad(amount, RIGHT)
                pad(amount, TOP)
                pad(amount, BOTTOM)
            }
        }
    }

    fun padPercent(percentage: Float, side: Side = ALL) {
        return when (side) {
            TOP -> y *= percentage
            LEFT -> x *= percentage
            RIGHT -> width *= (width * percentage)
            BOTTOM -> height *= (height * percentage)
            ALL -> {
                padPercent(percentage, LEFT)
                padPercent(percentage, RIGHT)
                padPercent(percentage, TOP)
                padPercent(percentage, BOTTOM)
            }
        }
    }

    operator fun plus(amount: Float): Bounds {
        return this.copy(
            width = width + amount,
            height = height + amount
        )
    }

    operator fun minus(amount: Float): Bounds {
        return this.copy(
            width = width - amount,
            height = height - amount
        )
    }

    operator fun times(amount: Float): Bounds {
        return this.copy(
            width = width * amount,
            height = height * amount
        )
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
        x = 0f
        y = 0f
        width = 0f
        height = 0f
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

    enum class Side {
        TOP, LEFT, RIGHT, BOTTOM, ALL
    }
}
