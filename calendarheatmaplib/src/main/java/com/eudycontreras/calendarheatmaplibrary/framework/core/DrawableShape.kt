package com.eudycontreras.calendarheatmaplibrary.framework.core

import android.graphics.Shader
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

internal abstract class DrawableShape: RenderTarget {

    var id: Int = View.NO_ID

    var color: MutableColor = MutableColor()

    var bounds: Bounds = Bounds()

    var elevation: Float = MIN_OFFSET

    var strokeWidth: Float = MIN_OFFSET

    var shadowAlpha: Int = MAX_COLOR

    var corners: CornerRadii = CornerRadii()

    val alpha: Float
        get() = opacity.toFloat() / MAX_COLOR.toFloat()

    var opacity: Int = MAX_COLOR
        set(value) {
            field = value
            color.updateAlpha(opacity)
        }

    val drawShadows: Boolean
        get() = elevation > MIN_OFFSET && shadowAlpha > MIN_OFFSET

    var strokeColor: MutableColor? = null

    var shadowColor: MutableColor? = null

    var shader: Shader? = null

    var render: Boolean = true

    var showStroke: Boolean = false
        get() = field && strokeWidth > 0

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
    }

    var x: Float
        get() = bounds.x
        set(value) {
            bounds.left = value
        }

    var y: Float
        get() = bounds.y
        set(value) {
            bounds.top = value
        }

    var width: Float
        get() = bounds.width
        set(value) {
            bounds.right = bounds.left + value
        }

    var height: Float
        get() = bounds.height
        set(value) {
            bounds.bottom = bounds.top + value
        }

    val radii: FloatArray
        get() = corners.corners

    val left: Float = bounds.left
    val right: Float = bounds.right
    val bottom: Float = bounds.bottom
    val top: Float = bounds.top
}