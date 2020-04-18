package com.eudycontreras.calendarheatmaplibrary.framework.core

import android.graphics.Shader
import android.view.MotionEvent
import android.view.View
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Color.Companion.MAX_COLOR
import com.eudycontreras.calendarheatmaplibrary.properties.CornerRadii
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

abstract class DrawableShape: RenderTarget {

    internal var id: Int = View.NO_ID

    internal var color: MutableColor = MutableColor()

    internal var bounds: Bounds = Bounds()

    internal var elevation: Float = MIN_OFFSET

    internal var strokeWidth: Float = MIN_OFFSET

    internal var shadowAlpha: Int = MAX_COLOR

    internal var corners: CornerRadii = CornerRadii()

    internal val alpha: Float
        get() = opacity.toFloat() / MAX_COLOR.toFloat()

    internal var opacity: Int = MAX_COLOR
        set(value) {
            field = value
            color.updateAlpha(opacity)
        }

    internal val drawShadows: Boolean
        get() = elevation > MIN_OFFSET && shadowAlpha > MIN_OFFSET

    internal var strokeColor: MutableColor? = null

    internal var shadowColor: MutableColor? = null

    internal var shader: Shader? = null

    internal var touchProcessor: ((DrawableShape, MotionEvent, Float, Float) -> Unit)? = null

    internal var render: Boolean = true

    internal var showStroke: Boolean = false
        get() = field && strokeWidth > 0

    abstract fun build()

    fun reset() {
        color.reset()
        bounds.reset()
        corners.reset()
        shadowColor?.reset()
        elevation = MIN_OFFSET
        strokeWidth = MIN_OFFSET
        shadowAlpha = MAX_COLOR
        opacity = MAX_COLOR
        shader = null
        render = true
        touchProcessor = null
    }

    internal var x: Float
        get() = bounds.x
        set(value) {
            bounds.x = value
        }

    internal var y: Float
        get() = bounds.y
        set(value) {
            bounds.y = value
        }

    internal var width: Float
        get() = bounds.width
        set(value) {
            bounds.width = value
        }

    internal var height: Float
        get() = bounds.height
        set(value) {
            bounds.height = value
        }

    internal val radius: Float
        get() = ((width + height) / 2) / 2

    internal val radii: FloatArray
        get() = corners.corners

    val left: Float = bounds.left
    val right: Float = bounds.right
    val bottom: Float = bounds.bottom
    val top: Float = bounds.top
}