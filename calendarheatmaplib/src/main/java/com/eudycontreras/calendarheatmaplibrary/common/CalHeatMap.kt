package com.eudycontreras.calendarheatmaplibrary.common

import android.animation.ValueAnimator
import com.eudycontreras.calendarheatmaplibrary.animations.AnimationEvent

interface CalHeatMap: ValueAnimator.AnimatorUpdateListener {
    fun startAnimation()
    fun stopAnimation()
    fun hapticFeeback(feedback: Int)
    fun fullyVisible(): Boolean
    fun addAnimation(animation: AnimationEvent?)
    fun removeAnimation(animation: AnimationEvent?)
    var animationCollection: MutableList<AnimationEvent>
    var onFullyVisible: ((CalHeatMap) -> Unit)?
}