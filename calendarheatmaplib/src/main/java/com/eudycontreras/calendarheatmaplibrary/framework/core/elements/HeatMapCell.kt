package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
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
import kotlin.math.abs

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class HeatMapCell(
    var rowIndex: Int,
    var colIndex: Int
) : Rectangle(), TouchConsumer, Animateable {

    var hovered: Boolean = false

    var isHighlighting = false

    var cellText: Text? = null

    private var allowInteraction: Boolean = true

    private val interpolatorIn = DecelerateInterpolator()
    private val interpolatorOut = DecelerateInterpolator()

    private var highlightSavedState: Triple<Float, Bounds, MutableColor>? = null
    private var highlightSavedStateText: Pair<Float, Bounds>? = null
    private var revealAnimationState: Triple<Int, Bounds, Float?> = Triple(0, Bounds(), null)

    override var touchHandler: ((TouchConsumer, MotionEvent, Bounds, Float, Float) -> Unit)? = null

    override var render: Boolean
        get() = super.render
        set(value) {
            super.render = value
            cellText?.render = value
        }

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
        super.onRender(canvas, paint, shapePath, shadowPath)
        cellText?.render = render
        cellText?.onRender(canvas, paint, shapePath, shadowPath)
    }

    fun applyHighlight(duration: Long): AnimationEvent? {
        if (highlightSavedState == null) {
            highlightSavedState = Triple(elevation, bounds.copy(), color.clone())
            cellText?.let {
                highlightSavedStateText = Pair(it.textSize, it.bounds.copy())
            }
        }
        return highlightSavedState?.let { savedState ->
            isHighlighting = true
            AnimationEvent(
                duration = duration,
                onEnd = { isHighlighting = true },
                updateListener = { _, _, offset ->
                    val zoom = 6.dp
                    val elevate = 6.dp
                    val adjust = 0.2f
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
                    val zoom = 6.dp
                    val adjust = 0.2f
                    val delta = interpolatorOut.getInterpolation(MAX_OFFSET - offset)
                    bounds.left = savedState.second.left - abs(bounds.left - savedState.second.left) * delta
                    bounds.right = savedState.second.right + abs(bounds.right - savedState.second.right) * delta
                    bounds.top =  savedState.second.top - abs(bounds.top - savedState.second.top) * delta
                    bounds.bottom =  savedState.second.bottom + abs(bounds.bottom - savedState.second.bottom) * delta
                    color = savedState.third.adjust(MAX_OFFSET + (adjust * delta))
                    elevation =  savedState.first - abs(elevation - savedState.first) * delta
                    cellText?.let {
                        highlightSavedStateText?.let { textState ->
                            it.textSize = textState.first + (zoom * delta)
                        }
                    }
                }
            )
        }
    }

    fun moveTo(rowIndex: Int, colIndex: Int, shapes: Array<Array<HeatMapCell>>) {
        val lastInRender = shapes[rowIndex][colIndex]

        lastInRender.rowIndex = rowIndex
        lastInRender.colIndex = colIndex

        shapes[rowIndex][colIndex] = this

        shapes[this.rowIndex][this.colIndex] = lastInRender

        this.rowIndex = rowIndex
        this.colIndex = colIndex
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
}