package com.eudycontreras.calendarheatmaplibrary

import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.math.MathUtils
import com.eudycontreras.calendarheatmaplibrary.properties.Property
import kotlin.math.abs
import kotlin.math.roundToInt

internal const val VIEWMODEL = 1

internal const val MIN_OFFSET = 0.0f
internal const val MAX_OFFSET = 1.0f

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

/**
 * Maps the given value from the specified minimum to the specified
 * minimum and from the specified maximum to the specified maximum
 * value. Ex:
 * ```
 *  var value = 40f
 *
 *  var fromMin = 0f
 *  var fromMax = 100f
 *
 *  var toMin = 0f
 *  var toMax = 1f
 *
 *  var result = 0.4f
 * ```
 * @param value the value to be transformed
 * @param fromMin the minimum value to map from
 * @param fromMax the maximum value to map from
 * @param toMin the minimum value to map to
 * @param toMax the maximum value to map to
 */
internal fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return mapRange(value, fromMin, fromMax, toMin, toMax, toMin, toMax)
}

internal fun mapRange(value: Long, fromMin: Long, fromMax: Long, toMin: Float, toMax: Float): Float {
    return mapRange(value, fromMin, fromMax, toMin, toMax, toMin, toMax)
}

internal fun mapRange(value: Int, fromMin: Int, fromMax: Int, toMin: Int, toMax: Int): Int {
    return mapRange(value, fromMin, fromMax, toMin, toMax, toMin, toMax)
}

/**
 * Maps the given value from the specified minimum to the specified
 * minimum and from the specified maximum to the specified maximum using
 * clamping.
 * value. Ex:
 * ```
 *  var value = 40f
 *
 *  var fromMin = 0f
 *  var fromMax = 100f
 *
 *  var toMin = 0f
 *  var toMax = 1f
 *
 *  var result = 0.4f
 * ```
 * @param value the value to be transformed
 * @param fromMin the minimum value to map from
 * @param fromMax the maximum value to map from
 * @param toMin the minimum value to map to
 * @param toMax the maximum value to map to
 * @param clampMin the minimum value that the function can return
 * @param clampMax the maximum value that the function can return
 */
internal fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float, clampMin: Float, clampMax: Float): Float {
    return MathUtils.clamp(
        (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin,
        clampMin,
        clampMax
    )
}

internal fun mapRange(value: Long, fromMin: Long, fromMax: Long, toMin: Float, toMax: Float, clampMin: Float, clampMax: Float): Float {
    return MathUtils.clamp(
        (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin,
        clampMin,
        clampMax
    )
}

internal fun mapRange(value: Int, fromMin: Int, fromMax: Int, toMin: Int, toMax: Int, clampMin: Int, clampMax: Int): Int {
    return MathUtils.clamp(
        (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin,
        clampMin,
        clampMax
    )
}

/**
 * Returns the linear interpolation of a start value
 * to an end value given the specified fraction of progress.
 * @param from the start value
 * @param to the end value
 * @param fraction the amount to lerp to given the range
 */
fun lerp(from: Float, to: Float, fraction: Float): Float {
    return from + (to - from) * fraction
}

/**
 * Returns the linear interpolation of a start value
 * to an end value given the specified fraction of progress.
 * @param from the start value
 * @param to the end value
 * @param fraction the amount to lerp to given the range
 */
fun lerp(from: Int, to: Int, fraction: Float): Int {
    return (from + (to - from) * fraction).roundToInt()
}

/**
 * Returns the distance between two locations.
 * @param x0 The x axis coordinates of the first point
 * @param y0 The y axis coordinates of the first point
 * @param x1 The x axis coordinates of the second point
 * @param y1 The y axis coordinates of the second point
 */
internal fun manDistance(x0: Float, y0: Float, x1: Float, y1: Float) = abs(x1 - x0) + abs(y1 - y0)
internal fun manDistance(x0: Int, y0: Int, x1: Int, y1: Int) = abs(x1 - x0) + abs(y1 - y0)

fun findScrollParent(parent: ViewGroup, criteria: (ViewGroup) -> Boolean): ViewParent? {
    val property: Property<ViewGroup?> = Property(parent)

    fun digOutParent(parent: ViewGroup?) {
        if (parent != null) {
            if (criteria(parent)) {
                property.setValue(parent)
            } else {
                if (parent.parent != null) {
                    digOutParent(parent.parent as ViewGroup)
                } else {
                    property.setValue(null)
                }
            }
        } else {
            property.setValue(null)
        }
    }

    return if (!criteria(parent)) {
        digOutParent(parent.parent as ViewGroup)

        property.getValue()
    } else {
        parent
    }
}