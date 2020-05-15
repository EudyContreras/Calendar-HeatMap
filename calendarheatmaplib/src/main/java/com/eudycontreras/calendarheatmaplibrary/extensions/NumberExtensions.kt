package com.eudycontreras.calendarheatmaplibrary.extensions

import android.content.res.Resources
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

/**
 * Use the float as density independent pixels and return the pixel value
 */
val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

/**
 * Use the float as scale independent pixels and return the pixel value
 */
val Int.sp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity

val Float.sp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity

fun Int.toMutableColor() = MutableColor(this)

fun Float.clamp(min: Float? = null, max: Float? = null): Float {
    if (min == null && max == null) return this
    return when {
        this < min ?: this -> min ?: this
        this > max ?: this -> max ?: this
        else -> this
    }
}