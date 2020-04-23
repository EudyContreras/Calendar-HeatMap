package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.animation.Interpolator
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.animations.AnimationEvent
import com.eudycontreras.calendarheatmaplibrary.animations.HeatMapAnimation
import com.eudycontreras.calendarheatmaplibrary.common.Animateable
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.extensions.addRoundRect
import com.eudycontreras.calendarheatmaplibrary.extensions.addShadowBounds
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Color
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import com.eudycontreras.calendarheatmaplibrary.utilities.ShadowUtility

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class DrawableRectangle : DrawableShape(), TouchConsumer, Animateable {

    var hovered: Boolean = false

    private var shadowFilter: BlurMaskFilter? = null

    private var lastColor: MutableColor? = null

    private var lastBounds: Bounds? = null

    private var allowInteraction: Boolean = true

    override var touchHandler: ((TouchConsumer, MotionEvent, Bounds, Float, Float) -> Unit)? = null

    override fun onTouch(event: MotionEvent, bounds: Bounds, x: Float, y: Float) {
        if (allowInteraction) {
            touchHandler?.invoke(this, event, bounds, x, y)
        }
    }

    override fun onRender(
        canvas: Canvas,
        paint: Paint,
        shapePath: Path,
        shadowPath: Path
    ) {
        if (!render) {
            return
        }

        if (drawShadows) {
            renderShadow(canvas, paint, shadowPath)
        }

        renderShape(canvas, paint, shapePath)

        if (showStroke) {
            strokeColor?.let {
                renderStroke(canvas, paint, shapePath, it)
            }
        }
    }

    private fun renderShadow(canvas: Canvas, paint: Paint, shadowPath: Path) {
        if (shadowColor == null) {
            val color = ShadowUtility.COLOR
            this.shadowAlpha = color.alpha
            this.shadowColor = ShadowUtility.getShadowColor(color, elevation)
            this.shadowFilter = ShadowUtility.getShadowFilter(elevation)
        }
        paint.recycle()
        paint.shader = null
        paint.maskFilter = shadowFilter
        paint.color = shadowColor?.toColor() ?: ShadowUtility.DEFAULT_COLOR

        shadowPath.rewind()
        shadowPath.addShadowBounds(bounds, radii, elevation)

        canvas.drawPath(shadowPath, paint)
    }

    private fun renderShape(canvas: Canvas, paint: Paint, shapePath: Path) {
        paint.recycle()
        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        if (shader != null) {
            paint.shader = shader
        }

        shapePath.rewind()
        shapePath.addRoundRect(bounds, radii)

        canvas.drawPath(shapePath, paint)
    }

    private fun renderStroke(canvas: Canvas, paint: Paint, shapePath: Path, stroke: Color) {
        paint.recycle()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = stroke.toColor()

        canvas.drawPath(shapePath, paint)
    }

    var highlightSavedState: Pair<Float, Bounds>? = null

    fun applyHighlight(interpolator: Interpolator): AnimationEvent? {
        if (highlightSavedState == null) {
            highlightSavedState = Pair(elevation, bounds.copy())
        }
        return highlightSavedState?.let { savedState ->
            AnimationEvent(
                duration = 250,
                interpolator = interpolator,
                updateListener = { _, _, delta ->
                    val zoom = 6.dp
                    val elevate = 6.dp
                    bounds.left = savedState.second.left - (zoom * delta)
                    bounds.right = savedState.second.right + (zoom * delta)
                    bounds.top = savedState.second.top - (zoom * delta)
                    bounds.bottom = savedState.second.bottom + (zoom * delta)

                    elevation = savedState.first + (elevate * delta)
                }
            )
        }
    }

    fun removeHighlight(interpolator: Interpolator): AnimationEvent? {
        return highlightSavedState?.let { savedState ->
            AnimationEvent(
                duration = 250,
                interpolator = interpolator,
                updateListener = { _, _, offset ->
                    val zoom = 6.dp
                    val elevate = 6.dp
                    val delta = MAX_OFFSET - offset
                    bounds.left = savedState.second.left - (zoom * delta)
                    bounds.right = savedState.second.right + (zoom * delta)
                    bounds.top = savedState.second.top - (zoom * delta)
                    bounds.bottom = savedState.second.bottom + (zoom * delta)
                    elevation = savedState.first + (elevate * delta)
                }
            )
        }
    }

    private var revealAnimationState: Pair<Int, Bounds> = Pair(0, Bounds())

    override fun onPreAnimation() {
        revealAnimationState = Pair(opacity, bounds.copy())
        bounds.width = 0f
        bounds.height = 0f
        render = true
    }

    override fun onPostAnimation() {}

    override fun onAnimate(delta: Float) {
        bounds.centerX = revealAnimationState.second.centerX
        bounds.centerY = revealAnimationState.second.centerY
        bounds.width = revealAnimationState.second.width * delta
        bounds.height = revealAnimationState.second.height * delta
    }
}