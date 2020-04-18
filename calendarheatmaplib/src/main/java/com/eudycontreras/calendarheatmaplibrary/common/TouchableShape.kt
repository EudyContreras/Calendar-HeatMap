package com.eudycontreras.calendarheatmaplibrary.common

import android.view.MotionEvent

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

interface TouchableShape {
    fun onTouch(event: MotionEvent, x: Float, y: Float)
    fun onHovered(event: MotionEvent, x: Float, y: Float)
    fun onLongPressed(event: MotionEvent, x: Float, y: Float)
}