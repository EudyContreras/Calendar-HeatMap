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

    var renderIndex: Int = 0

    open var color: MutableColor = MutableColor()

    open var bounds: Bounds = Bounds()

    open var elevation: Float = MIN_OFFSET

    open var strokeWidth: Float = MIN_OFFSET

    open var shadowAlpha: Int = MAX_COLOR

    open var corners: CornerRadii = CornerRadii()

    open val alpha: Float
        get() = opacity.toFloat() / MAX_COLOR.toFloat()

    open var opacity: Int = MAX_COLOR
        set(value) {
            field = value
            color.updateAlpha(opacity)
        }

    open val drawShadows: Boolean
        get() = elevation > MIN_OFFSET && shadowAlpha > MIN_OFFSET

    open var strokeColor: MutableColor? = null

    open var shadowColor: MutableColor? = null

    open var shader: Shader? = null

    open var render: Boolean = true

    open var showStroke: Boolean = false
        get() = field && strokeWidth > 0

    open fun reset() {
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

    open var x: Float
        get() = bounds.x
        set(value) {
            bounds.x = value
        }

    open var y: Float
        get() = bounds.y
        set(value) {
            bounds.y = value
        }

    open var width: Float
        get() = bounds.width
        set(value) {
            bounds.width = value
        }

    open var height: Float
        get() = bounds.height
        set(value) {
            bounds.height = value
        }

    open val radii: FloatArray
        get() = corners.corners

    val left: Float
        get() = bounds.left
    val right: Float
        get() = bounds.right
    val bottom: Float
        get() = bounds.bottom
    val top: Float
        get() = bounds.top
}