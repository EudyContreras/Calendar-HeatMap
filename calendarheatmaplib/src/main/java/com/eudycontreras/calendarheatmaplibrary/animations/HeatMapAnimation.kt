package com.eudycontreras.calendarheatmaplibrary.animations

import android.view.animation.Interpolator
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMap

/**
 * Created by eudycontreras.
 */
interface HeatMapAnimation<T> {

    var interpolator: Interpolator
    var duration: Long
    var delay: Long

    var onEnd: (() -> Unit)?
    var onStart: (() -> Unit)?

    interface Animateable {
        fun onPreAnimation()
        fun onPostAnimation()
        fun onAnimate(delta: Float)
    }

    fun animate(heatMap: CalHeatMap, animateable: T)
}