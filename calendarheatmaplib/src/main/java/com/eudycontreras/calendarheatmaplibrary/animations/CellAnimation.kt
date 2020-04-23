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
import com.eudycontreras.calendarheatmaplibrary.mapRange

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class CellAnimation : HeatMapAnimation<List<Animateable>> {
    override var interpolator: Interpolator = FastOutSlowInInterpolator()

    override var duration: Long = 0
    override var delay: Long = 0

    override var onEnd: (() -> Unit)? = null
    override var onStart: (() -> Unit)? = null

    var sequential: Boolean = false
    var stagger: Long = 0

    var type: AnimationType = AnimationType.LEFT_TO_RIGHT

    enum class AnimationType {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        EDGE_TO_CENTER,
        CENTER_TO_EDGE
    }

    override fun animate(heatMap: CalHeatMap, animateable: List<Animateable>) {
        if (sequential) {
            performSequentialAnimation(heatMap, animateable)
        } else {
            performAnimation(heatMap, animateable)
        }
    }

    private fun performAnimation(
        view: CalHeatMap,
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

    private fun performSequentialAnimation(
        view: CalHeatMap,
        animateable: List<Animateable>
    ) {
        val indexes = createOrder(animateable.size, type)

        val valueAnimator = ValueAnimator.ofFloat(MIN_OFFSET, MAX_OFFSET)

        val startPoints = ArrayList<Pair<Float, Float>>()

        val values = ArrayList<Pair<Float, Float>>()

        var start = 0L
        var end = start + duration
        var totalDuration = duration

        startPoints.add(start.toFloat() to end.toFloat())

        for (i in animateable.indices) {
            values.add(MIN_OFFSET to MAX_OFFSET)
            start += stagger
            end = start + duration
            totalDuration += stagger
            startPoints.add(start.toFloat() to end.toFloat())
        }

        valueAnimator.startDelay = delay
        valueAnimator.duration = totalDuration
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
            for (index in animateable.indices) {
                val currentTime = it.currentPlayTime.toFloat()
                val animate = MathUtils.clamp(
                    mapRange(
                        currentTime,
                        startPoints[index].first,
                        startPoints[index].second,
                        values[index].first,
                        values[index].second
                    ),
                    values[index].first,
                    values[index].second
                )
                animateable[indexes[index]].onAnimate(it.interpolator.getInterpolation(animate))
            }
        }
        valueAnimator.start()
    }

    private fun createOrder(itemCount: Int, type: AnimationType): ArrayList<Int> {
        val indexes = ArrayList<Int>()

        when (type) {
            AnimationType.LEFT_TO_RIGHT -> {
                for (i in 0 until itemCount) {
                    indexes.add(i)
                }
            }
            AnimationType.RIGHT_TO_LEFT -> {
                for (i in (itemCount - 1) downTo 0) {
                    indexes.add(i)
                }
            }
            AnimationType.EDGE_TO_CENTER -> {
                val even = itemCount % 2 == 0
                val midSection = itemCount / 2

                var offsetLeft = 0
                var offsetRight = itemCount - 1

                for (i in 0 until midSection) {
                    indexes.add(offsetLeft)
                    indexes.add(offsetRight)
                    offsetLeft += 1
                    offsetRight -= 1
                }

                if (!even) {
                    indexes.add(midSection)
                }
            }
            AnimationType.CENTER_TO_EDGE -> {
                val even = itemCount % 2 == 0
                val midSection = itemCount / 2

                var offsetLeft = midSection - if (even) 1 else 0
                var offsetRight = midSection + if (even) 0 else 1

                for (i in 0 until midSection) {
                    indexes.add(offsetLeft)
                    indexes.add(offsetRight)
                    offsetLeft -= 1
                    offsetRight += 1
                }

                if (!even) {
                    indexes.add(0)
                }
            }
        }
        return indexes
    }
}