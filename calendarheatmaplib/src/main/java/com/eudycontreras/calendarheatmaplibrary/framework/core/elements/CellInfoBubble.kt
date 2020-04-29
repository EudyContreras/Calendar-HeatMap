package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DropShadow
import com.eudycontreras.calendarheatmaplibrary.framework.data.WeekDay
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal class CellInfoBubble(
    val bounds: Bounds,
    val topOffset: Float,
    val sideOffset: Float,
    val bubbleLayout: BubbleLayout<WeekDay>
): RenderTarget {
    var offsetX: Float = MIN_OFFSET
    var offsetY: Float = MIN_OFFSET

    var isRevealed: Boolean = false
    var isRevealing: Boolean = false

    private val shadow: DropShadow by lazy {
        DropShadow(bubbleLayout.elevation).also {
            it.bounds = Bounds(
                left = bubbleLayout.x,
                top = bubbleLayout.y,
                right = bubbleLayout.x + bubbleLayout.width,
                bottom = bubbleLayout.y + bubbleLayout.height
            )
            it.elevation = bubbleLayout.elevation
            it.render = false
        }
    }

    fun setInterceptedData(weekDay: WeekDay) {
        bubbleLayout.onDataIntercepted(weekDay)
    }

    fun onInterception(x: Float, y: Float, minInX: Float, maxInX: Float): Float {
        var positionY = y - (bubbleLayout.height + topOffset)

        if (positionY < (bounds.top - offsetY)) {
            positionY = (bounds.top - offsetY)
        }

        val minX = bounds.left + sideOffset

        val maxX = ((bounds.left + bubbleLayout.boundsWidth) - bubbleLayout.width) - sideOffset

        val interpolateX = mapRange(x, minInX, maxInX, minX, maxX)

        bubbleLayout.onMove(interpolateX, positionY + offsetY)

        shadow.scaleX = bubbleLayout.scaleX
        shadow.scaleY = bubbleLayout.scaleY

        shadow.bounds.x = interpolateX - offsetX
        shadow.bounds.y = positionY
        shadow.width = bubbleLayout.width
        shadow.height = bubbleLayout.height

        return positionY
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        shadow.onRender(canvas, paint, shapePath, shadowPath)
    }

    fun bringToFront() {
        val offset = MAX_OFFSET
        bubbleLayout.toFront(offset, bubbleLayout.width * 0.5f, bubbleLayout.height,250)
        isRevealing = false
    }

    fun revealInfoBubble(x: Float, y: Float, minInX: Float, maxInX: Float) {
        val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.width)
        val pivotY = (bubbleLayout.height + topOffset)

        val pivot = Coordinate(pivotX, pivotY)

        isRevealing = true
        val offset = MAX_OFFSET
        shadow.render = true
        bubbleLayout.reveal(offset, pivot,250)
        isRevealed = true
        isRevealing = false
    }

    fun concealInfoBubble(x: Float, y: Float, minInX: Float, maxInX: Float) {
        val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.width)
        val pivotY = (bubbleLayout.height + topOffset)

        val pivot = Coordinate(pivotX, pivotY)

        isRevealed = false
        val offset = MIN_OFFSET
        shadow.render = false
        bubbleLayout.conceal(offset, pivot,250)
        isRevealing = false
    }
}