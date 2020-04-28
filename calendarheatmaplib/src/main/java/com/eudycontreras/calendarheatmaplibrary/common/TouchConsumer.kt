package com.eudycontreras.calendarheatmaplibrary.common

import com.eudycontreras.calendarheatmaplibrary.properties.Bounds

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal interface TouchConsumer {
    fun onTouch(eventAction: Int, bounds: Bounds, x: Float, y: Float)
    var touchHandler: ((TouchConsumer, Int, Bounds, Float, Float) -> Unit)?
}