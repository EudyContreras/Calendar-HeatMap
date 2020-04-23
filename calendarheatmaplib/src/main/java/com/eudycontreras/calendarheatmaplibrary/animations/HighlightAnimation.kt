package com.eudycontreras.calendarheatmaplibrary.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.Interpolator
import androidx.core.math.MathUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.Animateable
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.mapRange

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class HighlightAnimation : HeatMapAnimation<List<Animateable>> {

    override var interpolator: Interpolator = FastOutSlowInInterpolator()

    override var duration: Long = 0
    override var delay: Long = 0

    override var onEnd: (() -> Unit)? = null
    override var onStart: (() -> Unit)? = null

    override fun animate(heatMap: CalHeatMap, animateable: List<Animateable>) {
        performAnimation(heatMap, animateable)
    }

    private fun performAnimation(
        heatMap: CalHeatMap,
        animateable: List<Animateable>
    ) {
        val valueAnimator = ValueAnimator.ofFloat(MIN_OFFSET, MAX_OFFSET)

        valueAnimator.startDelay = delay
        valueAnimator.duration = duration
        valueAnimator.interpolator = interpolator
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                animateable.forEach { it.onPreAnimation() }
                onStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onEnd?.invoke()
            }
        })
        valueAnimator.addUpdateListener {
            val animate = it.animatedValue as Float
            for (item in animateable) {
                item.onAnimate(animate)
            }
        }
        valueAnimator.start()
    }
}