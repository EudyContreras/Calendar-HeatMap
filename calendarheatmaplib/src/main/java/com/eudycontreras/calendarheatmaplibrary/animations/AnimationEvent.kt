package com.eudycontreras.calendarheatmaplibrary.animations

import android.view.animation.Interpolator
import com.eudycontreras.calendarheatmaplibrary.Action
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.UpdateTarget
import com.eudycontreras.calendarheatmaplibrary.mapRange

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
class AnimationEvent(
    internal val minOffset: Float = MIN_OFFSET,
    internal val maxOffset: Float = MAX_OFFSET,
    internal val duration: Long = 250L,
    internal val startDelay: Long = 0L,
    internal val interpolator: Interpolator? = null,
    internal val onStart: Action? = null,
    internal val onEnd: Action? = null,
    internal val updateListener: (animation: AnimationEvent, currentPlayTime: Long, delta: Float) -> Unit
) : UpdateTarget {

    internal var startTime: Long = System.currentTimeMillis() + startDelay

    internal val endTime: Long
        get() = (startTime + duration)

    internal val isRunning: Boolean
        get() = System.currentTimeMillis() < endTime && !hasEnded

    internal val hasEnded: Boolean
        get() = System.currentTimeMillis() > endTime

    internal val hasStarted: Boolean
        get() = System.currentTimeMillis() >= startTime

    internal var endedInvoked: Boolean = false
    internal var startInvoked: Boolean = false

    override fun onUpdate(currentPlayTime: Long, delta: Float) {
        val value = System.currentTimeMillis()
        val offset = mapRange(value, startTime, endTime, minOffset, maxOffset)
        val interpolated = interpolator?.getInterpolation(offset) ?: offset
        updateListener(this, currentPlayTime, interpolated)
    }
}