package com.eudycontreras.calendarheatmaplibrary.common

import android.view.MotionEvent

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal interface TouchConsumer {
    fun onTouch(event: MotionEvent, x: Float, y: Float)
    var touchHandler: ((TouchConsumer, MotionEvent, Float, Float) -> Unit)?
}