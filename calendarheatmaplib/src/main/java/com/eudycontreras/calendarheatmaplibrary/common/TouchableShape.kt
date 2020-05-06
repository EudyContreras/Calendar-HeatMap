package com.eudycontreras.calendarheatmaplibrary.common

import android.graphics.Rect
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeManager

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal interface TouchableShape {
    fun onTouch(
        event: MotionEvent,
        x: Float,
        y: Float,
        viewBounds: Rect,
        shapeManager: ShapeManager
    )

    fun onLongPressed(
        event: MotionEvent,
        x: Float,
        y: Float,
        viewBounds: Rect,
        shapeManager: ShapeManager
    )

    var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)?
    var hovered: Boolean
}