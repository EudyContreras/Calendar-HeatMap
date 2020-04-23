package com.eudycontreras.calendarheatmaplibrary.common

import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal interface TouchConsumer {
    fun onTouch(event: MotionEvent, bounds: Bounds, x: Float, y: Float)
    var touchHandler: ((TouchConsumer, MotionEvent, Bounds, Float, Float) -> Unit)?
}