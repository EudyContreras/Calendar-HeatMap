package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.animations.AnimationEvent
import com.eudycontreras.calendarheatmaplibrary.common.Animateable
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Rectangle
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Text
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal class HeatMapCell(
    val cellGap: Float
) : Rectangle(), TouchConsumer, Animateable {

    var hovered: Boolean = false

    var isHighlighting = false

    var cellText: Text? = null

    private var allowInteraction: Boolean = true

    private val interpolatorIn = OvershootInterpolator()
    private val interpolatorOut = AccelerateInterpolator()

    private var highlightSavedState: Triple<Float, Bounds, MutableColor>? = null
    private var highlightSavedStateText: Pair<Float, MutableColor>? = null
    private var revealAnimationState: Triple<Int, Bounds, Float?> = Triple(0, Bounds(), null)

    override var touchHandler: ((TouchConsumer, Int, Bounds, Float, Float, Float, Float, Float, Float) -> Unit)? = null

    override var render: Boolean
        get() = super.render
        set(value) {
            super.render = value
            cellText?.render = value
        }

    override fun onTouch(eventAction: Int, bounds: Bounds, x: Float, y: Float, minX: Float, maxX: Float, minY: Float, maxY: Float) {
        if (allowInteraction) {
            touchHandler?.invoke(this, eventAction, bounds, x, y, minX, maxX, minY, maxY)
        }
    }

    override fun onRender(
        canvas: Canvas,
        paint: Paint,
        shapePath: Path,
        shadowPath: Path
    ) {
        super.onRender(canvas, paint, shapePath, shadowPath)
        cellText?.render = render
        cellText?.onRender(canvas, paint, shapePath, shadowPath)
    }

    fun applyHighlight(duration: Long): AnimationEvent? {
        if (highlightSavedState == null) {
            highlightSavedState = Triple(elevation, bounds.copy(), color.clone())
            cellText?.let {
                highlightSavedStateText = Pair(it.textSize, it.textColor)
            }
        }
        return highlightSavedState?.let { savedState ->
            isHighlighting = true
            AnimationEvent(
                duration = duration,
                onEnd = {
                    isHighlighting = true
                },
                updateListener = { _, _, offset ->
                    val elevate = DEPTH_AMOUNT
                    val adjust = COLOR_AMOUNT
                    val zoom = cellGap * ZOOM_AMOUNT
                    val delta = interpolatorIn.getInterpolation(offset)
                    bounds.left = savedState.second.left - (zoom * delta)
                    bounds.right = savedState.second.right + (zoom * delta)
                    bounds.top = savedState.second.top - (zoom * delta)
                    bounds.bottom = savedState.second.bottom + (zoom * delta)
                    color = savedState.third.adjust(MAX_OFFSET + (adjust * delta))
                    elevation = savedState.first + (elevate * delta)

                    cellText?.let {
                        highlightSavedStateText?.let { textState ->
                            it.textSize = textState.first + (zoom * delta)
                        }
                    }
                }
            )
        }
    }

    fun removeHighlight(duration: Long): AnimationEvent? {
        return highlightSavedState?.let { savedState ->
            AnimationEvent(
                duration = duration,
                onEnd = { isHighlighting = false },
                updateListener = { _, _, offset ->
                    val elevate = DEPTH_AMOUNT
                    val adjust = COLOR_AMOUNT
                    val zoom = cellGap * ZOOM_AMOUNT
                    val delta = interpolatorOut.getInterpolation(MAX_OFFSET - offset)
                    bounds.left = savedState.second.left - (zoom * delta)
                    bounds.right = savedState.second.right + (zoom * delta)
                    bounds.top = savedState.second.top - (zoom * delta)
                    bounds.bottom = savedState.second.bottom + (zoom * delta)
                    color = savedState.third.adjust(MAX_OFFSET + (adjust * delta))
                    elevation = savedState.first + (elevate * delta)

                    cellText?.let {
                        highlightSavedStateText?.let { textState ->
                            it.textSize = textState.first + (zoom * delta)
                        }
                    }
                }
            )
        }
    }

    fun isInViewport(viewport: Bounds): Boolean {
        return bounds.intercepts(viewport)
    }

    override fun onPreAnimation() {
        revealAnimationState = Triple(opacity, bounds.copy(), cellText?.textSize)
        bounds.width = MIN_OFFSET
        bounds.height = MIN_OFFSET
        render = true
        cellText?.let {
            it.textSize = MIN_OFFSET
            it.render = true
        }
    }

    override fun onPostAnimation() {}

    override fun onAnimate(delta: Float) {
        bounds.centerX = revealAnimationState.second.centerX
        bounds.centerY = revealAnimationState.second.centerY
        bounds.width = revealAnimationState.second.width * delta
        bounds.height = revealAnimationState.second.height * delta
        cellText?.textSize =  (revealAnimationState.third?: MIN_OFFSET) * delta
    }

    companion object {
        const val ZOOM_AMOUNT = 1f
        val DEPTH_AMOUNT = 4.dp

        const val COLOR_AMOUNT = 0.2f
    }
}