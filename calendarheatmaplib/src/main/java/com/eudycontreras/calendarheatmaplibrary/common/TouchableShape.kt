package com.eudycontreras.calendarheatmaplibrary.common

import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal interface TouchableShape {
    fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer)
    fun onLongPressed(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer)

    var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)?
    var hovered: Boolean
}