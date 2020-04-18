package com.eudycontreras.calendarheatmaplibrary.common

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

@FunctionalInterface
internal interface RenderTarget {
    fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path)
}
