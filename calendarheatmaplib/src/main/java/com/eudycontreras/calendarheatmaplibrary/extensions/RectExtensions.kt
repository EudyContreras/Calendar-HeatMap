package com.eudycontreras.calendarheatmaplibrary.extensions

import android.graphics.Rect
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

fun Rect.toBounds(): Bounds {
    return Bounds(
        left = this.left.toFloat(),
        top = this.top.toFloat(),
        right = this.right.toFloat(),
        bottom = this.bottom.toFloat()
    )
}