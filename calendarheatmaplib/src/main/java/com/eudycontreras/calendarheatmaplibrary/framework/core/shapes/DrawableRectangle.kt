package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.animations.HeatMapAnimation
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

internal class DrawableRectangle : DrawableShape(), TouchConsumer, HeatMapAnimation.Animateable {

    var hovered: Boolean = false

    private var shadowFilter: BlurMaskFilter? = null

    private var lastColor: MutableColor? = null

    private var lastBounds: Bounds? = null

    private var allowInteraction: Boolean = true

    override var touchHandler: ((TouchConsumer, MotionEvent, Float, Float) -> Unit)? = null

    override fun onTouch(event: MotionEvent, x: Float, y: Float) {
        if (allowInteraction) {
            touchHandler?.invoke(this, event, x, y)
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

    private var savedState: Pair<Int, Bounds> = Pair(0, Bounds())

    //TODO refactor highlight and redefine what highlight is
    fun applyHighlight() {
        if (lastColor == null) {
            lastColor = color.clone()
        }
        if (lastBounds == null) {
            lastBounds = bounds.copy()
        }
        elevation = 8.dp

        lastBounds?.let {
            val zoom = 5.dp
            bounds.left = it.left - zoom
            bounds.right = it.right + zoom
            bounds.top = it.top - zoom
            bounds.bottom = it.bottom + zoom
        }
    }

    fun removeHighlight() {
        showStroke = false
        strokeWidth = 0f
        elevation = 0f
        color = lastColor?.clone() ?: return
        bounds = lastBounds?.copy() ?: return
    }

    override fun onPreAnimation() {
        savedState = Pair(opacity, bounds.copy())
        bounds.width = 0f
        bounds.height = 0f
        render = true
    }

    override fun onPostAnimation() {}

    override fun onAnimate(delta: Float) {
        bounds.centerX = savedState.second.centerX
        bounds.centerY = savedState.second.centerY
        bounds.width = savedState.second.width * delta
        bounds.height = savedState.second.height * delta
    }
}