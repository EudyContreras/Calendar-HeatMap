package com.eudycontreras.calendarheatmaplibrary.animations

import android.view.animation.Interpolator
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMap

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

interface HeatMapAnimation<T> {

    var interpolator: Interpolator
    var duration: Long
    var delay: Long

    var onEnd: (() -> Unit)?
    var onStart: (() -> Unit)?

    fun animate(heatMap: CalHeatMap, animateable: T)
}