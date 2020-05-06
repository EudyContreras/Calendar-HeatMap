package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeManager
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Circle
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Rectangle
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Dimension
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal class CellInterceptor(
    val infoBubble: CellInfoBubble?,
    markerRadius: Float = MIN_OFFSET,
    lineThickness: Float = MIN_OFFSET
) : RenderTarget, TouchableShape {
    var bounds: Bounds = Bounds()

    var viewPort: Dimension = Dimension(MIN_OFFSET, MIN_OFFSET)

    var markerRadius: Float = markerRadius
        set(value) {
            field = value
            marker.radius = value
        }

    var lineThickness: Float = lineThickness
        set(value) {
            field = value
            lineLeft.height = value
            lineRight.height = value
            lineTop.width = value
            lineBottom.width = value
        }

    var lineColor: MutableColor
        get() = lineLeft.color
        set(value) {
            lineLeft.color = value
            lineRight.color = value
            lineTop.color = value
            lineBottom.color = value
        }

    var markerFillColor: MutableColor
        get() = marker.color
        set(value) {
            marker.color = MutableColor(value).updateAlpha(MARKER_FILL_ALPHA)
        }

    var markerStrokeColor: MutableColor
        get() = marker.color
        set(value) {
            marker.strokeColor = MutableColor(value).updateAlpha(MARKER_STROKE_FILL_ALPHA)
        }

    var elevation: Float = MIN_OFFSET
        set(value) {
            field = value
            lineLeft.elevation = value
            lineRight.elevation = value
            lineTop.elevation = value
            lineBottom.elevation = value
            marker.elevation = value
        }

    var allowIntercept: Boolean = true
        get() = field && visible
        set(value) {
            shouldRender = value
            visible = value
        }

    var shouldRender: Boolean = false
        private set(value) {
            field = value
            if (showHorizontalLine) {
                lineLeft.render = value
                lineRight.render = value
            } else {
                lineLeft.render = false
                lineRight.render = false
            }
            if (showVerticalLine) {
                lineTop.render = value
                lineBottom.render = value
            } else {
                lineTop.render = false
                lineBottom.render = false
            }
            if (showTopLine) {
                lineTop.render = value
            } else {
                lineTop.render = false
            }
            if (showBottomLine) {
                lineBottom.render = value
            } else {
                lineBottom.render = false
            }
            marker.render = value
        }

    var visible: Boolean = false
        set(value) {
            field = value
            shouldRender = false
        }

    var showTopLine: Boolean = true
        set(value) {
            field = value
            lineTop.render = value
        }

    var showBottomLine: Boolean = true
        set(value) {
            field = value
            lineBottom.render = value
        }

    var showVerticalLine: Boolean = true
        set(value) {
            field = value
            lineTop.render = value
            lineBottom.render = value
        }

    var showHorizontalLine: Boolean = true
        set(value) {
            field = value
            lineLeft.render = value
            lineRight.render = value
        }

    private fun setPositionX(value: Float, minX: Float, maxX: Float) {
        val section = (viewPort.width / 2)

        val shift = mapRange(value - (minX + section), -section, section, -(shiftOffsetX * 3f), shiftOffsetX)

        marker.centerX = (value + shift)

        if (marker.centerX < (minX + marker.radius)) {
            marker.centerX = (minX + marker.radius)
        } else if (marker.centerX > (maxX - marker.radius)) {
            marker.centerX = (maxX - marker.radius)
        }

        lineTop.bounds.left = (marker.centerX - (lineTop.width / 2))
        lineTop.bounds.right = lineTop.bounds.left + lineThickness

        lineBottom.bounds.left = lineTop.bounds.left
        lineBottom.bounds.right = lineBottom.bounds.left + lineThickness

        lineLeft.bounds.right = (marker.centerX - marker.radius)
        lineLeft.bounds.left = bounds.left

        lineRight.bounds.left = (marker.centerX + marker.radius)
        lineRight.bounds.right = bounds.right
    }

    private fun setPositionY(value: Float,  minY: Float, maxY: Float) {
        val section = bounds.height / 2

        val shift = mapRange(value - section, -section, bounds.height, -(shiftOffsetY * 3), shiftOffsetY / 3)

        marker.centerY = (value + shift)

        if (marker.centerY < (bounds.top + marker.radius)) {
            marker.centerY = (bounds.top + marker.radius)
        } else if (marker.centerY > (bounds.bottom - marker.radius)) {
            marker.centerY = (bounds.bottom - marker.radius)
        }

        lineTop.bounds.y = bounds.top
        lineTop.bounds.bottom = (marker.centerY - marker.radius)

        lineBottom.bounds.top = (marker.centerY + marker.radius)
        lineBottom.bounds.bottom = bounds.bottom

        lineLeft.bounds.top = (marker.centerY - (lineThickness / 2))
        lineLeft.bounds.bottom = lineLeft.bounds.top + lineThickness

        lineRight.bounds.top = (marker.centerY - (lineThickness / 2))
        lineRight.bounds.bottom = lineRight.bounds.top + lineThickness
    }

    var shiftOffsetX: Float = Float.MAX_VALUE
        get() = if (field == Float.MAX_VALUE) (marker.radius) else field

    var shiftOffsetY: Float = Float.MAX_VALUE
        get() = if (field == Float.MAX_VALUE) (marker.radius) else field

    override var hovered: Boolean = false

    override var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)? = null

    private var lineLeft: DrawableShape = Rectangle()
    private var lineRight: DrawableShape = Rectangle()
    private var lineTop: DrawableShape = Rectangle()
    private var lineBottom: DrawableShape = Rectangle()

    private var marker: Circle = Circle()

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)

        lineTop.y = bounds.y
        lineLeft.x = bounds.x

        marker.showStroke = true
        marker.strokeWidth = lineThickness
        marker.radius = markerRadius

        lineRight.render = true
        lineLeft.render = true
        lineBottom.render = true
        lineTop.render = true
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        if (shouldRender) {
            lineLeft.onRender(canvas, paint, shapePath, shadowPath)
            lineRight.onRender(canvas, paint, shapePath, shadowPath)
            lineTop.onRender(canvas, paint, shapePath, shadowPath)
            lineBottom.onRender(canvas, paint, shapePath, shadowPath)
            marker.onRender(canvas, paint, shapePath, shadowPath)
        }
    }

    override fun onLongPressed(
        event: MotionEvent,
        x: Float,
        y: Float,
        viewBounds: Rect,
        shapeManager: ShapeManager
    ) {
        if (!allowIntercept)
            return
        if (!shouldRender && visible) {
            shouldRender = true
            applyPositions(viewBounds, x, y, shapeManager, MotionEvent.ACTION_BUTTON_PRESS)
        }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, viewBounds: Rect, shapeManager: ShapeManager) {
        if (!allowIntercept || !shouldRender)
            return

        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                if (shouldRender && visible) {
                    shouldRender = false
                }
                hovered = false
            }
        }
        applyPositions(viewBounds, x, y, shapeManager, event.action)
    }

    private fun applyPositions(
        viewBounds: Rect,
        x: Float,
        y: Float,
        shapeManager: ShapeManager,
        eventAction: Int
    ) {
        val offsetx = mapRange(viewBounds.left.toFloat(), MIN_OFFSET, -bounds.left, MIN_OFFSET, bounds.left)
        val offsetY = mapRange(viewBounds.top.toFloat(), MIN_OFFSET, -bounds.top, MIN_OFFSET, bounds.top)

        val minX = (bounds.left - viewBounds.left) - offsetx
        val maxX = (bounds.left + viewPort.width) - viewBounds.left

        val minY = (bounds.top - viewBounds.top) - offsetY
        val maxY = (bounds.top + viewPort.height) - viewBounds.top

        setPositionX(x, minX, maxX)
        setPositionY(y, minY, maxY)

        infoBubble?.offsetY = viewBounds.top.toFloat()
        infoBubble?.offsetX = viewBounds.left.toFloat()

        infoBubble?.onInterception(
            x = marker.centerX,
            y = marker.centerY,
            minInX = minX,
            maxInX = maxX
        )

        shapeManager.delegateTouchEvent(
            eventAction = eventAction,
            bounds = marker.bounds,
            x = marker.centerX,
            y = marker.centerY,
            minX = minX,
            maxX = maxX,
            minY = minY,
            maxY = maxY,
            caller = this
        )
    }

    companion object {
        const val MARKER_FILL_ALPHA = 0.35f
        const val MARKER_STROKE_FILL_ALPHA = 0.8f
    }
}